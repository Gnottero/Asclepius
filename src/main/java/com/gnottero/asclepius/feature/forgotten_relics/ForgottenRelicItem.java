package com.gnottero.asclepius.feature.forgotten_relics;

import com.gnottero.asclepius.registry.AsclepiusComponents;
import com.gnottero.asclepius.registry.AsclepiusTags;
import com.gnottero.asclepius.utils.PlayerXpUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Base for the repair-then-apply relic state machine: a relic starts un-repaired,
 * rolls a random rarity and repair-material list on first use, gets repaired either
 * by feeding those materials (shift-click stacking) or by spending XP levels
 * directly, and once repaired can be applied to another item (shift-click) to
 * permanently socket its effect. The whole interaction model runs through the
 * stack-click override hooks ({@link #overrideOtherStackedOnMe}/
 * {@link #overrideStackedOnOther}) — dragging one item stack onto another in an
 * inventory slot — rather than a dedicated GUI. Concrete subclasses only need to
 * implement {@link #applyAttribute}; see {@link AttributeRelicItem} for the one
 * existing example.
 */
public abstract class ForgottenRelicItem extends Item {

    private static final int MIN_ITEMS_PER_SLOT = 10;
    private final @Nullable DataComponentType<?> requiredComponent;

    public ForgottenRelicItem(Properties settings, @Nullable DataComponentType<?> requiredComponent) {
        super(settings.stacksTo(1).component(AsclepiusComponents.REPAIRED, false));
        this.requiredComponent = requiredComponent;
    }

    // ── Abstract hooks ────────────────────────────────────────────────────────

    /**
     * Allows to handle custom logic upon the insertion of the Forgotten Relic to
     * the item
     * 
     * @param self  The Forgotten Relic ItemStack being applied (its rolled rarity
     *              scales the effect)
     * @param other The ItemStack upon which the Forgotten Relic is applied
     */
    protected abstract void applyAttribute(ItemStack self, ItemStack other);

    /**
     * Allows to add more conditions to the applicability of a Forgotten Relic to an
     * Item
     * 
     * @param other The ItemStack upon which the Forgotten Relic is applied
     * @return A boolean relative to the applicability of the Forgotten Relic
     */
    protected boolean satisfiesRelicConditions(ItemStack other) {
        return true;
    }

    /**
     * Define the sound to play whenever the Forgotten Relic is applied to the item
     * 
     * @return The SoundEvent to play when the Forgotten Relic is applied to the
     *         item
     */
    protected SoundEvent getApplySound() {
        return SoundEvents.RESPAWN_ANCHOR_CHARGE;
    }

    // ── Shared logic ──────────────────────────────────────────────────────────

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context,
            @NonNull TooltipDisplay tooltipDisplay,
            @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {

        ForgottenRelicsRarity rarity = stack.get(AsclepiusComponents.RELIC_RARITY);
        if (rarity != null) {
            tooltipAdder
                    .accept(Component
                            .translatable("item.asclepius.forgotten_relics.rarity.begin",
                                    Component.translatable(rarity.getDisplayName()))
                            .withStyle(rarity.getColor()));
        }

        if (!isRepaired(stack)) {
            tooltipAdder.accept(Component.translatable("item.asclepius.forgotten_relics.needs_repair.line_1")
                    .withStyle(ChatFormatting.GRAY));
            tooltipAdder.accept(Component.translatable("item.asclepius.forgotten_relics.needs_repair.line_2")
                    .withStyle(ChatFormatting.GRAY));
            tooltipAdder.accept(Component.empty());

            var materials = stack.getOrDefault(AsclepiusComponents.REPAIR_MATERIALS, List.<ItemStackTemplate>of());
            if (!materials.isEmpty()) {
                tooltipAdder.accept(Component.translatable("item.asclepius.forgotten_relics.needs_repair.line_3")
                        .withStyle(ChatFormatting.GRAY));

                materials.stream()
                        .map(material -> Component.literal("- ")
                                .withStyle(ChatFormatting.GRAY)
                                .append(Component.translatable("tooltip.asclepius.forgotten_relic.material_entry",
                                        material.count(),
                                        material.create().getHoverName())))
                        .forEach(tooltipAdder);

                tooltipAdder.accept(Component.empty());
            }
            if (rarity != null) {
                tooltipAdder.accept(Component
                        .translatable("item.asclepius.forgotten_relics.required_levels", rarity.getRequiredLevels())
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    // overrideOtherStackedOnMe / overrideStackedOnOther implement the relic's
    // whole interaction model — feeding repair materials and applying the relic
    // to a target item — via the stack-click override hooks (dragging one stack
    // onto another in an inventory slot) instead of a dedicated GUI.
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction,
            Player player, SlotAccess carriedItem) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer))
            return false;
        if (isRepaired(self) || clickAction != ClickAction.SECONDARY || !slot.allowModification(player))
            return false;

        List<ItemStackTemplate> repairMaterials = getRepairMaterials(self);
        Item otherItem = other.getItem();
        var match = find(repairMaterials, otherItem);
        if (match.isEmpty())
            return false;

        int requiredAmount = match.get().count();
        int toAdd = Math.min(requiredAmount, other.getCount());
        int remaining = requiredAmount - toAdd;

        List<ItemStackTemplate> updatedMaterials = remaining == 0
                ? remove(repairMaterials, otherItem)
                : setCount(repairMaterials, otherItem, remaining);

        other.shrink(toAdd);

        self.set(AsclepiusComponents.REPAIR_MATERIALS, updatedMaterials);

        return true;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer))
            return false;
        if (!isRepaired(self) || clickAction != ClickAction.SECONDARY || !slot.allowModification(player))
            return false;

        ItemStack other = slot.getItem();
        if (!canApplyOnItem(other) || !satisfiesRelicConditions(other))
            return false;

        applyAttribute(self, other);
        addToSockets(self, other);
        addRelicLore(self, other);

        self.shrink(1);
        player.level().playSound(null, player.blockPosition(), getApplySound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide() || !(player instanceof ServerPlayer))
            return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);

        if (isRepaired(stack))
            return InteractionResult.PASS;

        if (!hasRepairMaterialsList(stack)) {
            ForgottenRelicsRarity rarity = stack.get(AsclepiusComponents.RELIC_RARITY);
            if (rarity == null) {
                rarity = ForgottenRelicsRarity.roll(player.experienceLevel, level.getRandom());
                stack.set(AsclepiusComponents.RELIC_RARITY, rarity);
            }

            stack.set(AsclepiusComponents.REPAIR_MATERIALS, generateRepairMaterials(level, level.getRandom(), rarity));
            return InteractionResult.PASS;
        }

        if (getRepairMaterials(stack).size() == 0) {
            return finishRepairWithLevels(stack, player);
        }

        return InteractionResult.PASS;
    }

    // Allow the player to finish the repairment of the relic with the required
    // levels
    private InteractionResult finishRepairWithLevels(ItemStack stack, Player player) {
        ForgottenRelicsRarity rarity = stack.getOrDefault(AsclepiusComponents.RELIC_RARITY,
                ForgottenRelicsRarity.purified);
        int requiredXp = PlayerXpUtils.getTotalXpForLevel(rarity.getRequiredLevels());
        boolean isCreative = player.isCreative();

        if (!isCreative && PlayerXpUtils.getTotalXp(player) < requiredXp) {
            player.sendSystemMessage(Component.translatable("item.asclepius.forgotten_relics.not_enough_xp_to_repair",
                    rarity.getRequiredLevels()));
            return InteractionResult.PASS;
        }

        stack.set(AsclepiusComponents.REPAIRED, true);
        stack.remove(AsclepiusComponents.REPAIR_MATERIALS);
        if (!isCreative)
            player.giveExperiencePoints(-requiredXp);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS,
                1.0f, 1.0f);
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isRepaired(stack);
    }

    public boolean isRepaired(ItemStack stack) {
        return stack.getOrDefault(AsclepiusComponents.REPAIRED, false);
    }

    public static boolean hasRepairMaterialsList(ItemStack stack) {
        return stack.has(AsclepiusComponents.REPAIR_MATERIALS);
    }

    public List<ItemStackTemplate> getRepairMaterials(ItemStack stack) {
        return stack.getOrDefault(AsclepiusComponents.REPAIR_MATERIALS, List.of());
    }

    public static List<ItemStackTemplate> generateRepairMaterials(Level level, RandomSource random,
            ForgottenRelicsRarity rarity) {
        HolderLookup.RegistryLookup<Item> registry = level.registryAccess().lookupOrThrow(Registries.ITEM);
        List<Item> massPool = registry.getOrThrow(AsclepiusTags.FORGOTTEN_RELICS_MASS).stream().map(Holder::value)
                .toList();
        List<Item> valuePool = registry.getOrThrow(AsclepiusTags.FORGOTTEN_RELICS_VALUE).stream().map(Holder::value)
                .toList();

        if (massPool.isEmpty() || valuePool.isEmpty())
            return List.of();

        // Weight mass-tag items 2x over value-tag items by duplicating massPool's
        // entries in the pool, rather than using an explicit weight table — biases
        // selection toward "common" materials without a second data structure.
        List<Item> combinedPool = new ArrayList<>();
        combinedPool.addAll(valuePool);
        combinedPool.addAll(massPool);
        combinedPool.addAll(massPool);

        // NOTE: uses the JVM's default Random via Collections.shuffle, not the
        // RandomSource passed into this method (that one is only used below in
        // rollBaseQuantity) — the item selection is therefore not seed-deterministic
        // even though quantity rolling is. Left as-is: fixing it would change loot
        // randomness/determinism, which is a gameplay decision outside this pass.
        Collections.shuffle(combinedPool);

        // Use a Set for O(1) lookups and collect unique items
        Set<Item> selectedItems = new HashSet<>();
        List<Item> chosenItems = new ArrayList<>();

        // Try to get 4 unique items from the combined pool
        for (Item item : combinedPool) {
            if (!selectedItems.contains(item)) {
                selectedItems.add(item);
                chosenItems.add(item);
                if (chosenItems.size() == 4)
                    break;
            }
        }

        List<ItemStackTemplate> result = new ArrayList<>();

        float qtyMultiplier = rarity.getMultiplier()/1.2f;
        for (Item item : chosenItems) {
            int base = rollBaseQuantity(random, item.getDefaultMaxStackSize());
            int qty = Math.clamp(Math.round(base * qtyMultiplier), 1, 99);

            DataComponentPatch patch = DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, qty).build();
            result.add(new ItemStackTemplate(item.builtInRegistryHolder(), qty, patch));
        }
        return result;
    }

    // Rolls a base quantity in [min(MIN_ITEMS_PER_SLOT, maxStack), maxStack] —
    // items with a small max
    // stack size (e.g. Totem of Undying) would otherwise make random.nextInt's
    // bounds invalid.
    private static int rollBaseQuantity(RandomSource random, int maxStack) {
        int lower = Math.min(MIN_ITEMS_PER_SLOT, maxStack);
        return lower >= maxStack ? maxStack : random.nextInt(lower, maxStack + 1);
    }

    public boolean canApplyOnItem(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        if (requiredComponent != null && !stack.has(requiredComponent))
            return false;
        int maxSockets = stack.getOrDefault(AsclepiusComponents.MAX_SOCKETS, 0);
        int usedSockets = stack.getOrDefault(AsclepiusComponents.SOCKETS, List.of()).size();
        return maxSockets > 0 && usedSockets < maxSockets;
    }

    private void addToSockets(ItemStack self, ItemStack other) {
        var sockets = new ArrayList<>(other.getOrDefault(AsclepiusComponents.SOCKETS, List.of()));
        sockets.add(new ItemStackTemplate(self.getItem()));
        other.set(AsclepiusComponents.SOCKETS, sockets);
    }

    private void addRelicLore(ItemStack self, ItemStack other) {
        var currentLore = other.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        var lines = new ArrayList<>(currentLore.lines());
        ForgottenRelicsRarity rarity = self.get(AsclepiusComponents.RELIC_RARITY);

        var sockets = other.getOrDefault(AsclepiusComponents.SOCKETS, List.of());
        // This method runs after addToSockets() already appended the new socket,
        // so size() == 1 here means "this was the first relic ever applied", not
        // "the item currently has exactly one socket" — it gates printing the
        // "Sockets:" lore header exactly once.
        if (sockets.size() == 1) {
            if (!lines.isEmpty())
                lines.add(Component.empty());
            lines.add(Component.translatable("item.asclepius.sockets_title").withStyle(ChatFormatting.GRAY)
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)));
        }

        // Safe to derive from the registry key here (rather than requiring every
        // subclass to pass its own id) because addRelicLore only ever runs from a
        // real gameplay interaction (overrideStackedOnOther), always well after
        // AsclepiusItems.registerAll() has registered this item.
        Identifier spriteId = BuiltInRegistries.ITEM.getKey(this).withPrefix("item/");
        var spriteComponent = Component.object(new AtlasSprite(AtlasIds.ITEMS, spriteId))
                .withStyle(style -> style.withColor(ChatFormatting.WHITE).withItalic(false));

        var relicLine = spriteComponent
                .append(Component.literal(" ").withStyle(Style.EMPTY))
                .append(Component.translatable(self.getItemName().getString()).withStyle(rarity.getColor()));

        lines.add(relicLine);
        other.set(DataComponents.LORE, new ItemLore(lines));
    }

    public @Nullable DataComponentType<?> getRequiredComponent() {
        return requiredComponent;
    }

    // Find the matching template
    Optional<ItemStackTemplate> find(List<ItemStackTemplate> materials, Item item) {
        return materials.stream()
                .filter(t -> t.create().is(item))
                .findFirst();
    }

    // Edit — returns a new list with the updated count (lists are immutable in
    // components)
    List<ItemStackTemplate> setCount(List<ItemStackTemplate> materials, Item item, int newCount) {
        return materials.stream()
                .map(t -> t.create().is(item) ? t.withCount(newCount) : t)
                .toList();
    }

    // Remove — returns a new list without the entry
    List<ItemStackTemplate> remove(List<ItemStackTemplate> materials, Item item) {
        return materials.stream()
                .filter(t -> !t.create().is(item))
                .toList();
    }
}
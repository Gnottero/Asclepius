package com.gnottero.asclepius.item.forgotten_relics;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.registry.AsclepiusComponents;
import com.gnottero.asclepius.registry.AsclepiusTags;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class ForgottenRelicItem extends SimplePolymerItem {

    private static final int MIN_ITEMS_PER_SLOT = 10;
    private final String identifier;
    private final @Nullable DataComponentType<?> requiredComponent;

    public ForgottenRelicItem(Properties settings, String identifier, @Nullable DataComponentType<?> requiredComponent) {
        super(settings.stacksTo(1).component(AsclepiusComponents.REPAIRED, false));
        this.identifier = identifier;
        this.requiredComponent = requiredComponent;
    }

    // ── Abstract hooks ────────────────────────────────────────────────────────

    /**
     * Allows to handle custom logic upon the insertion of the Forgotten Relic to the item
     * @param other     The ItemStack upon which the Forgotten Relic is applied
     */
    protected abstract void applyAttribute(ItemStack other);

    /**
     * Allows to add more conditions to the applicability of a Forgotten Relic to an Item
     * @param other     The ItemStack upon which the Forgotten Relic is applied
     * @return          A boolean relative to the applicability of the Forgotten Relic
     */
    protected boolean satisfiesRelicConditions(ItemStack other) { return true; }

    /**
     * Define the sound to play whenever the Forgotten Relic is applied to the item
     * @return          The SoundEvent to play when the Forgotten Relic is applied to the item
     */
    protected SoundEvent getApplySound() {
        return SoundEvents.RESPAWN_ANCHOR_CHARGE;
    }

    // ── Shared logic ──────────────────────────────────────────────────────────

    @Override
    public @NonNull Identifier getPolymerItemModel(ItemStack stack, @Nullable PacketContext context, HolderLookup.Provider lookup) {
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, identifier);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay tooltipDisplay,
                                @NonNull Consumer<Component> tooltipAdder, @NonNull TooltipFlag flag) {

        if (!isRepaired(stack)) {
            tooltipAdder.accept(Component.translatable("item.asclepius.forgotten_relic.needs_repair.line_1").withStyle(ChatFormatting.GRAY));
            tooltipAdder.accept(Component.translatable("item.asclepius.forgotten_relic.needs_repair.line_2").withStyle(ChatFormatting.GRAY));
            tooltipAdder.accept(Component.empty());

            var materials = stack.getOrDefault(AsclepiusComponents.REPAIR_MATERIALS, List.<ItemStackTemplate>of());
            if (!materials.isEmpty()) {
                tooltipAdder.accept(Component.translatable("item.asclepius.forgotten_relic.needs_repair.line_3")
                        .withStyle(ChatFormatting.GRAY));

                materials.stream()
                        .map(material -> Component.literal("- ")
                                .withStyle(ChatFormatting.GRAY)
                                .append(Component.translatable("tooltip.asclepius.forgotten_relic.material_entry",
                                        material.count(),
                                        material.create().getHoverName()))
                        )
                        .forEach(tooltipAdder);
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer)) return false;
        if (isRepaired(self) || clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        List<ItemStackTemplate> repairMaterials = getRepairMaterials(self);
        Item otherItem = other.getItem();
        var match = find(repairMaterials, otherItem);
        if (match.isEmpty()) return false;

        int requiredAmount = match.get().count();
        int toAdd = Math.min(requiredAmount, other.getCount());
        int remaining = requiredAmount - toAdd;

        List<ItemStackTemplate> updatedMaterials = remaining == 0
                ? remove(repairMaterials, otherItem)
                : setCount(repairMaterials, otherItem, remaining);

        other.shrink(toAdd);

        if (updatedMaterials.isEmpty()) {
            self.set(AsclepiusComponents.REPAIRED, true);
            self.remove(AsclepiusComponents.REPAIR_MATERIALS);
        } else {
            self.set(AsclepiusComponents.REPAIR_MATERIALS, updatedMaterials);
        }

        return true;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack self, Slot slot, ClickAction clickAction, Player player) {
        if (player.level().isClientSide() || !(player instanceof ServerPlayer)) return false;
        if (!isRepaired(self) || clickAction != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        ItemStack other = slot.getItem();
        if (!canApplyOnItem(other) || !satisfiesRelicConditions(other)) return false;

        applyAttribute(other);
        addToSockets(self, other);
        addRelicLore(self, other);

        self.shrink(1);
        player.level().playSound(null, player.blockPosition(), getApplySound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide() || !(player instanceof ServerPlayer)) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);

        if (isRepaired(stack)) return InteractionResult.PASS;
        if (hasRepairMaterialsList(stack)) return InteractionResult.PASS;

        stack.set(AsclepiusComponents.REPAIR_MATERIALS, generateRepairMaterials(level, ItemTags.LOGS, level.getRandom()));

        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(AsclepiusComponents.REPAIRED, false);
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

    public static List<ItemStackTemplate> generateRepairMaterials(Level level, TagKey<Item> tag, RandomSource random) {
        HolderLookup.RegistryLookup<Item> registry = level.registryAccess().lookupOrThrow(Registries.ITEM);
        List<Item> massPool = registry.getOrThrow(AsclepiusTags.FORGOTTEN_RELICS_MASS).stream().map(Holder::value).toList();
        List<Item> valuePool = registry.getOrThrow(AsclepiusTags.FORGOTTEN_RELICS_VALUE).stream().map(Holder::value).toList();

        if (massPool.isEmpty() || valuePool.isEmpty()) return List.of();

        List<Item> chosenItems = new ArrayList<>();
        chosenItems.add(valuePool.get(random.nextInt(valuePool.size())));
        chosenItems.add(massPool.get(random.nextInt(massPool.size())));
        chosenItems.add(massPool.get(random.nextInt(massPool.size())));
        chosenItems.add(random.nextBoolean() ? massPool.get(random.nextInt(massPool.size())) : valuePool.get(random.nextInt(valuePool.size())));

        List<ItemStackTemplate> result = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Item item = chosenItems.get(i);
            int maxStack = item.getDefaultMaxStackSize();
            int qty = random.nextInt(MIN_ITEMS_PER_SLOT, maxStack);
            qty = Math.max(MIN_ITEMS_PER_SLOT, qty);

            result.add(new ItemStackTemplate(item, qty));
        }
        return result;
    }

    public boolean canApplyOnItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (requiredComponent != null && !stack.has(requiredComponent)) return false;
        int maxSockets  = stack.getOrDefault(AsclepiusComponents.MAX_SOCKETS, 0);
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

        var sockets = other.getOrDefault(AsclepiusComponents.SOCKETS, List.of());
        if (!lines.isEmpty() && sockets.size() == 1) {
            lines.add(Component.empty());
            lines.add(Component.translatable("item.asclepius.sockets_title").withStyle(ChatFormatting.GRAY)
                    .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)));
        }

        Identifier spriteId = Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "item/" + identifier);
        var spriteComponent = Component.object(new AtlasSprite(AtlasIds.ITEMS, spriteId))
                .withStyle(style -> style.withColor(ChatFormatting.WHITE).withItalic(false));

        var relicLine = spriteComponent
                .append(Component.literal(" ").withStyle(Style.EMPTY))
                .append(self.getHoverName().copy().withStyle(ChatFormatting.GRAY));

        lines.add(relicLine);
        other.set(DataComponents.LORE, new ItemLore(lines));
    }

    public @Nullable DataComponentType<?> getRequiredComponent() { return requiredComponent; }

    // Find the matching template
    Optional<ItemStackTemplate> find(List<ItemStackTemplate> materials, Item item) {
        return materials.stream()
                .filter(t -> t.create().is(item))
                .findFirst();
    }

    // Edit — returns a new list with the updated count (lists are immutable in components)
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
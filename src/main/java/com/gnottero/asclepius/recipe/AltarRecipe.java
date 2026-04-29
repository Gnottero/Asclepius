package com.gnottero.asclepius.recipe;

import com.gnottero.asclepius.registry.AsclepiusRecipes;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AltarRecipe implements Recipe<AltarRecipeInput> {

    private final String group;
    private final AltarRecipeConditions conditions;
    private final IngredientWithComponents altarItem;
    private final IngredientWithComponents catalyst;
    private final ItemStackTemplate result;

    public AltarRecipe(String group, IngredientWithComponents altarItem, IngredientWithComponents catalyst, ItemStackTemplate result, AltarRecipeConditions conditions) {
        this.group = group;
        this.conditions = conditions;
        this.altarItem = altarItem;
        this.catalyst = catalyst;
        this.result = result;
    }

    @Override
    public boolean matches(AltarRecipeInput input, Level level) {
        return altarItem.test(input.altarItem()) && catalyst.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(AltarRecipeInput input) {
        ItemStack output = input.altarItem().is(result.item())
                ? input.altarItem().copyWithCount(result.count())
                : result.create().copy();

        // Apply all components except enchantments
        DataComponentPatch filtered = DataComponentPatch.builder()
                .build(); // start empty
        result.components().entrySet().stream()
                .filter(e -> e.getKey() != DataComponents.STORED_ENCHANTMENTS
                        && e.getKey() != DataComponents.ENCHANTMENTS)
                .forEach(e -> e.getValue().ifPresent(v -> applyComponent(output, e.getKey(), v)));

        // Merge enchantments on top of existing ones
        mergeEnchantments(output, DataComponents.STORED_ENCHANTMENTS);
        mergeEnchantments(output, DataComponents.ENCHANTMENTS);

        return output;
    }

    @SuppressWarnings("unchecked")
    private <T> void applyComponent(ItemStack stack, DataComponentType<?> type, Object value) {
        stack.set((DataComponentType<T>) type, (T) value);
    }

    private void mergeEnchantments(ItemStack output, DataComponentType<ItemEnchantments> type) {
        ItemEnchantments resultEnchants = result.get(type);
        ItemEnchantments existing = output.get(type);
        if (resultEnchants == null) return;
        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(
                existing != null ? existing : ItemEnchantments.EMPTY);
        resultEnchants.entrySet().forEach(e -> merged.set(e.getKey(), e.getValue()));
        output.set(type, merged.toImmutable());
    }

    @Override
    public RecipeSerializer<AltarRecipe> getSerializer() {
        return AsclepiusRecipes.ALTAR_SERIALIZER;
    }

    @Override
    public RecipeType<AltarRecipe> getType() {
        return AsclepiusRecipes.ALTAR_TYPE;
    }

    @Override
    public @Nullable RecipeBookCategory recipeBookCategory() {
        return null;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return this.group;
    }

    public AltarRecipeConditions getConditions() { return conditions; }

    public boolean checkConditions(Player player, Level level) { return conditions.check(player, level); }

    public IngredientWithComponents getBaseItem() { return altarItem; }
    public IngredientWithComponents getCatalystItem() { return catalyst; }
    public ItemStackTemplate getResultItem() { return result; }
}
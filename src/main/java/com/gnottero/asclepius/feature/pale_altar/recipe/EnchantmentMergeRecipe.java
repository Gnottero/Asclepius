package com.gnottero.asclepius.feature.pale_altar.recipe;

import com.gnottero.asclepius.registry.AsclepiusRecipes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchantmentMergeRecipe implements AltarRitualRecipe {

    @Override
    public boolean matches(AltarRecipeInput input, Level level) {
        ItemStack altarItem = input.altarItem();
        ItemStack catalyst = input.catalyst();
        if (altarItem.isEmpty() || catalyst.isEmpty()) return false;

        boolean catalystHasEnchants =
                (catalyst.has(DataComponents.ENCHANTMENTS) && !catalyst.get(DataComponents.ENCHANTMENTS).isEmpty()) ||
                (catalyst.has(DataComponents.STORED_ENCHANTMENTS) && !catalyst.get(DataComponents.STORED_ENCHANTMENTS).isEmpty());
        if (!catalystHasEnchants) return false;

        return altarItem.is(catalyst.getItem()) || catalyst.is(Items.ENCHANTED_BOOK);
    }

    public Optional<EnchantmentMerger.MergeResult> tryMerge(AltarRecipeInput input) {
        return EnchantmentMerger.tryMerge(input.altarItem(), input.catalyst());
    }

    @Override
    public ItemStack assemble(AltarRecipeInput input) {
        return tryMerge(input).map(EnchantmentMerger.MergeResult::output).orElseGet(() -> input.altarItem().copy());
    }

    @Override
    public RecipeSerializer<EnchantmentMergeRecipe> getSerializer() {
        return AsclepiusRecipes.ENCHANTMENT_MERGE_SERIALIZER;
    }

    @Override
    public RecipeType<EnchantmentMergeRecipe> getType() {
        return AsclepiusRecipes.ENCHANTMENT_MERGE_TYPE;
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
        return "";
    }

    @Override
    public boolean consumeCatalyst() { return true; }

    @Override
    public int getCatalystAmount() { return 1; }
}

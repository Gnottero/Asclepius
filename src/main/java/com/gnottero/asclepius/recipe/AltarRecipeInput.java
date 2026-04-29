package com.gnottero.asclepius.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AltarRecipeInput(ItemStack altarItem, ItemStack catalyst) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> altarItem;
            case 1 -> catalyst;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() { return 2; }
}

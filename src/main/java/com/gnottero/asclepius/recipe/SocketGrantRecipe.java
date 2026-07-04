package com.gnottero.asclepius.recipe;

import com.gnottero.asclepius.registry.AsclepiusComponents;
import com.gnottero.asclepius.registry.AsclepiusRecipes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SocketGrantRecipe implements AltarRitualRecipe {

    private final boolean consumeCatalyst;
    private final String group;
    private final IngredientWithComponents altarItem;
    private final IngredientWithComponents catalyst;
    private final int socketsGranted;
    private final int maxSockets;
    private final AltarRecipeConditions conditions;

    public SocketGrantRecipe(boolean consumeCatalyst, String group, IngredientWithComponents altarItem,
                              IngredientWithComponents catalyst, int socketsGranted, int maxSockets, AltarRecipeConditions conditions) {
        this.consumeCatalyst = consumeCatalyst;
        this.group = group;
        this.altarItem = altarItem;
        this.catalyst = catalyst;
        this.socketsGranted = socketsGranted;
        this.maxSockets = maxSockets;
        this.conditions = conditions;
    }

    @Override
    public boolean matches(AltarRecipeInput input, Level level) {
        return altarItem.test(input.altarItem()) && catalyst.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(AltarRecipeInput input) {
        ItemStack output = input.altarItem().copy();
        int currentSockets = output.getOrDefault(AsclepiusComponents.MAX_SOCKETS, 0);
        int newSockets = Math.max(currentSockets, Math.min(currentSockets + socketsGranted, maxSockets));
        output.set(AsclepiusComponents.MAX_SOCKETS, newSockets);
        return output;
    }

    @Override
    public RecipeSerializer<SocketGrantRecipe> getSerializer() {
        return AsclepiusRecipes.SOCKET_GRANT_SERIALIZER;
    }

    @Override
    public RecipeType<SocketGrantRecipe> getType() {
        return AsclepiusRecipes.SOCKET_GRANT_TYPE;
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

    @Override
    public boolean consumeCatalyst() { return this.consumeCatalyst; }

    @Override
    public int getCatalystAmount() { return catalyst.count(); }

    @Override
    public boolean checkConditions(Player player, Level level) { return conditions.check(player, level); }

    public AltarRecipeConditions getConditions() { return this.conditions; }
    public IngredientWithComponents getBaseItem() { return altarItem; }
    public IngredientWithComponents getCatalystItem() { return catalyst; }
    public int getSocketsGranted() { return socketsGranted; }
    public int getMaxSockets() { return maxSockets; }
}

package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusBlocks;
import com.gnottero.asclepius.registry.AsclepiusItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AsclepiusRecipeProvider extends FabricRecipeProvider {
    public AsclepiusRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries,
            @NotNull RecipeOutput exporter) {
        return new RecipeProvider(registries, exporter) {
            @Override
            public void buildRecipes() {
                HolderGetter<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                // Teru Teru Bozu
                ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.DECORATIONS, AsclepiusBlocks.TERU_TERU_BOZU_ITEM)
                        .pattern(" W ")
                        .pattern("WSW")
                        .pattern(" W ")
                        .define('W', Blocks.WHITE_WOOL)
                        .define('S', Items.STRING)
                        .unlockedBy(getHasName(Blocks.WHITE_WOOL), has(Blocks.WHITE_WOOL))
                        .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
                        .save(exporter);

                // Enderic Key
                ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.MISC, AsclepiusItems.ENDERIC_KEY)
                        .pattern("E")
                        .pattern("C")
                        .define('E', Items.ENDER_EYE)
                        .define('C', Items.CLOCK)
                        .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                        .save(exporter);

                // Paxels
                generatePaxelRecipe(itemLookup, exporter, AsclepiusItems.WOODEN_PAXEL, Items.WOODEN_AXE,
                        Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL);
                generatePaxelRecipe(itemLookup, exporter, AsclepiusItems.STONE_PAXEL, Items.STONE_AXE,
                        Items.STONE_PICKAXE, Items.STONE_SHOVEL);
                generatePaxelRecipe(itemLookup, exporter, AsclepiusItems.IRON_PAXEL, Items.IRON_AXE, Items.IRON_PICKAXE,
                        Items.IRON_SHOVEL);
                generatePaxelRecipe(itemLookup, exporter, AsclepiusItems.GOLDEN_PAXEL, Items.GOLDEN_AXE,
                        Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL);
                generatePaxelRecipe(itemLookup, exporter, AsclepiusItems.DIAMOND_PAXEL, Items.DIAMOND_AXE,
                        Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL);
                generatePaxelRecipe(itemLookup, exporter, AsclepiusItems.COPPER_PAXEL, Items.COPPER_AXE,
                        Items.COPPER_PICKAXE, Items.COPPER_SHOVEL);

                // Netherite Paxel (Smithing)
                netheriteSmithing(AsclepiusItems.DIAMOND_PAXEL, RecipeCategory.TOOLS, AsclepiusItems.NETHERITE_PAXEL);
            }

            private void generatePaxelRecipe(HolderGetter<Item> itemLookup, RecipeOutput exporter, Item paxel, Item axe,
                    Item pickaxe, Item shovel) {
                ShapedRecipeBuilder.shaped(itemLookup, RecipeCategory.TOOLS, paxel)
                        .pattern("ASP")
                        .pattern(" T ")
                        .pattern(" T ")
                        .define('A', axe)
                        .define('P', pickaxe)
                        .define('S', shovel)
                        .define('T', Items.STICK)
                        .unlockedBy(getHasName(axe), has(axe))
                        .unlockedBy(getHasName(pickaxe), has(pickaxe))
                        .unlockedBy(getHasName(shovel), has(shovel))
                        .save(exporter);
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "Asclepius Recipes";
    }
}

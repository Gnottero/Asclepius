package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.registry.AsclepiusDecorativeBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Recipe provider for generating recipes for all custom and vanilla-derived decorative blocks.
 */
public class AsclepiusDecorativeRecipeProvider extends FabricRecipeProvider {

    public AsclepiusDecorativeRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderGetter<Item> items = registryLookup.lookupOrThrow(Registries.ITEM);

                // 1. Recipes for custom base blocks (wool & concrete)
                AsclepiusDecorativeBlocks.CUSTOM_BASE_BLOCKS.forEach(baseBlock -> {
                    Block slab = AsclepiusDecorativeBlocks.SLABS.get(baseBlock);
                    Block stairs = AsclepiusDecorativeBlocks.STAIRS.get(baseBlock);
                    Block sidewaysStairs = AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.get(baseBlock);
                    Block vertSlab = AsclepiusDecorativeBlocks.VERTICAL_SLABS.get(baseBlock);
                    Block wall = AsclepiusDecorativeBlocks.WALLS.get(baseBlock);

                    // Slab
                    if (slab != null) {
                        // Crafting: 3 base blocks -> 6 slabs
                        var recipe = ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, slab, 6)
                                .define('#', baseBlock)
                                .pattern("###")
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveRecipe(recipe, exporter, slab, "");

                        // Stonecutting: 1 base block -> 2 slabs
                        var stonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, slab, 2)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveSingleRecipe(stonecutter, exporter, slab, "_stonecutter");
                    }

                    // Stairs
                    if (stairs != null) {
                        // Crafting: 6 base blocks -> 4 stairs
                        var recipe = ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, stairs, 4)
                                .define('#', baseBlock)
                                .pattern("#  ")
                                .pattern("## ")
                                .pattern("###")
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveRecipe(recipe, exporter, stairs, "");

                        // Stonecutting: 1 base block -> 1 stairs
                        var stonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, stairs, 1)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveSingleRecipe(stonecutter, exporter, stairs, "_stonecutter");
                    }

                    // Sideways Stairs
                    if (sidewaysStairs != null) {
                        // Stonecutting: 1 base block -> 1 sideways stair
                        var stonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, sidewaysStairs, 1)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveSingleRecipe(stonecutter, exporter, sidewaysStairs, "_stonecutter");

                        if (stairs != null) {
                            // Shapeless conversion: 1 stair -> 1 sideways stair
                            var recipe1 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, sidewaysStairs, 1)
                                    .requires(stairs)
                                    .unlockedBy(getHasName(stairs), has(stairs));
                            saveRecipe(recipe1, exporter, sidewaysStairs, "_from_stairs");

                            // Shapeless conversion: 1 sideways stair -> 1 stair
                            var recipe2 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, stairs, 1)
                                    .requires(sidewaysStairs)
                                    .unlockedBy(getHasName(sidewaysStairs), has(sidewaysStairs));
                            saveRecipe(recipe2, exporter, stairs, "_from_sideways_stairs");

                            // Stonecutting conversion: 1 stair -> 1 sideways stair
                            var stonecutterFromStairs = SingleItemRecipeBuilder.stonecutting(Ingredient.of(stairs), RecipeCategory.BUILDING_BLOCKS, sidewaysStairs, 1)
                                    .unlockedBy(getHasName(stairs), has(stairs));
                            saveSingleRecipe(stonecutterFromStairs, exporter, sidewaysStairs, "_stonecutter_from_stairs");
                        }
                    }

                    // Vertical Slab
                    if (vertSlab != null) {
                        // Crafting: 3 base blocks vertically -> 6 vertical slabs
                        var recipe = ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, vertSlab, 6)
                                .define('#', baseBlock)
                                .pattern("#")
                                .pattern("#")
                                .pattern("#")
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveRecipe(recipe, exporter, vertSlab, "");

                        // Stonecutting: 1 base block -> 2 vertical slabs
                        var stonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, vertSlab, 2)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveSingleRecipe(stonecutter, exporter, vertSlab, "_stonecutter");

                        if (slab != null) {
                            // Shapeless conversion: 1 slab -> 1 vertical slab
                            var recipe1 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, vertSlab, 1)
                                    .requires(slab)
                                    .unlockedBy(getHasName(slab), has(slab));
                            saveRecipe(recipe1, exporter, vertSlab, "_from_slab");

                            // Shapeless conversion: 1 vertical slab -> 1 slab
                            var recipe2 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, slab, 1)
                                    .requires(vertSlab)
                                    .unlockedBy(getHasName(vertSlab), has(vertSlab));
                            saveRecipe(recipe2, exporter, slab, "_from_vertical_slab");

                            // Stonecutting conversion: 1 slab -> 1 vertical slab
                            var stonecutterFromSlab = SingleItemRecipeBuilder.stonecutting(Ingredient.of(slab), RecipeCategory.BUILDING_BLOCKS, vertSlab, 1)
                                    .unlockedBy(getHasName(slab), has(slab));
                            saveSingleRecipe(stonecutterFromSlab, exporter, vertSlab, "_stonecutter_from_slab");
                        }
                    }

                    // Wall
                    if (wall != null) {
                        // Crafting: 6 base blocks -> 6 walls
                        var recipe = ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, wall, 6)
                                .define('#', baseBlock)
                                .pattern("###")
                                .pattern("###")
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveRecipe(recipe, exporter, wall, "");

                        // Stonecutting: 1 base block -> 1 wall
                        var stonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, wall, 1)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock));
                        saveSingleRecipe(stonecutter, exporter, wall, "_stonecutter");
                    }
                });

                // 2. Recipes for vanilla-derived vertical slabs and sideways stairs
                AsclepiusDecorativeBlocks.VANILLA_SLAB_BLOCKS.forEach(vanillaSlab -> {
                    Block vertSlab = AsclepiusDecorativeBlocks.VERTICAL_SLABS.get(vanillaSlab);
                    Block sidewaysStair = AsclepiusDecorativeBlocks.SIDEWAYS_STAIRS.get(vanillaSlab);

                    Optional<Block> baseBlockOpt = findBaseBlock(vanillaSlab);

                    // Vertical Slab
                    if (vertSlab != null) {
                        // Shapeless conversion: 1 vanilla slab -> 1 vertical slab
                        var recipe1 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, vertSlab, 1)
                                .requires(vanillaSlab)
                                .unlockedBy(getHasName(vanillaSlab), has(vanillaSlab));
                        saveRecipe(recipe1, exporter, vertSlab, "_from_slab");

                        // Shapeless conversion: 1 vertical slab -> 1 vanilla slab
                        var recipe2 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 1)
                                .requires(vertSlab)
                                .unlockedBy(getHasName(vertSlab), has(vertSlab));
                        saveRecipe(recipe2, exporter, vanillaSlab, "_from_vertical_slab");

                        // Stonecutting conversion: 1 vanilla slab -> 1 vertical slab
                        var stonecutterFromSlab = SingleItemRecipeBuilder.stonecutting(Ingredient.of(vanillaSlab), RecipeCategory.BUILDING_BLOCKS, vertSlab, 1)
                                .unlockedBy(getHasName(vanillaSlab), has(vanillaSlab));
                        saveSingleRecipe(stonecutterFromSlab, exporter, vertSlab, "_stonecutter_from_slab");

                        // Stonecutting conversion: 1 vertical slab -> 1 vanilla slab
                        var stonecutterFromVertSlab = SingleItemRecipeBuilder.stonecutting(Ingredient.of(vertSlab), RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 1)
                                .unlockedBy(getHasName(vertSlab), has(vertSlab));
                        saveSingleRecipe(stonecutterFromVertSlab, exporter, vanillaSlab, "_stonecutter_from_vertical_slab");

                        // If base block is found:
                        if (baseBlockOpt.isPresent()) {
                            Block baseBlock = baseBlockOpt.get();
                            // Crafting: 3 base blocks vertically -> 6 vertical slabs
                            var baseRecipe = ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, vertSlab, 6)
                                    .define('#', baseBlock)
                                    .pattern("#")
                                    .pattern("#")
                                    .pattern("#")
                                    .unlockedBy(getHasName(baseBlock), has(baseBlock));
                            saveRecipe(baseRecipe, exporter, vertSlab, "");

                            // Stonecutting: 1 base block -> 2 vertical slabs
                            var baseStonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, vertSlab, 2)
                                    .unlockedBy(getHasName(baseBlock), has(baseBlock));
                            saveSingleRecipe(baseStonecutter, exporter, vertSlab, "_stonecutter");
                        }
                    }

                    // Sideways Stairs
                    if (sidewaysStair != null) {
                        // Find the corresponding vanilla stair block
                        Identifier slabId = BuiltInRegistries.BLOCK.getKey(vanillaSlab);
                        Identifier stairId = Identifier.fromNamespaceAndPath(slabId.getNamespace(), slabId.getPath().replace("_slab", "_stairs"));
                        Optional<Block> vanillaStairOpt = BuiltInRegistries.BLOCK.getOptional(stairId);

                        if (vanillaStairOpt.isPresent()) {
                            Block vanillaStair = vanillaStairOpt.get();

                            // Shapeless conversion: 1 vanilla stair -> 1 sideways stair
                            var recipe1 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, sidewaysStair, 1)
                                    .requires(vanillaStair)
                                    .unlockedBy(getHasName(vanillaStair), has(vanillaStair));
                            saveRecipe(recipe1, exporter, sidewaysStair, "_from_stairs");

                            // Shapeless conversion: 1 sideways stair -> 1 vanilla stair
                            var recipe2 = ShapelessRecipeBuilder.shapeless(items, RecipeCategory.BUILDING_BLOCKS, vanillaStair, 1)
                                    .requires(sidewaysStair)
                                    .unlockedBy(getHasName(sidewaysStair), has(sidewaysStair));
                            saveRecipe(recipe2, exporter, vanillaStair, "_from_sideways_stairs");

                            // Stonecutting conversion: 1 vanilla stair -> 1 sideways stair
                            var stonecutterFromStairs = SingleItemRecipeBuilder.stonecutting(Ingredient.of(vanillaStair), RecipeCategory.BUILDING_BLOCKS, sidewaysStair, 1)
                                    .unlockedBy(getHasName(vanillaStair), has(vanillaStair));
                            saveSingleRecipe(stonecutterFromStairs, exporter, sidewaysStair, "_stonecutter_from_stairs");

                            // Stonecutting conversion: 1 sideways stair -> 1 vanilla stair
                            var stonecutterFromSideways = SingleItemRecipeBuilder.stonecutting(Ingredient.of(sidewaysStair), RecipeCategory.BUILDING_BLOCKS, vanillaStair, 1)
                                    .unlockedBy(getHasName(sidewaysStair), has(sidewaysStair));
                            saveSingleRecipe(stonecutterFromSideways, exporter, vanillaStair, "_stonecutter_from_sideways_stairs");
                        }

                        // If base block is found:
                        if (baseBlockOpt.isPresent()) {
                            Block baseBlock = baseBlockOpt.get();
                            // Stonecutting: 1 base block -> 1 sideways stair
                            var baseStonecutter = SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, sidewaysStair, 1)
                                    .unlockedBy(getHasName(baseBlock), has(baseBlock));
                            saveSingleRecipe(baseStonecutter, exporter, sidewaysStair, "_stonecutter");

                            // If no vanilla stair exists, allow crafting sideways stairs directly from base block
                            if (vanillaStairOpt.isEmpty()) {
                                var baseRecipe = ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, sidewaysStair, 4)
                                        .define('#', baseBlock)
                                        .pattern("#  ")
                                        .pattern("## ")
                                        .pattern("###")
                                        .unlockedBy(getHasName(baseBlock), has(baseBlock));
                                saveRecipe(baseRecipe, exporter, sidewaysStair, "");
                            }
                        }
                    }
                });
            }
        };
    }

    private static Identifier recipeId(Block output, String suffix) {
        Identifier baseId = BuiltInRegistries.BLOCK.getKey(output);
        return Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, baseId.getPath() + suffix);
    }

    private static void saveRecipe(RecipeBuilder builder, RecipeOutput exporter, Block output, String suffix) {
        Identifier id = recipeId(output, suffix);
        builder.save(exporter, ResourceKey.create(Registries.RECIPE, id));
    }

    private static void saveSingleRecipe(SingleItemRecipeBuilder builder, RecipeOutput exporter, Block output, String suffix) {
        Identifier id = recipeId(output, suffix);
        builder.save(exporter, ResourceKey.create(Registries.RECIPE, id));
    }

    private static Optional<Block> findBaseBlock(Block slab) {
        Identifier slabId = BuiltInRegistries.BLOCK.getKey(slab);
        String path = slabId.getPath();
        if (!path.endsWith("_slab")) {
            return Optional.empty();
        }
        String baseName = path.substring(0, path.length() - 5); // remove "_slab"

        // Try baseName (e.g. cobblestone, smooth_stone)
        Optional<Block> opt = BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(slabId.getNamespace(), baseName));
        if (opt.isPresent()) return opt;

        // Try baseName + "_planks" (e.g. oak_planks)
        opt = BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(slabId.getNamespace(), baseName + "_planks"));
        if (opt.isPresent()) return opt;

        // Try baseName + "_block" (e.g. purpur_block)
        opt = BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(slabId.getNamespace(), baseName + "_block"));
        if (opt.isPresent()) return opt;

        // Try baseName + "s" (e.g. stone_bricks)
        opt = BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(slabId.getNamespace(), baseName + "s"));
        if (opt.isPresent()) return opt;

        return Optional.empty();
    }

    @Override
    public String getName() {
        return "Asclepius Decorative Recipes";
    }
}

package com.gnottero.asclepius.datagen;

import com.gnottero.asclepius.registry.AsclepiusBlocks;
import com.gnottero.asclepius.registry.AsclepiusItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
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
            final HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);

            @Override
            public void buildRecipes() {
                buildBlockRecipes();
                buildItemRecipes();
                buildPaxelRecipes();
                buildHammerRecipes();
                buildSmithingRecipes();
            }

            private void buildBlockRecipes() {
                ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, AsclepiusBlocks.TERU_TERU_BOZU)
                        .pattern(" W ")
                        .pattern("WSW")
                        .pattern(" W ")
                        .define('W', Blocks.WOOL.white())
                        .define('S', Items.STRING)
                        .unlockedBy(getHasName(Blocks.WOOL.white()), has(Blocks.WOOL.white()))
                        .save(exporter);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, Blocks.TUFF)
                        .pattern("AAA")
                        .pattern("AAA")
                        .pattern("AAA")
                        .define('A', AsclepiusBlocks.VOLCANIC_ASH)
                        .unlockedBy(getHasName(AsclepiusBlocks.VOLCANIC_ASH), has(AsclepiusBlocks.VOLCANIC_ASH))
                        .save(exporter);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, AsclepiusBlocks.SHALE)
                        .pattern("MC")
                        .pattern("CM")
                        .define('M', Blocks.MUD)
                        .define('C', Blocks.CLAY)
                        .unlockedBy(getHasName(Blocks.MUD), has(Blocks.MUD))
                        .unlockedBy(getHasName(Blocks.CLAY), has(Blocks.CLAY))
                        .save(exporter);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, AsclepiusBlocks.PACKED_SHALE)
                        .pattern("SS")
                        .pattern("SS")
                        .define('S', AsclepiusBlocks.SHALE)
                        .unlockedBy(getHasName(AsclepiusBlocks.SHALE), has(AsclepiusBlocks.SHALE))
                        .save(exporter);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.BUILDING_BLOCKS, Blocks.CALCITE)
                        .pattern("DD")
                        .pattern("DD")
                        .define('D', Blocks.DRIPSTONE_BLOCK)
                        .unlockedBy(getHasName(Blocks.DRIPSTONE_BLOCK), has(Blocks.DRIPSTONE_BLOCK))
                        .save(exporter);

                SimpleCookingRecipeBuilder
                        .smelting(
                                Ingredient.of(AsclepiusBlocks.PACKED_SHALE),
                                RecipeCategory.MISC,
                                CookingBookCategory.BLOCKS,
                                Blocks.DEEPSLATE,
                                0.7f,
                                200)
                        .unlockedBy(getHasName(AsclepiusBlocks.PACKED_SHALE), has(AsclepiusBlocks.PACKED_SHALE))
                        .save(output);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.DECORATIONS, AsclepiusBlocks.PALE_ALTAR)
                        .pattern("AMA")
                        .pattern("BCB")
                        .pattern("PPP")
                        .define('A', Items.AMETHYST_SHARD)
                        .define('M', Blocks.PALE_MOSS_BLOCK)
                        .define('B', Blocks.AMETHYST_BLOCK)
                        .define('C', Blocks.CREAKING_HEART)
                        .define('P', Blocks.PALE_OAK_LOG)
                        .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                        .unlockedBy(getHasName(Blocks.PALE_MOSS_BLOCK), has(Blocks.PALE_MOSS_BLOCK))
                        .unlockedBy(getHasName(Blocks.AMETHYST_BLOCK), has(Blocks.AMETHYST_BLOCK))
                        .unlockedBy(getHasName(Blocks.CREAKING_HEART), has(Blocks.CREAKING_HEART))
                        .unlockedBy(getHasName(Blocks.PALE_OAK_LOG), has(Blocks.PALE_OAK_LOG))
                        .save(exporter);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, AsclepiusBlocks.CHUNK_LOADER)
                        .pattern("OEO")
                        .pattern("ELE")
                        .pattern("OEO")
                        .define('O', Blocks.OBSIDIAN)
                        .define('E', Items.ENDER_PEARL)
                        .define('L', Blocks.LODESTONE)
                        .unlockedBy(getHasName(Blocks.LODESTONE), has(Blocks.LODESTONE))
                        .unlockedBy(getHasName(Items.ENDER_PEARL), has(Items.ENDER_PEARL))
                        .save(exporter);
            }

            private void buildItemRecipes() {
                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, AsclepiusItems.ENDER_KEY)
                        .pattern("S")
                        .pattern("K")
                        .pattern("E")
                        .define('S', Items.SHULKER_SHELL)
                        .define('K', Items.TRIAL_KEY)
                        .define('E', Items.ENDER_EYE)
                        .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                        .save(exporter);

                ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, AsclepiusItems.RECALL_EYE)
                        .pattern("LIL")
                        .pattern("IEI")
                        .pattern("LIL")
                        .define('L', Items.LAPIS_LAZULI)
                        .define('I', Items.IRON_INGOT)
                        .define('E', Items.ENDER_EYE)
                        .unlockedBy(getHasName(Items.ENDER_EYE), has(Items.ENDER_EYE))
                        .save(exporter);
            }

            private void buildPaxelRecipes() {
                paxel(AsclepiusItems.WOODEN_PAXEL, Items.WOODEN_AXE, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL);
                paxel(AsclepiusItems.STONE_PAXEL, Items.STONE_AXE, Items.STONE_PICKAXE, Items.STONE_SHOVEL);
                paxel(AsclepiusItems.IRON_PAXEL, Items.IRON_AXE, Items.IRON_PICKAXE, Items.IRON_SHOVEL);
                paxel(AsclepiusItems.GOLDEN_PAXEL, Items.GOLDEN_AXE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL);
                paxel(AsclepiusItems.DIAMOND_PAXEL, Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL);
                paxel(AsclepiusItems.COPPER_PAXEL, Items.COPPER_AXE, Items.COPPER_PICKAXE, Items.COPPER_SHOVEL);
            }

            private void buildHammerRecipes() {
                hammer(AsclepiusItems.WOODEN_HAMMER, Items.WOODEN_PICKAXE, ItemTags.PLANKS, Items.STICK);
                hammer(AsclepiusItems.STONE_HAMMER, Items.STONE_PICKAXE, Items.COBBLESTONE, Items.STICK);
                hammer(AsclepiusItems.IRON_HAMMER, Items.IRON_PICKAXE, Items.IRON_BLOCK, Items.STICK);
                hammer(AsclepiusItems.GOLDEN_HAMMER, Items.GOLDEN_PICKAXE, Items.GOLD_BLOCK, Items.STICK);
                hammer(AsclepiusItems.DIAMOND_HAMMER, Items.DIAMOND_PICKAXE, Items.DIAMOND_BLOCK, Items.STICK);
                hammer(AsclepiusItems.COPPER_HAMMER, Items.COPPER_PICKAXE, Items.COPPER_BLOCK.weathering().unaffected(), Items.STICK);
            }

            private void buildSmithingRecipes() {
                netheriteSmithing(AsclepiusItems.DIAMOND_PAXEL, RecipeCategory.TOOLS, AsclepiusItems.NETHERITE_PAXEL);
                netheriteSmithing(AsclepiusItems.DIAMOND_HAMMER, RecipeCategory.TOOLS, AsclepiusItems.NETHERITE_HAMMER);
            }

            private void paxel(Item result, Item axe, Item pickaxe, Item shovel) {
                ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, result)
                        .pattern("ASP")
                        .pattern(" T ")
                        .pattern(" T ")
                        .define('A', axe)
                        .define('P', pickaxe)
                        .define('S', shovel)
                        .define('T', Items.STICK)
                        .unlockedBy(getHasName(axe), has(axe))
                        .save(exporter);
            }

            private void hammer(Item result, Item pickaxe, Object block, Item stick) {
                ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, result)
                        .pattern("BPB")
                        .pattern(" T ")
                        .pattern(" T ")
                        .define('P', pickaxe)
                        .define('T', stick);

                if (block instanceof Item i) {
                    builder.define('B', i).unlockedBy(getHasName(i), has(i));
                } else if (block instanceof net.minecraft.tags.TagKey<?> t) {
                    builder.define('B', (net.minecraft.tags.TagKey<Item>) t);
                }

                builder.unlockedBy(getHasName(pickaxe), has(pickaxe)).save(exporter);
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "Asclepius Recipes";
    }
}
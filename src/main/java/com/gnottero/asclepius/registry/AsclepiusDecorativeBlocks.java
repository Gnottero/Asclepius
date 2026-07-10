package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.Asclepius;
import com.gnottero.asclepius.feature.decorative_blocks.SidewaysStairBlock;
import com.gnottero.asclepius.feature.decorative_blocks.VerticalSlabBlock;
import com.gnottero.asclepius.feature.decorative_blocks.WeatheringCopperSidewaysStairBlock;
import com.gnottero.asclepius.feature.decorative_blocks.WeatheringCopperVerticalSlabBlock;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Bulk registration for decorative block families (vertical slabs, sideways stairs,
 * plus weathering-copper variants) generated for every vanilla slab and a fixed list
 * of custom base blocks. Unlike every other class here, this isn't a flat list of
 * {@code public static final} fields — with dozens of base blocks each producing
 * several variants, registration is a loop over data ({@link #CUSTOM_BASE_BLOCKS},
 * {@link #VANILLA_SLAB_BLOCKS}) run once from {@link #registerAll()}, not one field
 * initializer per block. The block/item classes themselves live in
 * {@code feature.decorative_blocks}; this class only wires them in and builds the
 * dedicated "Decorative Blocks" creative tab.
 */
public class AsclepiusDecorativeBlocks {

    /** All vanilla Minecraft slab blocks. */
    public static final List<Block> VANILLA_SLAB_BLOCKS = BuiltInRegistries.BLOCK.stream()
            .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("minecraft"))
            .filter(block -> block instanceof SlabBlock)
            .toList();

    /** Base blocks for which custom slabs, stairs, vertical slabs, and sideways stairs are generated. */
    public static final List<Block> CUSTOM_BASE_BLOCKS = Stream.concat(
            Blocks.WOOL.asList().stream(),
            Blocks.CONCRETE.asList().stream()
    ).toList();

    // Populated by registerAll(), not at static-init like every other registry class'
    // fields — datagen (a separate fabric-datagen entrypoint) reads these, so it
    // implicitly depends on the main entrypoint's registerAll() having already run.
    public static final Map<Block, Block> SLABS = new HashMap<>();
    public static final Map<Block, Block> VERTICAL_SLABS = new HashMap<>();
    public static final Map<Block, Block> STAIRS = new HashMap<>();
    public static final Map<Block, Block> SIDEWAYS_STAIRS = new HashMap<>();
    public static final Map<Block, Block> WALLS = new HashMap<>();

    /**
     * Registers a vertical slab variant of a vanilla slab.
     */
    public static void registerVanillaVerticalSlab(Block vanillaSlab) {
        String slabPath  = BuiltInRegistries.BLOCK.getKey(vanillaSlab).getPath();
        String vSlabPath = slabPath.replace("_slab", "_vertical_slab");

        Block vSlab = register(vSlabPath, verticalSlabFactory(vanillaSlab), BlockBehaviour.Properties.ofFullCopy(vanillaSlab));
        VERTICAL_SLABS.put(vanillaSlab, vSlab);
    }

    /**
     * Registers a sideways stair variant of a vanilla slab.
     */
    public static void registerVanillaSidewaysStair(Block vanillaSlab) {
        String slabPath = BuiltInRegistries.BLOCK.getKey(vanillaSlab).getPath();
        String sidewaysPath = slabPath.replace("_slab", "_sideways_stairs");

        Block sidewaysStairs = register(sidewaysPath, sidewaysStairFactory(vanillaSlab), BlockBehaviour.Properties.ofFullCopy(vanillaSlab));
        SIDEWAYS_STAIRS.put(vanillaSlab, sidewaysStairs);
    }

    /**
     * Registers a custom slab, stair, vertical slab, sideways stair, and wall for a base block.
     */
    public static void registerSlabStairAndVerticalSlab(Block baseBlock) {
        String basePath = BuiltInRegistries.BLOCK.getKey(baseBlock).getPath();

        Block slab = register(basePath + "_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(baseBlock));
        SLABS.put(baseBlock, slab);

        Block stairs = register(basePath + "_stairs",
                properties -> new StairBlock(baseBlock.defaultBlockState(), properties),
                BlockBehaviour.Properties.ofFullCopy(baseBlock));
        STAIRS.put(baseBlock, stairs);

        Block sidewaysStairs = register(basePath + "_sideways_stairs", sidewaysStairFactory(baseBlock), BlockBehaviour.Properties.ofFullCopy(baseBlock));
        SIDEWAYS_STAIRS.put(baseBlock, sidewaysStairs);

        Block vSlab = register(basePath + "_vertical_slab", verticalSlabFactory(baseBlock), BlockBehaviour.Properties.ofFullCopy(baseBlock));
        VERTICAL_SLABS.put(baseBlock, vSlab);

        Block wall = register(basePath + "_wall", WallBlock::new, BlockBehaviour.Properties.ofFullCopy(baseBlock));
        WALLS.put(baseBlock, wall);
    }

    private static Function<BlockBehaviour.Properties, Block> verticalSlabFactory(Block source) {
        return source instanceof WeatheringCopper weatheringCopper
                ? props -> new WeatheringCopperVerticalSlabBlock(weatheringCopper.getAge(), props)
                : VerticalSlabBlock::new;
    }

    private static Function<BlockBehaviour.Properties, Block> sidewaysStairFactory(Block source) {
        return source instanceof WeatheringCopper weatheringCopper
                ? props -> new WeatheringCopperSidewaysStairBlock(weatheringCopper.getAge(), props)
                : SidewaysStairBlock::new;
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        Block block = AsclepiusBlocks.register(name, properties, blockFactory);
        AsclepiusItems.registerBlockItem(block);
        return block;
    }

    private static void registerOxidationAndWaxingPairs() {
        registerOxidationAndWaxingPairs(VERTICAL_SLABS);
        registerOxidationAndWaxingPairs(SIDEWAYS_STAIRS);
    }

    private static void registerOxidationAndWaxingPairs(Map<Block, Block> variants) {
        for (Map.Entry<Block, Block> entry : variants.entrySet()) {
            Block sourceBlock = entry.getKey();
            Block variant     = entry.getValue();

            if (sourceBlock instanceof WeatheringCopper) {
                WeatheringCopper.getNext(sourceBlock).ifPresent(nextSource -> {
                    Block nextVariant = variants.get(nextSource);
                    if (nextVariant != null) {
                        OxidizableBlocksRegistry.registerNextStage(variant, nextVariant);
                    }
                });
            }

            Block waxedSource = HoneycombItem.WAXABLES.get().get(sourceBlock);
            if (waxedSource != null) {
                Block waxedVariant = variants.get(waxedSource);
                if (waxedVariant != null) {
                    OxidizableBlocksRegistry.registerWaxable(variant, waxedVariant);
                }
            }
        }
    }

    /**
     * Registers every block/item family and the dedicated creative tab that displays
     * them. Called once from {@link com.gnottero.asclepius.Asclepius#onInitialize()} —
     * see that method's ordering comment for why it runs after {@code AsclepiusItems}.
     */
    public static void registerAll() {
        VANILLA_SLAB_BLOCKS.forEach(AsclepiusDecorativeBlocks::registerVanillaVerticalSlab);
        VANILLA_SLAB_BLOCKS.forEach(AsclepiusDecorativeBlocks::registerVanillaSidewaysStair);
        CUSTOM_BASE_BLOCKS.forEach(AsclepiusDecorativeBlocks::registerSlabStairAndVerticalSlab);
        registerOxidationAndWaxingPairs();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath(Asclepius.MOD_ID, "decorative_blocks"),
                FabricCreativeModeTab.builder()
                        .icon(() -> new ItemStack(WALLS.getOrDefault(Blocks.CONCRETE.white(), CUSTOM_BASE_BLOCKS.getFirst())))
                        .title(Component.translatable("itemgroup.asclepius.decorative_blocks"))
                        .displayItems((params, output) -> Stream.of(SLABS, VERTICAL_SLABS, STAIRS, SIDEWAYS_STAIRS, WALLS)
                                .flatMap(map -> map.values().stream())
                                .forEach(output::accept))
                        .build());

        Asclepius.LOGGER.info("[{}]> Register Decorative Blocks", Asclepius.MOD_ID);
    }
}

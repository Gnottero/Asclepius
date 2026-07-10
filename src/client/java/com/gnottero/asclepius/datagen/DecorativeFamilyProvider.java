package com.gnottero.asclepius.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.world.level.block.Block;

/**
 * Fluent builder wrapper for block families with vertical slabs and sideways stairs.
 */
public class DecorativeFamilyProvider {
    private final BlockModelGenerators generator;
    private final BlockModelGenerators.BlockFamilyProvider family;
    private final Block baseBlock;

    public DecorativeFamilyProvider(BlockModelGenerators generator, Block baseBlock) {
        this.generator = generator;
        this.baseBlock = baseBlock;
        this.family = generator.family(baseBlock);
    }

    public DecorativeFamilyProvider slab(Block slab) {
        if (slab != null) {
            family.slab(slab);
        }
        return this;
    }

    public DecorativeFamilyProvider stairs(Block stairs) {
        if (stairs != null) {
            family.stairs(stairs);
        }
        return this;
    }

    public DecorativeFamilyProvider verticalSlab(Block vertSlab) {
        if (vertSlab != null) {
            VerticalSlabModelGenerator.registerVerticalSlabFromBaseBlock(generator, vertSlab, baseBlock);
        }
        return this;
    }

    public DecorativeFamilyProvider sidewaysStair(Block sidewaysStairs) {
        if (sidewaysStairs != null) {
            SidewaysStairModelGenerator.registerSidewaysStairFromBaseBlock(generator, sidewaysStairs, baseBlock);
        }
        return this;
    }

    public DecorativeFamilyProvider wall(Block wall) {
        if (wall != null) {
            family.wall(wall);
        }
        return this;
    }
}

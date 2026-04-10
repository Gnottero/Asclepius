package com.gnottero.asclepius.block;
import com.gnottero.asclepius.Asclepius;
import eu.pb4.polymer.core.api.item.VanillaModeledPolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BaseBlockItem extends BlockItem implements VanillaModeledPolymerItem {

    public BaseBlockItem(Properties properties, Block block) {
        super(block, properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext player) {
        return Items.LANTERN;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        return BuiltInRegistries.ITEM.getKey(this);
    }
}

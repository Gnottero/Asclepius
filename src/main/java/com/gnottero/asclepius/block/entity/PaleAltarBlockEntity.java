package com.gnottero.asclepius.block.entity;

import com.gnottero.asclepius.block.PaleAltarBlock;
import com.gnottero.asclepius.registry.AsclepiusBlockEntities;
import eu.pb4.factorytools.api.block.BlockEntityExtraListener;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PaleAltarBlockEntity extends BlockEntity implements BlockEntityExtraListener {

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private PaleAltarBlock.Model model;

    public PaleAltarBlockEntity(BlockPos pos, BlockState state) {
        super(AsclepiusBlockEntities.PALE_ALTAR, pos, state);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        if (this.model != null) {
            this.model.setAltarItem(this.getStoredItem());
        }
    }

    public ItemStack getStoredItem() {
        return this.items.getFirst();
    }

    public void setStoredItem(ItemStack stack) {
        this.items.set(0, stack.copy());
        this.setChanged();
        if (this.model != null) {
            this.model.setAltarItem(this.getStoredItem());
        }
    }

    public ItemStack removeStoredItem() {
        ItemStack out = this.getStoredItem().copy();
        this.items.set(0, ItemStack.EMPTY);
        this.setChanged();
        if (this.model != null) {
            this.model.setAltarItem(ItemStack.EMPTY);
        }
        return out;
    }

    @Override
    public void onListenerUpdate(LevelChunk chunk) {
        var attachment = BlockAwareAttachment.get(chunk, this.getBlockPos());
        if (attachment == null) return;
        if (!(attachment.holder() instanceof PaleAltarBlock.Model altarModel)) return;

        this.model = altarModel;
        this.model.setAltarItem(this.getStoredItem());
    }
}
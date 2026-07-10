package com.gnottero.asclepius.feature.pale_altar;

import com.gnottero.asclepius.registry.AsclepiusBlockEntities;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class PaleAltarBlockEntity extends BlockEntity implements ItemOwner {

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

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
        this.items.set(0, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public ItemStack getStoredItem() {
        return this.items.getFirst();
    }

    public void setStoredItem(ItemStack stack) {
        this.items.set(0, stack.copy());
        this.setChanged();
        this.syncToClients();
    }

    public ItemStack removeStoredItem() {
        ItemStack out = this.getStoredItem().copy();
        this.items.set(0, ItemStack.EMPTY);
        this.setChanged();
        this.syncToClients();
        return out;
    }

    // setChanged() alone doesn't push an update packet on its own — vanilla only
    // resends block entity data on chunk (re)load. Since players need to see the
    // altar's stored item change immediately, push the update packet by hand to
    // every player currently tracking this block entity.
    private void syncToClients() {
        if (!(this.level instanceof ServerLevel)) return;
        ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
        for (ServerPlayer player : PlayerLookup.tracking(this)) {
            player.connection.send(packet);
        }
    }

    @Override
    public Level level() {
        return this.getLevel();
    }

    @Override
    public Vec3 position() {
        return Vec3.atCenterOf(this.getBlockPos());
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return 0f;
    }
}
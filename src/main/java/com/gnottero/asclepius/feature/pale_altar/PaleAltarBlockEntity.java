package com.gnottero.asclepius.feature.pale_altar;

import com.gnottero.asclepius.registry.AsclepiusBlockEntities;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class PaleAltarBlockEntity extends BlockEntity implements ItemOwner, WorldlyContainer {

    private static final int[] SLOTS = {0};

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    // Distinguishes a freshly-inserted raw item (false) from a finished ritual
    // result (true) — a hopper below the altar may only extract the latter, so
    // automation can't skip the actual ritual step.
    private boolean hasCraftedResult = false;

    public PaleAltarBlockEntity(BlockPos pos, BlockState state) {
        super(AsclepiusBlockEntities.PALE_ALTAR, pos, state);
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putBoolean("has_crafted_result", this.hasCraftedResult);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.items.set(0, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.hasCraftedResult = input.getBooleanOr("has_crafted_result", false);
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
        this.hasCraftedResult = false;
        this.setChanged();
        this.syncToClients();
    }

    // Used only by attemptCraft on a successful ritual — marks the stored item as
    // a finished result so a hopper below the altar is allowed to extract it.
    public void setCraftedResult(ItemStack stack) {
        this.items.set(0, stack.copy());
        this.hasCraftedResult = true;
        this.setChanged();
        this.syncToClients();
    }

    public ItemStack removeStoredItem() {
        ItemStack out = this.getStoredItem().copy();
        this.items.set(0, ItemStack.EMPTY);
        this.hasCraftedResult = false;
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

    // --- Container / WorldlyContainer: lets a hopper feed the altar from above
    // and drain it from below, without exposing any way to bypass the manual
    // ritual step itself (see canTakeItemThroughFace).

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.items.getFirst().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.getFirst();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
        if (!result.isEmpty()) {
            if (this.items.getFirst().isEmpty()) this.hasCraftedResult = false;
            this.setChanged();
            this.syncToClients();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = ContainerHelper.takeItem(this.items, slot);
        this.hasCraftedResult = false;
        this.setChanged();
        this.syncToClients();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        // A hopper inserting into the altar is a raw input, exactly like a manual
        // player insert — it still needs a Hammer swing to become a result.
        this.setStoredItem(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.set(0, ItemStack.EMPTY);
        this.hasCraftedResult = false;
        this.setChanged();
        this.syncToClients();
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction == Direction.UP && this.items.getFirst().isEmpty();
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN && this.hasCraftedResult;
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
package com.gnottero.asclepius.feature.pale_altar.client.render.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class PaleAltarRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    public boolean hasItem;
    public long gameTime;
    public float partialTick;
}

package com.gnottero.asclepius.feature.pale_altar.client.render;

import com.gnottero.asclepius.feature.pale_altar.PaleAltarBlockEntity;
import com.gnottero.asclepius.feature.pale_altar.client.render.state.PaleAltarRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PaleAltarBlockEntityRenderer implements BlockEntityRenderer<PaleAltarBlockEntity, PaleAltarRenderState> {

    private static final float FLOAT_AMPLITUDE = 0.06f;
    private static final float FLOAT_SPEED = 0.05f;
    private static final float ROTATION_SPEED = 0.05f;

    private final ItemModelResolver itemModelResolver;

    public PaleAltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PaleAltarRenderState createRenderState() {
        return new PaleAltarRenderState();
    }

    @Override
    public void extractRenderState(PaleAltarBlockEntity blockEntity, PaleAltarRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        ItemStack stack = blockEntity.getStoredItem();
        state.hasItem = !stack.isEmpty();
        if (state.hasItem) {
            BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);

            if (state.hasItem) {
                this.itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), blockEntity, 0);
            }
            state.gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
            state.partialTick = partialTick;
        }
    }

    @Override
    public void submit(PaleAltarRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (!state.hasItem) return;

        float ticks = state.gameTime + state.partialTick;
        float yOffset = (float) Math.sin(ticks * FLOAT_SPEED) * FLOAT_AMPLITUDE;
        float angleDegrees = (float) Math.toDegrees(ticks * ROTATION_SPEED);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.2 + yOffset, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(angleDegrees));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}

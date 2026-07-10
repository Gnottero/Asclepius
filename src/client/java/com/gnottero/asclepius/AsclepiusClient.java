package com.gnottero.asclepius;

import com.gnottero.asclepius.feature.forgotten_relics.client.RelicSocketTooltipHint;
import com.gnottero.asclepius.feature.pale_altar.client.AltarAmbientHintHandler;
import com.gnottero.asclepius.feature.pale_altar.client.AltarTooltipHint;
import com.gnottero.asclepius.feature.pale_altar.client.render.PaleAltarBlockEntityRenderer;
import com.gnottero.asclepius.registry.AsclepiusBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class AsclepiusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(AsclepiusBlockEntities.PALE_ALTAR, PaleAltarBlockEntityRenderer::new);

        AltarTooltipHint.register();
        AltarAmbientHintHandler.register();
        RelicSocketTooltipHint.register();
    }
}

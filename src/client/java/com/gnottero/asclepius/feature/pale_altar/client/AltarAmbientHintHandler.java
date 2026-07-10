package com.gnottero.asclepius.feature.pale_altar.client;

import com.gnottero.asclepius.feature.pale_altar.PaleAltarBlockEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Sparse ambient particle hint: aiming at an altar holding a base item while
 * the offhand carries a matching catalyst spawns a few particles, without
 * spoiling the exact recipe. Gated to every 10th tick to keep the recipe scan
 * cheap.
 */
public class AltarAmbientHintHandler {

    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(AltarAmbientHintHandler::onEndTick);
    }

    private static void onEndTick(Minecraft client) {
        if (++tickCounter < 10) return;
        tickCounter = 0;

        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) return;
        if (!(client.hitResult instanceof BlockHitResult blockHit)) return;
        if (!(level.getBlockEntity(blockHit.getBlockPos()) instanceof PaleAltarBlockEntity altar)) return;

        ItemStack altarItem = altar.getStoredItem();
        ItemStack offhand = player.getOffhandItem();
        if (altarItem.isEmpty() || offhand.isEmpty()) return;

        boolean matches = AltarHintRecipes.matches(level,
                recipe -> recipe.getBaseItem().test(altarItem) && recipe.getCatalystItem().test(offhand));
        if (!matches) return;

        BlockPos pos = blockHit.getBlockPos();
        RandomSource random = level.getRandom();
        for (int i = 0; i < 3; i++) {
            level.addParticle(ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6,
                    pos.getY() + 1.2 + random.nextDouble() * 0.3,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6,
                    0, 0.02, 0);
        }
    }
}

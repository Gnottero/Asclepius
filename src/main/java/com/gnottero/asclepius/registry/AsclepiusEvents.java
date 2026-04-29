package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.utils.HammerUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class AsclepiusEvents {

    public static void registerAll() {
        PlayerBlockBreakEvents.BEFORE.register(HammerUtils::onHammerBreaksBlock);
    }

}

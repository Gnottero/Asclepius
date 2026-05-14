package com.gnottero.asclepius.registry;

import com.gnottero.asclepius.event.HammerBreakHandler;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class AsclepiusEvents {

    public static void registerAll() {
        registerPlayerBlockBreakEvents();
    }

    public static void registerPlayerBlockBreakEvents() {
        PlayerBlockBreakEvents.BEFORE.register(HammerBreakHandler::onHammerBreaksBlock);
    }

}

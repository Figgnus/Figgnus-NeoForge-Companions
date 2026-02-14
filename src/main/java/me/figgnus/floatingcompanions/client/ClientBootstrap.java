package me.figgnus.floatingcompanions.client;

import me.figgnus.floatingcompanions.client.render.ClientRenderEvents;
import me.figgnus.floatingcompanions.client.render.PetRenderRegistry;
import me.figgnus.floatingcompanions.client.state.ClientPetState;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientBootstrap {
    private static boolean initialized;

    private ClientBootstrap() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        PetRenderRegistry.registerDefaults();
        NeoForge.EVENT_BUS.addListener(ClientRenderEvents::onRenderPlayerPost);
        NeoForge.EVENT_BUS.addListener(ClientBootstrap::onClientLogout);
    }

    private static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientPetState.clear();
    }
}

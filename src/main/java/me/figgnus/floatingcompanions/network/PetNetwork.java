package me.figgnus.floatingcompanions.network;

import me.figgnus.floatingcompanions.client.state.ClientPetState;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class PetNetwork {
    private PetNetwork() {
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(PetStateSyncPayload.TYPE, PetStateSyncPayload.STREAM_CODEC, PetNetwork::handlePetSync);
    }

    private static void handlePetSync(PetStateSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientPetState.replaceSnapshot(payload.activePets()));
    }
}

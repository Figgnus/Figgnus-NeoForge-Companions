package me.figgnus.floatingcompanions.client.render;

import me.figgnus.floatingcompanions.client.state.ClientPetState;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public final class ClientRenderEvents {
    private ClientRenderEvents() {
    }

    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();

        ClientPetState.getActivePet(player.getUUID())
                .flatMap(PetRenderRegistry::get)
                .ifPresent(renderer -> renderer.render(event));
    }
}

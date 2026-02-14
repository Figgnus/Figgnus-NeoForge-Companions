package me.figgnus.floatingcompanions.network;

import me.figgnus.floatingcompanions.pet.PlayerPetService;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class PetSyncEvents {
    private PetSyncEvents() {
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerPetService.syncToPlayer(serverPlayer);
        }
    }
}

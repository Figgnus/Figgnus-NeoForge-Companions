package me.figgnus.floatingcompanions.pet;

import me.figgnus.floatingcompanions.network.PetStateSyncPayload;
import me.figgnus.floatingcompanions.persistence.PetSaveData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PlayerPetService {
    private PlayerPetService() {
    }

    public static Optional<ResourceLocation> getActivePet(ServerPlayer player) {
        return getData(player).getPet(player.getUUID());
    }

    public static boolean setActivePet(ServerPlayer target, ResourceLocation petId) {
        PetSaveData data = getData(target);
        boolean changed = data.setPet(target.getUUID(), petId);

        if (changed) {
            syncAll(target.getServer());
        }

        return changed;
    }

    public static boolean clearActivePet(ServerPlayer target) {
        PetSaveData data = getData(target);
        boolean changed = data.clearPet(target.getUUID());

        if (changed) {
            syncAll(target.getServer());
        }

        return changed;
    }

    public static void syncToPlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new PetStateSyncPayload(getData(player).snapshot()));
    }

    public static void syncAll(MinecraftServer server) {
        Map<UUID, ResourceLocation> snapshot = getData(server).snapshot();
        PacketDistributor.sendToAllPlayers(new PetStateSyncPayload(snapshot));
    }

    private static PetSaveData getData(ServerPlayer player) {
        return getData(player.getServer());
    }

    private static PetSaveData getData(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            throw new IllegalStateException("Could not access overworld for pet data storage.");
        }

        return PetSaveData.get(overworld);
    }
}

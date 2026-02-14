package me.figgnus.floatingcompanions.client.state;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientPetState {
    private static final Map<UUID, ResourceLocation> ACTIVE_PETS = new ConcurrentHashMap<>();

    private ClientPetState() {
    }

    public static void replaceSnapshot(Map<UUID, ResourceLocation> snapshot) {
        ACTIVE_PETS.clear();
        ACTIVE_PETS.putAll(snapshot);
    }

    public static Optional<ResourceLocation> getActivePet(UUID playerId) {
        return Optional.ofNullable(ACTIVE_PETS.get(playerId));
    }

    public static void clear() {
        ACTIVE_PETS.clear();
    }
}

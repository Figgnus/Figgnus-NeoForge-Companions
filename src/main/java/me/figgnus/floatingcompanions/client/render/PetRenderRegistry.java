package me.figgnus.floatingcompanions.client.render;

import me.figgnus.floatingcompanions.pet.PetRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PetRenderRegistry {
    private static final Map<ResourceLocation, PetVisualRenderer> RENDERERS = new ConcurrentHashMap<>();

    private PetRenderRegistry() {
    }

    public static void registerDefaults() {
        register(PetRegistry.BEE.id(), new BeePetRenderer());
        register(PetRegistry.ALLAY.id(), new AllayPetRenderer());
        register(PetRegistry.FEATHER_SWARM.id(), new FeatherSwarmPetRenderer());
    }

    public static void register(ResourceLocation petId, PetVisualRenderer renderer) {
        RENDERERS.put(petId, renderer);
    }

    public static Optional<PetVisualRenderer> get(ResourceLocation petId) {
        return Optional.ofNullable(RENDERERS.get(petId));
    }
}

package me.figgnus.floatingcompanions.pet;

import me.figgnus.floatingcompanions.FloatingCompanionsMod;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PetRegistry {
    private static final Map<ResourceLocation, PetType> TYPES = new LinkedHashMap<>();

    public static final PetType BEE = register(SimplePetType.create(id("bee")));
    public static final PetType ALLAY = register(SimplePetType.create(id("allay")));
    public static final PetType FEATHER_SWARM = register(SimplePetType.create(id("feather_swarm")));

    private PetRegistry() {
    }

    public static PetType register(PetType petType) {
        TYPES.put(petType.id(), petType);
        return petType;
    }

    public static Optional<PetType> get(ResourceLocation id) {
        return Optional.ofNullable(TYPES.get(id));
    }

    public static Collection<PetType> values() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(FloatingCompanionsMod.MOD_ID, path);
    }
}

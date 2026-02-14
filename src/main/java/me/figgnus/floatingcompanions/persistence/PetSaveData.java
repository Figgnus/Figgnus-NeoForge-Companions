package me.figgnus.floatingcompanions.persistence;

import me.figgnus.floatingcompanions.FloatingCompanionsMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class PetSaveData extends SavedData {
    private static final String DATA_NAME = FloatingCompanionsMod.MOD_ID + "_pet_data";
    private static final String KEY_PETS = "pets";
    private static final String KEY_PLAYER = "player";
    private static final String KEY_PET = "pet";

    private static final Factory<PetSaveData> FACTORY =
            new Factory<>(PetSaveData::new, PetSaveData::load, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    private final Map<UUID, ResourceLocation> activePets = new HashMap<>();

    private PetSaveData() {
    }

    public static PetSaveData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static PetSaveData load(CompoundTag tag, HolderLookup.Provider provider) {
        PetSaveData data = new PetSaveData();
        ListTag petsTag = tag.getList(KEY_PETS, Tag.TAG_COMPOUND);

        for (Tag entry : petsTag) {
            if (!(entry instanceof CompoundTag entryTag)) {
                continue;
            }

            if (!entryTag.hasUUID(KEY_PLAYER) || !entryTag.contains(KEY_PET, Tag.TAG_STRING)) {
                continue;
            }

            ResourceLocation petId = ResourceLocation.tryParse(entryTag.getString(KEY_PET));
            if (petId != null) {
                data.activePets.put(entryTag.getUUID(KEY_PLAYER), petId);
            }
        }

        return data;
    }

    public Optional<ResourceLocation> getPet(UUID playerId) {
        return Optional.ofNullable(activePets.get(playerId));
    }

    public boolean setPet(UUID playerId, ResourceLocation petId) {
        ResourceLocation previous = activePets.put(playerId, petId);
        if (petId.equals(previous)) {
            return false;
        }

        setDirty();
        return true;
    }

    public boolean clearPet(UUID playerId) {
        ResourceLocation removed = activePets.remove(playerId);
        if (removed == null) {
            return false;
        }

        setDirty();
        return true;
    }

    public Map<UUID, ResourceLocation> snapshot() {
        return Map.copyOf(activePets);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag petsTag = new ListTag();
        for (Map.Entry<UUID, ResourceLocation> entry : activePets.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID(KEY_PLAYER, entry.getKey());
            entryTag.putString(KEY_PET, entry.getValue().toString());
            petsTag.add(entryTag);
        }

        tag.put(KEY_PETS, petsTag);
        return tag;
    }
}

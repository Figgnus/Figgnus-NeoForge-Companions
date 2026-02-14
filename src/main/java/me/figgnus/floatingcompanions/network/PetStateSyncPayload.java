package me.figgnus.floatingcompanions.network;

import me.figgnus.floatingcompanions.FloatingCompanionsMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PetStateSyncPayload(Map<UUID, ResourceLocation> activePets) implements CustomPacketPayload {
    public static final Type<PetStateSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FloatingCompanionsMod.MOD_ID, "pet_state_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PetStateSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(HashMap::new, UUIDUtil.STREAM_CODEC, ResourceLocation.STREAM_CODEC),
                    PetStateSyncPayload::activePets,
                    PetStateSyncPayload::new
            );

    public PetStateSyncPayload {
        activePets = Map.copyOf(activePets);
    }

    @Override
    public Type<PetStateSyncPayload> type() {
        return TYPE;
    }
}

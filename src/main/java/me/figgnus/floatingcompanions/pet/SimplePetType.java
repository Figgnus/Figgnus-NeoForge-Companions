package me.figgnus.floatingcompanions.pet;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record SimplePetType(ResourceLocation id, Component displayName) implements PetType {
    public static SimplePetType create(ResourceLocation id) {
        return new SimplePetType(id, Component.translatable("pet." + id.getNamespace() + "." + id.getPath()));
    }
}

package me.figgnus.floatingcompanions.pet;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface PetType {
    ResourceLocation id();

    Component displayName();
}

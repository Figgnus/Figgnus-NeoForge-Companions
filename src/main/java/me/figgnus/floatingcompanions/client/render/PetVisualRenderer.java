package me.figgnus.floatingcompanions.client.render;

import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@FunctionalInterface
public interface PetVisualRenderer {
    void render(RenderPlayerEvent.Post event);
}

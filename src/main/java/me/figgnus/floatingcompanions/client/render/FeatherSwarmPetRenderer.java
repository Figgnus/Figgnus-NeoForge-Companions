package me.figgnus.floatingcompanions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.UUID;

public final class FeatherSwarmPetRenderer implements PetVisualRenderer{
    private static final OrbitalFeather[] FEATHER_ORBITS = new OrbitalFeather[] {
            new OrbitalFeather(0.90F, 1.10D, 0.11F,  1, 0.00F, 0.25F, 0.06F),
            new OrbitalFeather(1.10F, 1.40D, 0.10F, -1, 1.20F, 0.22F, 0.07F),
            new OrbitalFeather(0.80F, 1.70D, 0.13F,  1, 2.40F, 0.28F, 0.05F),
            new OrbitalFeather(1.20F, 1.00D, 0.09F, -1, 3.60F, 0.20F, 0.08F),
            new OrbitalFeather(1.00F, 1.50D, 0.12F,  1, 4.80F, 0.24F, 0.06F)
    };

    private ItemEntity[] cachedFeathers;
    private Level cachedLevel;

    @Override
    public void render(RenderPlayerEvent.Post event) {
        Player owner = event.getEntity();
        ItemEntity[] feathers = getOrCreateFeathers(owner.level());
        if (feathers == null) {
            return;
        }

        float partialTick = event.getPartialTick();
        float animationTime = owner.tickCount + partialTick;
        float playerPhase = phaseFromUUID(owner.getUUID());

        for (int i = 0; i < FEATHER_ORBITS.length; i++) {
            OrbitalFeather orbit = FEATHER_ORBITS[i];
            ItemEntity feather = feathers[i];

            float angle = animationTime * orbit.orbitSpeed() * orbit.direction()
                    + orbit.phaseOffset()
                    + playerPhase;

            double offsetX = Mth.cos(angle) * orbit.radius();
            double offsetZ = Mth.sin(angle) * orbit.radius();
            double offsetY = orbit.baseHeight()
                    + Mth.sin(animationTime * orbit.bobSpeed() + orbit.phaseOffset()) * orbit.bobAmplitude();

            float yaw = (float) Math.toDegrees(angle * orbit.direction()) + 90.0F;
            configureFeather(feather, owner, yaw, offsetX, offsetY, offsetZ);

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate(offsetX, offsetY, offsetZ);

            Minecraft.getInstance().getEntityRenderDispatcher().render(
                    feather,
                    0.0D,
                    0.0D,
                    0.0D,
                    yaw,
                    partialTick,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight()
            );

            poseStack.popPose();
        }
    }

    private ItemEntity[] getOrCreateFeathers(Level level) {
        if (cachedFeathers != null && cachedLevel == level) {
            return cachedFeathers;
        }

        ItemEntity[] created = new ItemEntity[FEATHER_ORBITS.length];

        for (int i = 0; i < FEATHER_ORBITS.length; i++) {
            if (!(EntityType.ITEM.create(level) instanceof ItemEntity feather)) {
                return null;
            }

            feather.setItem(new ItemStack(Items.FEATHER));
            feather.setNoGravity(true);
            created[i] = feather;
        }

        cachedFeathers = created;
        cachedLevel = level;
        return cachedFeathers;
    }

    private static void configureFeather(ItemEntity feather, Player owner, float yaw, double ox, double oy, double oz) {
        feather.setPos(owner.getX() + ox, owner.getY() + oy, owner.getZ() + oz);
        feather.setOldPosAndRot();
        feather.tickCount = owner.tickCount;

        feather.setYRot(yaw);
        feather.yRotO = yaw;
        feather.setDeltaMovement(0.0D, 0.0D, 0.0D);
    }

    private static float phaseFromUUID(UUID playerId) {
        long mixed = playerId.getMostSignificantBits() ^ playerId.getLeastSignificantBits();
        return (mixed & 0xFFFFL) / 65535.0F * Mth.TWO_PI;
    }

    private record OrbitalFeather(
            float radius,
            double baseHeight,
            float orbitSpeed,
            int direction,
            float phaseOffset,
            float bobSpeed,
            float bobAmplitude
    ) {
    }
}

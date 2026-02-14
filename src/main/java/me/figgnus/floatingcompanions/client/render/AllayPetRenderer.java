package me.figgnus.floatingcompanions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.UUID;

public final class AllayPetRenderer implements PetVisualRenderer {
    private static final float ORBIT_RADIUS = 1.1F;
    private static final float ORBIT_SPEED = 0.10F;
    private static final float BOB_SPEED = 0.20F;
    private static final float BOB_HEIGHT = 0.20F;
    private static final double BASE_HEIGHT = 1.35D;

    private Allay cachedAllay;
    private Level cachedLevel;

    @Override
    public void render(RenderPlayerEvent.Post event) {
        Player owner = event.getEntity();
        Allay allay = getOrCreateAllay(owner.level());
        if (allay == null) return;

        float partialTick = event.getPartialTick();
        float animationTime = owner.tickCount + partialTick;
        float angle = animationTime * ORBIT_SPEED + phaseFromUUID(owner.getUUID());

        double offsetX = Mth.cos(angle) * ORBIT_RADIUS;
        double offsetZ = Mth.sin(angle) * ORBIT_RADIUS;
        double offsetY = BASE_HEIGHT + Mth.sin(animationTime * BOB_SPEED) * BOB_HEIGHT;

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);

        float yaw = (float) Math.toDegrees(angle * 2F) + 90.0F;

        configureAllay(allay, owner, yaw, offsetX, offsetY, offsetZ);

        Minecraft.getInstance().getEntityRenderDispatcher().render(
                allay,
                0.0D, 0.0D, 0.0D,
                yaw,
                partialTick,
                poseStack,
                event.getMultiBufferSource(),
                event.getPackedLight()
        );

        poseStack.popPose();
    }

    private Allay getOrCreateAllay(Level level) {
        if (cachedAllay != null && cachedLevel == level) return cachedAllay;
        if (!(EntityType.ALLAY.create(level) instanceof Allay allay)) return null;

        allay.setNoAi(true);
        allay.setNoGravity(true);
        allay.setSilent(true);

        cachedAllay = allay;
        cachedLevel = level;
        return allay;
    }

    private static void configureAllay(Allay allay, Player owner, float yaw, double ox, double oy, double oz) {
        allay.setPos(owner.getX() + ox, owner.getY() + oy, owner.getZ() + oz);
        allay.setOldPosAndRot();
        allay.tickCount = owner.tickCount;

        allay.setYRot(yaw);
        allay.setYHeadRot(yaw);
        allay.setYBodyRot(yaw);
        allay.yRotO = yaw;
        allay.yHeadRotO = yaw;
        allay.yBodyRotO = yaw;

        allay.setDeltaMovement(0.0D, 0.03D, 0.0D);
    }

    private static float phaseFromUUID(UUID playerId) {
        long mixed = playerId.getMostSignificantBits() ^ playerId.getLeastSignificantBits();
        return (mixed & 0xFFFFL) / 65535.0F * Mth.TWO_PI;
    }
}

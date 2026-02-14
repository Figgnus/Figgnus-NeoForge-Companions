package me.figgnus.floatingcompanions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.UUID;

public final class BeePetRenderer implements PetVisualRenderer {
    private static final float ORBIT_RADIUS = 0.9F;
    private static final float ORBIT_SPEED = 0.12F;
    private static final float BOB_SPEED = 0.22F;
    private static final float BOB_HEIGHT = 0.15F;
    private static final double BASE_HEIGHT = 1.2D;

    private Bee cachedBee;
    private Level cachedLevel;

    @Override
    public void render(RenderPlayerEvent.Post event) {
        Player owner = event.getEntity();
        Bee bee = getOrCreateBee(owner.level());
        if (bee == null) {
            return;
        }

        float partialTick = event.getPartialTick();
        float animationTime = owner.tickCount + partialTick;
        float angle = animationTime * ORBIT_SPEED + phaseFromUUID(owner.getUUID());

        double offsetX = Mth.cos(angle) * ORBIT_RADIUS;
        double offsetZ = Mth.sin(angle) * ORBIT_RADIUS;
        double offsetY = BASE_HEIGHT + Mth.sin(animationTime * BOB_SPEED) * BOB_HEIGHT;

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(offsetX, offsetY, offsetZ);

        float yaw = (float) Math.toDegrees(angle * 0.5F) + 90.0F;
        configureBee(bee, owner, yaw, offsetX, offsetY, offsetZ);

        Minecraft.getInstance().getEntityRenderDispatcher().render(
                bee,
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

    private Bee getOrCreateBee(Level level) {
        if (cachedBee != null && cachedLevel == level) {
            return cachedBee;
        }

        if (!(EntityType.BEE.create(level) instanceof Bee bee)) {
            return null;
        }

        bee.setNoAi(true);
        bee.setNoGravity(true);
        bee.setSilent(true);

        this.cachedBee = bee;
        this.cachedLevel = level;
        return bee;
    }

    private static void configureBee(Bee bee, Player owner, float yaw, double offsetX, double offsetY, double offsetZ) {
        double worldX = owner.getX() + offsetX;
        double worldY = owner.getY() + offsetY;
        double worldZ = owner.getZ() + offsetZ;

        bee.setPos(worldX, worldY, worldZ);
        bee.setOldPosAndRot();
        bee.tickCount = owner.tickCount;

        bee.setYRot(yaw);
        bee.setYHeadRot(yaw);
        bee.setYBodyRot(yaw);
        bee.yRotO = yaw;
        bee.yHeadRotO = yaw;
        bee.yBodyRotO = yaw;

        bee.setDeltaMovement(0.0D, 0.08D, 0.0D);
    }

    private static float phaseFromUUID(UUID playerId) {
        long mixed = playerId.getMostSignificantBits() ^ playerId.getLeastSignificantBits();
        return (mixed & 0xFFFFL) / 65535.0F * Mth.TWO_PI;
    }
}

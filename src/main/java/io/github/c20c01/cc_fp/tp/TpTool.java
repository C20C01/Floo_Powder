package io.github.c20c01.cc_fp.tp;

import com.mojang.logging.LogUtils;
import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlock;
import io.github.c20c01.cc_fp.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.cc_fp.item.PortalWand;
import io.github.c20c01.cc_fp.item.flooReel.ExpansionReel;
import io.github.c20c01.cc_fp.network.CCNetwork;
import io.github.c20c01.cc_fp.particle.SendParticle;
import io.github.c20c01.cc_fp.savedData.Permission;
import io.github.c20c01.cc_fp.savedData.PermissionManager;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import io.github.c20c01.cc_fp.tool.Delayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TpTool {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<Entity> TP_COOLING = new HashSet<>(); // 保证同一实体一段时间内不会多次尝试传送

    public enum Result {success, fail, pass}

    public static void forceTeleportEntity(Entity entity, String targetName) {
        tryTeleportEntity(entity, targetName, Vec3.ZERO, Boolean.FALSE, Boolean.TRUE);
    }

    /**
     * @param entity     尝试传送的实体
     * @param targetName 目标传送点的名称
     * @param movement   实体传送时的动量，若为null则不保留动量
     * @param temporary  是由传送法杖生成的传送点
     * @param force      传送指令不会被忽略（不返回Result.pass）
     * @return Result.pass 这10tick内此实体已经尝试传送了，不再对其进行处理<br>
     * Result.fail 拒绝传送<br>
     * Result.success 尝试进行传送<br>
     */
    public static Result tryTeleportEntity(Entity entity, String targetName, @Nullable Vec3 movement, boolean temporary, boolean force) {
        if (!force && !TP_COOLING.add(entity)) {
            return Result.pass;
        } else {
            Delayer.add(10, () -> TP_COOLING.remove(entity));
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            printRejectedTeleportLog(entity, "Missing server");
            return Result.fail;
        }

        ServerLevel targetLevel;
        BlockPos targetPos;
        Direction direction = null;

        if (temporary) {
            GlobalPos target = PortalWand.getWandPoint(targetName);
            if (target == null) {
                printRejectedTeleportLog(entity, "Missing wand point");
                return Result.fail;
            }

            targetLevel = server.getLevel(target.dimension());
            if (targetLevel == null) {
                printRejectedTeleportLog(entity, "Missing server level");
                return Result.fail;
            }

            targetPos = target.pos();
            if (!targetLevel.getBlockState(targetPos).is(CCMain.FAKE_PORTAL_FIRE_BLOCK.get())) {
                printRejectedTeleportLog(entity, "Missing fake portal fire");
                return Result.fail;
            }

        } else {
            PortalPoint target = PortalPointManager.get(server).get(targetName, Boolean.TRUE);
            if (target == null) {
                printRejectedTeleportLog(entity, "Missing portal point");
                return Result.fail;
            }

            targetLevel = target.getLevel(server);
            if (targetLevel == null) {
                printRejectedTeleportLog(entity, "Missing server level");
                return Result.fail;
            }

            targetPos = target.pos();
            BlockEntity temp = targetLevel.getBlockEntity(targetPos.below());
            PortalPointBlockEntity blockEntity = temp instanceof PortalPointBlockEntity ? (PortalPointBlockEntity) temp : null;
            if (blockEntity == null) {
                PortalPointManager.get(server).remove(targetName);
                printRejectedTeleportLog(entity, "Missing portal point blockEntity");
                return Result.fail;
            }

            boolean notOwner = !entity.getUUID().equals(target.ownerUid());

            if (notOwner && blockEntity.checkNoneReel((reel) -> reel.allow(entity))) {
                printRejectedTeleportLog(entity, "Wrong entity type");
                return Result.fail;
            }

            if (entity instanceof ServerPlayer && notOwner && blockEntity.checkNoneReel(ExpansionReel::allowEveryone)) {
                Permission permission = PermissionManager.get(server).get(target.ownerUid());
                if (permission == null || !permission.contains(entity.getUUID())) {
                    printRejectedTeleportLog(entity, "Lack of permission");
                    return Result.fail;
                }
            }

            ServerTick.addTask(() -> {
                changeTargetFire(targetLevel, targetPos);
                PortalPointBlock.signalPulse(targetLevel, targetPos.below());
            });

            if (blockEntity.checkNoneReel(ExpansionReel::saveDirection)) {
                direction = blockEntity.getBlockState().getValue(PortalPointBlock.FACING);
            }
        }

        boolean showDecoration = entity instanceof LivingEntity;

        TpContext context = new TpContext(entity, targetLevel, targetPos, getNewMovement(movement, direction), direction, showDecoration);
        ServerTick.addTask(() -> teleportTo(context));
        ServerTick.register();
        return Result.success;
    }

    private static void changeTargetFire(ServerLevel targetLevel, BlockPos blockPos) {
        if (targetLevel.getBlockState(blockPos).getBlock() instanceof BaseFireBlock) {
            BasePortalFireBlock.changeAllToFakeFire(blockPos, targetLevel);
        }
    }

    /**
     * 若保留动量则会将y方向的动量设置为向上的，<br>同时若不保留方向还会将水平方向上的动量的方向调至传送核心的朝向
     */
    private static Vec3 getNewMovement(@Nullable Vec3 movement, @Nullable Direction direction) {
        if (movement == null) {
            return Vec3.ZERO;
        }
        double my = movement.y < 0 ? -movement.y : movement.y;
        if (direction == null) {
            return new Vec3(movement.x, my, movement.z);
        }
        double v = Mth.length(movement.x, movement.z);
        double mx = direction.getNormal().getX() * v;
        double mz = direction.getNormal().getZ() * v;
        return new Vec3(mx, my, mz);
    }

    protected static void teleportTo(TpContext context) {
        Entity entity = context.entity();
        ServerLevel targetLevel = context.targetLevel();
        BlockPos targetBlockPos = context.targetBlockPos();
        Vec3 movement = context.movement();
        float yaw = context.direction() == null ? entity.getYRot() : context.direction().toYRot();

        ServerLevel sourceLevel = (ServerLevel) entity.level();
        BlockPos sourceBlockPos = null;
        Vec3 sourcePos = null;

        if (context.showDecoration()) {
            sourceBlockPos = entity.blockPosition();
            sourcePos = new Vec3(entity.getX(), (entity.getY() + entity.getEyeY()) / 2, entity.getZ());
        }

        addFireResistance(entity);

        Entity mount = entity.getVehicle();
        boolean rideMinecart = mount != null && mount.getType().equals(EntityType.MINECART);

        if (rideMinecart && sourceLevel == targetLevel) {
            teleportEntity(mount, targetLevel, Vec3.atBottomCenterOf(targetBlockPos), yaw, movement);
        } else {
            entity.stopRiding();
            entity = teleportEntity(entity, targetLevel, Vec3.atBottomCenterOf(targetBlockPos), yaw, movement);
            if (rideMinecart) {
                mount = teleportEntity(mount, targetLevel, Vec3.atBottomCenterOf(targetBlockPos), yaw, movement);
                entity.startRiding(mount);
            }
        }

        if (context.showDecoration()) {
            sourceLevel.playSound(null, sourceBlockPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.5F, 1.0F);
            targetLevel.playSound(null, targetBlockPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.5F, 1.0F);

            SendParticle.ball(sourceLevel, SendParticle.Particles.SMOKE, sourcePos);
            SendParticle.ball(targetLevel, SendParticle.Particles.SMOKE, Vec3.atCenterOf(targetBlockPos));
        }

    }

    private static void addFireResistance(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            addEffect(livingEntity, MobEffects.FIRE_RESISTANCE, 200, 0);
        }
    }

    public static void addEffect(LivingEntity entity, MobEffect effect, int duration, int amplifier) {
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
    }

    private static Entity teleportEntity(Entity entity, ServerLevel targetWorld, Vec3 targetPos3d, float yaw, Vec3 movement) {
        double x = targetPos3d.x;
        double y = targetPos3d.y;
        double z = targetPos3d.z;

        ChunkPos chunkPos = new ChunkPos(Mth.floor(x), Mth.floor(z));
        targetWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());

        if (entity instanceof ServerPlayer serverPlayer) {
            entity.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }

            if (targetWorld == entity.level()) {
                serverPlayer.connection.teleport(x, y, z, yaw, entity.getXRot());
            } else {
                serverPlayer.teleportTo(targetWorld, x, y, z, yaw, entity.getXRot());
            }

            sendPlayerMovement(serverPlayer, movement);

            entity.setYHeadRot(yaw);
        } else {
            float pitch = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
            if (targetWorld == entity.level()) {
                entity.moveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
                entity.setDeltaMovement(movement);
            } else {
                entity.unRide();
                Entity oldEntity = entity;
                entity = entity.getType().create(targetWorld);
                if (entity == null) {
                    return oldEntity;
                }
                entity.restoreFrom(oldEntity);
                entity.moveTo(x, y, z, yaw, pitch);
                entity.setYHeadRot(yaw);
                entity.setDeltaMovement(movement);
                oldEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                targetWorld.addDuringTeleport(entity);
            }
        }

        if (entity instanceof PathfinderMob) {
            ((PathfinderMob) entity).getNavigation().stop();
        }

        return entity;
    }

    /**
     * 将玩家的动量发到客户端进行设置
     */
    private static void sendPlayerMovement(ServerPlayer serverPlayer, Vec3 movement) {
        CCNetwork.CHANNEL_Movement_TO_C.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new CCNetwork.MovementPacket((float) movement.x, (float) movement.y, (float) movement.z));
    }

    /**
     * 仅客户端使用，通过{@link CCNetwork#CHANNEL_Movement_TO_C}接收服务端发送的动量
     */
    public static void changePlayerMovement(float mx, float my, float mz) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            LOGGER.info("Rejected change movement due to missing local inserted");
        } else {
            player.setDeltaMovement(mx, my, mz);
        }
    }

    public static String getItemName(ItemStack itemStack) {
        String name = itemStack.getDisplayName().getString();
        return name.substring(1, name.length() - 1);
    }

    private static void printRejectedTeleportLog(Entity entity, String reason) {
        String name = entity.getName().getString();
        String type = entity.getType().getDescription().getString();
        LOGGER.info("{} was rejected teleport due to: {}", name.equals(type) ? name : String.format("%s(%s)", name, type), reason);
    }

    @Mod.EventBusSubscriber(modid = CCMain.ID)
    public static class ServerStopping {
        @SubscribeEvent
        public static void stoppingEvent(ServerStoppingEvent event) {
            TP_COOLING.clear();
        }
    }
}

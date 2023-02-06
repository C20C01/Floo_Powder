package io.github.c20c01.tp;

import com.mojang.logging.LogUtils;
import io.github.c20c01.CCMain;
import io.github.c20c01.block.FireBaseBlock;
import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.block.portalPoint.PortalPointBlock;
import io.github.c20c01.block.portalPoint.PortalPointBlockEntity;
import io.github.c20c01.item.flooReel.ExpansionReel;
import io.github.c20c01.network.CCNetwork;
import io.github.c20c01.particle.SendParticle;
import io.github.c20c01.savedData.Permission;
import io.github.c20c01.savedData.PermissionManager;
import io.github.c20c01.savedData.PortalPoint;
import io.github.c20c01.savedData.PortalPointManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TpTool {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<Entity> entitySet = new HashSet<>(); // 保证同一实体1tick内不会多次尝试传送

    public enum Result {success, fail, pass}

    public static Result forceTeleportEntity(Entity entity, String targetName) {
        return tryTeleportEntity(entity, targetName, Blocks.AIR, Vec3.ZERO, true);
    }

    /**
     * @param entity     尝试传送的实体
     * @param targetName 目标传送点的名称
     * @param fireBase   传送火焰的底座
     * @param movement   实体传送时的动量
     * @param force      传送指令不会被忽略（不返回Result.pass）
     * @return Result.pass 这1tick内此实体已经尝试传送了，不再对其进行处理<br>
     * Result.fail 拒绝传送<br>
     * Result.success 尝试进行传送<br>
     */
    public static Result tryTeleportEntity(Entity entity, String targetName, Block fireBase, Vec3 movement, boolean force) {
        if (!force && !entitySet.add(entity)) {
            ServerTick.register();
            return Result.pass;
        }

        MinecraftServer server = entity.getServer();
        if (server == null) {
            LOGGER.info("Rejected teleport due to missing server");
            return Result.fail;
        }

        PortalPointManager pointManager = PortalPointManager.get(server);
        PortalPoint target = pointManager.get(targetName);
        if (target == null) {
            LOGGER.info("Rejected teleport due to missing portal point");
            return Result.fail;
        }

        Direction direction = null;
        ServerLevel targetLevel = target.getLevel(server);

        if (target.isTemporary()) {
            if (!targetLevel.getBlockState(target.pos()).is(CCMain.FAKE_PORTAL_FIRE_BLOCK.get())) {
                pointManager.remove(targetName);
                LOGGER.info("Rejected teleport due to missing fake portal fire");
                return Result.fail;
            }

        } else {
            BlockEntity temp = targetLevel.getBlockEntity(target.pos().below());
            PortalPointBlockEntity blockEntity = temp instanceof PortalPointBlockEntity ? (PortalPointBlockEntity) temp : null;
            if (blockEntity == null) {
                pointManager.remove(targetName);
                LOGGER.info("Rejected teleport due to missing portal point blockEntity");
                return Result.fail;
            }

            boolean notOwner = !entity.getUUID().equals(target.ownerUid());

            if (notOwner && blockEntity.checkNoneReel((reel) -> reel.allow(entity))) {
                LOGGER.info("Rejected teleport due to wrong entity type");
                return Result.fail;
            }

            if (entity instanceof ServerPlayer && notOwner && blockEntity.checkNoneReel(ExpansionReel::allowEveryone)) {
                Permission permission = PermissionManager.get(server).get(target.ownerUid());
                if (permission == null || !permission.isFriend(entity.getUUID())) {
                    LOGGER.info("Rejected teleport due to lack of permission");
                    return Result.fail;
                }
            }

            changeTargetFire(targetLevel, target.pos());

            if (blockEntity.checkNoneReel(ExpansionReel::saveDirection)) {
                direction = blockEntity.getBlockState().getValue(PortalPointBlock.FACING);
            }

        }

        boolean saveMovement = fireBase instanceof FireBaseBlock;
        movement = saveMovement ? getNewMovement(movement, direction) : Vec3.ZERO;

        boolean showDecoration = entity instanceof LivingEntity;

        TpContext context = new TpContext(entity, targetLevel, target.pos(), movement, direction, showDecoration);
        ServerTick.add(context);
        return Result.success;
    }

    public static void cleanSet() {
        entitySet.clear();
    }

    private static void changeTargetFire(ServerLevel targetLevel, BlockPos blockPos) {
        if (targetLevel.getBlockState(blockPos).getBlock() instanceof BaseFireBlock) {
            BasePortalFireBlock.changeAllToFakeFire(blockPos, targetLevel);
        }
    }

    /**
     * 若保留动量则会将y方向的动量设置为向上的，<br>同时若不保留方向还会将水平方向上的动量的方向调至传送核心的朝向
     */
    private static Vec3 getNewMovement(Vec3 movement, @Nullable Direction direction) {
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
        Direction direction = context.direction() == null ? entity.getDirection() : context.direction();

        ServerLevel sourceLevel = (ServerLevel) entity.level;
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
            teleportEntity(mount, targetLevel, Vec3.atBottomCenterOf(targetBlockPos), direction, movement);
        } else {
            entity.stopRiding();
            entity = teleportEntity(entity, targetLevel, Vec3.atBottomCenterOf(targetBlockPos), direction, movement);
            if (rideMinecart) {
                mount = teleportEntity(mount, targetLevel, Vec3.atBottomCenterOf(targetBlockPos), direction, movement);
                entity.startRiding(mount);
            }
        }

        if (context.showDecoration()) {
            sourceLevel.playSound(null, sourceBlockPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.1f, 1f);
            targetLevel.playSound(null, targetBlockPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 0.1f, 1f);

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

    private static Entity teleportEntity(Entity entity, ServerLevel targetWorld, Vec3 targetPos3d, Direction direction, Vec3 movement) {
        float yaw = direction.toYRot();
        double x = targetPos3d.x;
        double y = targetPos3d.y;
        double z = targetPos3d.z;

        ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
        targetWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());

        if (entity instanceof ServerPlayer serverPlayer) {
            entity.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }

            if (targetWorld == entity.level) {
                serverPlayer.connection.teleport(x, y, z, yaw, entity.getXRot());
            } else {
                serverPlayer.teleportTo(targetWorld, x, y, z, yaw, entity.getXRot());
            }

            sendPlayerMovement(serverPlayer, movement);

            entity.setYHeadRot(yaw);
        } else {
            float pitch = Mth.clamp(entity.getXRot(), -90.0F, 90.0F);
            if (targetWorld == entity.level) {
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
            LOGGER.info("Rejected change movement due to missing local player");
        } else {
            player.setDeltaMovement(mx, my, mz);
        }
    }

    public static String getItemName(ItemStack itemStack) {
        String name = itemStack.getDisplayName().getString();
        return name.substring(1, name.length() - 1);
    }
}

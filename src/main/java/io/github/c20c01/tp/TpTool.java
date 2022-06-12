package io.github.c20c01.tp;

import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.pos.PosMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;

public class TpTool {

    public static void gogo(LivingEntity entity, String name, Level level, BlockPos blockPos) {
        new Thread(() -> {
            PosInfo posInfo = PosMap.get(name);
            if (posInfo == null) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    var text = new TranslatableComponent(CCMain.TEXT_NOT_FOUND);
                    serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
                }
                level.playSound(null, blockPos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 8.0F, 0.9F);
            } else if (posInfo.noNull()) {
                try {
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, false));
                    Thread.sleep(100);
                    teleportTo(entity, posInfo.level, Vec3.atBottomCenterOf(posInfo.blockPos));
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, false));
                    level.playSound(null, posInfo.blockPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 8.0F, 0.9F + level.random.nextFloat() * 0.2F);
                } catch (Exception ignore) {
                }
            } else {
                if (entity instanceof ServerPlayer serverPlayer) {
                    var text = new TranslatableComponent(CCMain.TEXT_NOT_LOADED);
                    serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
                }
                level.playSound(null, blockPos, SoundEvents.WOLF_WHINE, SoundSource.BLOCKS, 8.0F, 0.9F);
            }
        }).start();
    }

    private static void teleportTo(LivingEntity entity, ServerLevel targetLevel, Vec3 pos) {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        if (entity instanceof ServerPlayer serverPlayer) {
            float yRot = entity.getDirection().toYRot();
            float xRot = entity.getXRot();
            ChunkPos chunkpos = new ChunkPos(new BlockPos(pos));
            targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entity.getId());
            entity.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }

            if (targetLevel == entity.level) {
                serverPlayer.connection.teleport(x, y, z, yRot, xRot);
            } else {
                tpToAnotherDimension(serverPlayer, targetLevel, x, y, z, yRot, xRot);
            }
        } else {
            if (targetLevel == entity.level) {
                entity.moveTo(x, y, z);
            } else {
                entity.unRide();
                Entity oldEntity = entity;
                entity = (LivingEntity) entity.getType().create(targetLevel);
                if (entity == null) return;
                entity.restoreFrom(oldEntity);
                entity.moveTo(x, y, z);
                oldEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                targetLevel.addDuringTeleport(entity);
            }
        }

        if (!entity.isFallFlying()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0, 1));
            entity.setOnGround(true);
        }

        if (entity instanceof PathfinderMob) ((PathfinderMob) entity).getNavigation().stop();
    }

    private static void tpToAnotherDimension(ServerPlayer serverPlayer, ServerLevel targetLevel, double x, double y, double z, float yRot, float xRot) {
        ServerLevel serverlevel = serverPlayer.getLevel();
        serverPlayer.connection.send(new ClientboundRespawnPacket(targetLevel.dimensionTypeRegistration(), targetLevel.dimension(), BiomeManager.obfuscateSeed(targetLevel.getSeed()), serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.gameMode.getPreviousGameModeForPlayer(), targetLevel.isDebug(), targetLevel.isFlat(), true));
        serverPlayer.server.getPlayerList().sendPlayerPermissionLevel(serverPlayer);
        serverlevel.removePlayerImmediately(serverPlayer, Entity.RemovalReason.CHANGED_DIMENSION);
        serverPlayer.revive();
        serverPlayer.moveTo(x, y, z, yRot, xRot);
        serverPlayer.setLevel(targetLevel);
        targetLevel.addDuringCommandTeleport(serverPlayer);
        serverPlayer.connection.teleport(x, y, z, yRot, xRot);
        serverPlayer.gameMode.setLevel(targetLevel);
        serverPlayer.server.getPlayerList().sendLevelInfo(serverPlayer, targetLevel);
        serverPlayer.server.getPlayerList().sendAllPlayerInfo(serverPlayer);
        net.minecraftforge.event.ForgeEventFactory.firePlayerChangedDimensionEvent(serverPlayer, serverlevel.dimension(), targetLevel.dimension());
    }
}

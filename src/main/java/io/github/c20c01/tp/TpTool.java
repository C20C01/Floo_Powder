package io.github.c20c01.tp;

import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.pos.PosMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
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
                    teleportTo(entity, posInfo.level, Vec3.atBottomCenterOf(posInfo.blockPos));
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
                serverPlayer.moveTo(x, y, z);
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

    public static void tpToAnotherDimension(ServerPlayer serverPlayer, ServerLevel targetLevel, double x, double y, double z, float yRot, float xRot) {
        test(serverPlayer,"1");
        ServerLevel serverlevel = serverPlayer.getLevel();
        test(serverPlayer,"2");
        serverPlayer.connection.send(new ClientboundRespawnPacket(targetLevel.dimensionTypeRegistration(), targetLevel.dimension(), BiomeManager.obfuscateSeed(targetLevel.getSeed()), serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.gameMode.getPreviousGameModeForPlayer(), targetLevel.isDebug(), targetLevel.isFlat(), true));
        test(serverPlayer,"3");
        serverPlayer.server.getPlayerList().sendPlayerPermissionLevel(serverPlayer);
        test(serverPlayer,"4");
        serverlevel.removePlayerImmediately(serverPlayer, Entity.RemovalReason.CHANGED_DIMENSION);
        test(serverPlayer,"5");
        serverPlayer.revive();
        test(serverPlayer,"6");
        serverPlayer.moveTo(x, y, z, yRot, xRot);
        test(serverPlayer,"7");
        serverPlayer.setLevel(targetLevel);
        test(serverPlayer,"8");
        targetLevel.addDuringCommandTeleport(serverPlayer);
        test(serverPlayer,"9");
        serverPlayer.connection.teleport(x, y, z, yRot, xRot);
        test(serverPlayer,"10");
        serverPlayer.gameMode.setLevel(targetLevel);
        test(serverPlayer,"11");
        serverPlayer.server.getPlayerList().sendLevelInfo(serverPlayer, targetLevel);
        test(serverPlayer,"12");
        serverPlayer.server.getPlayerList().sendAllPlayerInfo(serverPlayer);
        test(serverPlayer,"13");
        net.minecraftforge.event.ForgeEventFactory.firePlayerChangedDimensionEvent(serverPlayer, serverlevel.dimension(), targetLevel.dimension());
        test(serverPlayer,"14");
    }

    private static void test(ServerPlayer serverPlayer, String str) {
        var text = new TextComponent(str);
        serverPlayer.sendMessage(text, ChatType.CHAT, Util.NIL_UUID);
    }
}

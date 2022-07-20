package io.github.c20c01.tp;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.delay.DelayTool;
import io.github.c20c01.pos.PosInfo;
import io.github.c20c01.pos.PosMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.BaseFireBlock;
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
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, false));
            entity.fallDistance = -Float.MAX_VALUE;  // 尝试去除掉落伤害
            try {
                var targetLevel = posInfo.level;
                var targetBlockPos = posInfo.blockPos;
                loadArea(targetBlockPos, targetLevel, entity);
                changeEndFire(targetBlockPos, targetLevel);
                new DelayTool(1, () -> teleportTo(entity, targetLevel, Vec3.atBottomCenterOf(targetBlockPos)));
            } catch (Exception e) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    var text = new TextComponent(e.getMessage());
                    serverPlayer.sendMessage(text, ChatType.CHAT, Util.NIL_UUID);
                }
            }
            level.playSound(null, posInfo.blockPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 8.0F, 0.9F + level.random.nextFloat() * 0.2F);
        } else {
            if (entity instanceof ServerPlayer serverPlayer) {
                var text = new TranslatableComponent(CCMain.TEXT_NOT_LOADED);
                serverPlayer.sendMessage(text, ChatType.GAME_INFO, Util.NIL_UUID);
            }
            level.playSound(null, blockPos, SoundEvents.WOLF_WHINE, SoundSource.BLOCKS, 8.0F, 0.9F);
        }
    }

    private static void loadArea(BlockPos blockPos, ServerLevel targetLevel, LivingEntity entity) {
        ChunkPos chunkpos = new ChunkPos(blockPos);
        targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entity.getId());
    }

    private static void changeEndFire(BlockPos blockPos, ServerLevel targetLevel) {
        if (targetLevel.getBlockState(blockPos).getBlock() instanceof BaseFireBlock) {
            BasePortalFireBlock.changeAllFireBlock(blockPos, targetLevel, null);
        }
    }

    private static void teleportTo(LivingEntity entity, ServerLevel targetLevel, Vec3 pos) {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        if (entity instanceof ServerPlayer serverPlayer) {
            float yRot = entity.getDirection().toYRot();
            float xRot = entity.getXRot();
            entity.stopRiding();
            if (serverPlayer.isSleeping()) {
                serverPlayer.stopSleepInBed(true, true);
            }

            if (targetLevel == entity.level) {
                serverPlayer.moveTo(x, y, z);
            } else {
                serverPlayer.teleportTo(targetLevel, x, y, z, yRot, xRot);
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
}

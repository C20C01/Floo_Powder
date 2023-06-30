package io.github.c20c01.cc_fp.tp;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * @param entity         尝试传送的实体
 * @param targetLevel    目的地所在世界
 * @param targetBlockPos 目的地方块坐标
 * @param movement       实体传送时的动量
 * @param direction      实体的朝向（为空则与传送核心朝向一致）
 * @param showDecoration 播放音效、粒子效果
 * @see TpTool
 */

record TpContext(Entity entity, ServerLevel targetLevel, BlockPos targetBlockPos, Vec3 movement,
                 @Nullable Direction direction, boolean showDecoration) {
}

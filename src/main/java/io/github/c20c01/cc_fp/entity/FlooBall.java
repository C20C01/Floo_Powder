package io.github.c20c01.cc_fp.entity;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.block.portalFire.PortalFireBlockEntity;
import io.github.c20c01.cc_fp.tool.Delayer;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class FlooBall extends ThrowableItemProjectile {
    public FlooBall(EntityType<? extends FlooBall> type, Level level) {
        super(type, level);
    }

    public FlooBall(Level level, LivingEntity livingEntity) {
        super(CCMain.FLOO_BALL_ENTITY.get(), livingEntity, level);
    }

    @Override
    protected Item getDefaultItem() {
        return CCMain.FLOO_BALL_ITEM.get();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        remove(RemovalReason.DISCARDED);
        super.onHit(hitResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (level.isClientSide) {
            return;
        }
        String name = TpTool.getItemName(getItem());
        if (!name.isEmpty()) {
            TpTool.forceTeleportEntity(hitResult.getEntity(), name);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        BlockPos blockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        Delayer.add(2, () -> {
            if (level.getBlockState(blockPos).isAir() && BasePortalFireBlock.canSurviveOnBlock(level, blockPos.below())) {
                level.setBlockAndUpdate(blockPos, CCMain.PORTAL_FIRE_BLOCK.get().defaultBlockState());
                if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
                    blockEntity.setTargetName(TpTool.getItemName(getItem()));
                }
            } else {
                level.addFreshEntity(new ItemEntity(level, this.getX(), this.getY(), this.getZ(), getItem()));
            }
        });
    }
}

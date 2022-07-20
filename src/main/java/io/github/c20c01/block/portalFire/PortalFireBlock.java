package io.github.c20c01.block.portalFire;

import io.github.c20c01.CCMain;
import io.github.c20c01.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFireBlock extends BasePortalFireBlock implements EntityBlock {
    public PortalFireBlock(BlockBehaviour.Properties properties, float damage) {
        super(properties, damage);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalFireBlockEntity(pos, state);
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (level instanceof ServerLevel serverLevel) {
            if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().is(CCMain.FLOO_POWDER_ITEM.get()))
                entity.hurt(DamageSource.IN_FIRE, 1F);
            else if (entity instanceof LivingEntity livingEntity) {
                if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
                    TpTool.gogo(livingEntity, blockEntity.name, level, blockPos);
                    if (level.getBlockState(blockPos).is(CCMain.PORTAL_FIRE_BLOCK.get()) && !blockEntity.lasting)
                        serverLevel.removeBlock(blockPos, false);
                }
            }
        }
    }
}
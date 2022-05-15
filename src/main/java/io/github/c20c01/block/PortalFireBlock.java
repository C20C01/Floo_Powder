package io.github.c20c01.block;

import io.github.c20c01.item.CCItems;
import io.github.c20c01.tp.TpTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class PortalFireBlock extends BaseFireBlock implements EntityBlock {
    private HashSet<BlockPos> allPortal;
    private String removeName;
    private Level removeSLevel;

    public PortalFireBlock(BlockBehaviour.Properties properties, float damage) {
        super(properties, damage);
    }

    @Override
    protected boolean canBurn(@NotNull BlockState p_49284_) {
        return true;
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PortalFireBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState blockState1, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPos, @NotNull BlockPos blockPos1) {
        return this.canSurvive(blockState, levelAccessor, blockPos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(@NotNull BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return canSurviveOnBlock(levelReader.getBlockState(blockPos.below()));
    }

    public static boolean canSurviveOnBlock(BlockState blockState) {
        return !blockState.isAir();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(@NotNull BlockState p_53012_, @NotNull Fluid p_53013_) {
        return false;
    }

    @Override
    public void entityInside(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Entity entity) {
        if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().is(CCItems.FlooPowder))
            entity.hurt(DamageSource.IN_FIRE, 1F);
        if (entity instanceof LivingEntity livingEntity) {
            if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
                TpTool.gogo(livingEntity, blockEntity.name, level, blockPos);
                if (level instanceof ServerLevel serverLevel)
                    remove(blockPos, serverLevel);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState1, boolean p_60519_) {
        if (level.getBlockState(blockPos).isAir()) {
            remove(blockPos, level);
            allPortal.clear();
        }
        super.onRemove(blockState, level, blockPos, blockState1, p_60519_);
    }

    private void remove(BlockPos blockPos, Level level) {
        removeSLevel = level;
        if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity) {
            removeName = blockEntity.name;
        }
        getAllPortalBlock(blockPos);
        for (BlockPos p : allPortal) level.removeBlock(p, false);
        allPortal.clear();
    }

    private void getAllPortalBlock(BlockPos inPos) {
        allPortal = new HashSet<>();
        allPortal.add(inPos);
        loop(inPos);
    }

    private void loop(BlockPos inPos) {
        BlockPos pos = inPos.north();
        if (isPortalBlock(pos) && allPortal.add(pos)) {
            loop(pos);
        }
        pos = inPos.west();
        if (isPortalBlock(pos) && allPortal.add(pos)) {
            loop(pos);
        }
        pos = inPos.south();
        if (isPortalBlock(pos) && allPortal.add(pos)) {
            loop(pos);
        }
        pos = inPos.east();
        if (isPortalBlock(pos) && allPortal.add(pos)) {
            loop(pos);
        }
    }

    private boolean isPortalBlock(BlockPos pos) {
        if (removeSLevel.getBlockEntity(pos) instanceof PortalFireBlockEntity blockEntity) {
            return removeName.equals(blockEntity.name);
        }
        return false;
    }
}
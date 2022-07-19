package io.github.c20c01.block.portalFire;

import io.github.c20c01.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasePortalFireBlock extends BaseFireBlock {
    private static final int MAXSIZE = 100;// 连锁反应的最大次数
    private static boolean lock = false;

    public BasePortalFireBlock(Properties properties, float damage) {
        super(properties, damage);
    }

    @Override
    protected boolean canBurn(BlockState p_49284_) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return this.canSurvive(blockState, levelAccessor, blockPos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return canSurviveOnBlock(levelReader.getBlockState(blockPos.below()));
    }

    public static boolean canSurviveOnBlock(BlockState blockState) {
        return !blockState.isAir();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState p_53012_, Fluid p_53013_) {
        return false;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean p_60519_) {
        if (blockState1.isAir()) removeAllFireBlock(blockPos, level);
        super.onRemove(blockState, level, blockPos, blockState1, p_60519_);
    }

    public static void removeAllFireBlock(BlockPos inPos, Level level) {
        if (!lock && level instanceof ServerLevel serverLevel) {
            lock = true;
            Predicate<BlockPos> check = pos -> serverLevel.getBlockState(pos).getBlock() instanceof BasePortalFireBlock;
            for (BlockPos pos : getAllFireBlock(inPos, check, MAXSIZE)) {
                serverLevel.removeBlock(pos, false);
            }
            lock = false;
        }
    }

    public static boolean canChangeToPortalFire(BlockPos pos, Level level) {
        return level.getBlockState(pos).getBlock() instanceof BaseFireBlock && level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP);
    }

    public static boolean canChangeToFakeFire(BlockPos pos, Level level) {
        return level.getBlockState(pos.below()).is(CCMain.PORTAL_POINT_BLOCK.get()) && level.getBlockState(pos).getBlock() instanceof BaseFireBlock;
    }

    public static void changeAllFireBlock(BlockPos inPos, Level level, @Nullable String name) {
        if (level instanceof ServerLevel serverLevel) {
            boolean isPortal = name != null;
            if (isPortal) {
                Predicate<BlockPos> check = pos -> canChangeToPortalFire(pos, serverLevel);
                for (BlockPos pos : getAllFireBlock(inPos, check, MAXSIZE)) {
                    changeToPortalFire(pos, serverLevel, name);
                }
            } else {
                Predicate<BlockPos> check = pos -> canChangeToFakeFire(pos, serverLevel);
                for (BlockPos pos : getAllFireBlock(inPos, check, MAXSIZE)) {
                    changeToFakeFire(pos, serverLevel);
                }
            }
        }
    }

    private static void changeToPortalFire(BlockPos inPos, ServerLevel level, String name) {
        level.setBlock(inPos, CCMain.PORTAL_FIRE_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
        if (level.getBlockEntity(inPos) instanceof PortalFireBlockEntity blockEntity) {
            blockEntity.name = name;
            blockEntity.setLevel(level);
        }
    }

    private static void changeToFakeFire(BlockPos inPos, ServerLevel level) {
        level.setBlock(inPos, CCMain.FAKE_PORTAL_FIRE_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    /**
     * @author : Sunbread
     * @see "<a href="https://github.com/Sunbread">Github</a>"
     */
    public static HashSet<BlockPos> getAllFireBlock(BlockPos inPos, Predicate<BlockPos> isFireBlock, int maxSize) {
        HashSet<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(inPos);

        while (!queue.isEmpty() && visited.size() < maxSize) {
            BlockPos current = queue.poll();
            visited.add(current);
            for (var nearby : List.of(current.north(), current.east(), current.south(), current.west())) {
                if (!visited.contains(nearby) && isFireBlock.test(nearby)) {
                    queue.add(nearby);
                }
            }
        }
        return visited;
    }
}
package io.github.c20c01.cc_fp.block.portalFire;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.config.CCConfig;
import io.github.c20c01.cc_fp.item.PortalWand;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class BasePortalFireBlock extends BaseFireBlock {
    public static final BooleanProperty TEMPORARY = BooleanProperty.create("temporary"); //为传送法杖生成的临时火焰

    public BasePortalFireBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noCollission().instabreak().lightLevel((x) -> 15), 0F);
    }

    public static boolean isTemporaryFire(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        return blockState.getBlock() instanceof BasePortalFireBlock && blockState.getValue(TEMPORARY);
    }

    @Override
    protected boolean canBurn(BlockState blockState) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos1) {
        return this.canSurvive(blockState, levelAccessor, blockPos) ? blockState : Blocks.AIR.defaultBlockState();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return canSurviveOnBlock(levelReader, blockPos.below());
    }

    public static boolean canSurviveOnBlock(LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos).isFaceSturdy(levelReader, blockPos, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState blockState, Fluid fluid) {
        return false;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
        if (random.nextInt(24) == 0) {
            level.playLocalSound((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }
    }

    protected void playParticle(ParticleOptions particleOptions, Level level, Vec3 pos, RandomSource random) {
        for (int i = 0; i < 3; ++i) {
            int j = random.nextInt(2) * 2 - 1;
            int k = random.nextInt(2) * 2 - 1;

            double x = pos.x + 0.25 * j;
            double y = pos.y + random.nextFloat();
            double z = pos.z + 0.25 * k;
            double mx = random.nextFloat() * 0.5F * j;
            double my = random.nextFloat() * 0.25F;
            double mz = random.nextFloat() * 0.5F * k;

            level.addParticle(particleOptions, x, y, z, mx, my, mz);
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        // 创造模式下不会触发
        removeAllPortalFire(blockPos, level, blockState);
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
    }

    protected void removeTemporaryFire(Level level, BlockPos blockPos, boolean in) {
        if (isTemporaryFire(level, blockPos) && level instanceof ServerLevel serverLevel) {
            PortalWand.TemporaryFires.removePoint(serverLevel, blockPos, in);
        }
    }

    public static void removeAllPortalFire(BlockPos blockPos, Level level, BlockState blockState) {
        if (level instanceof ServerLevel serverLevel) {
            final int MAXSIZE = CCConfig.maxConversion.get();
            Predicate<BlockPos> check = pos -> serverLevel.getBlockState(pos).equals(blockState);
            for (BlockPos pos : getAllFireBlock(blockPos, check, MAXSIZE)) {
                serverLevel.removeBlock(pos, Boolean.FALSE);
            }
        }
    }


    public static boolean canChangeToPortalFire(BlockPos blockPos, Level level) {
        var block = level.getBlockState(blockPos).getBlock();
        return !(block instanceof BasePortalFireBlock) && block instanceof BaseFireBlock && level.getBlockState(blockPos.below()).isFaceSturdy(level, blockPos, Direction.UP);
    }

    public static void changeAllToPortalFire(BlockPos blockPos, Level level, String targetName) {
        if (level instanceof ServerLevel serverLevel) {
            final int MAXSIZE = CCConfig.maxConversion.get();
            Predicate<BlockPos> check = pos -> canChangeToPortalFire(pos, serverLevel);
            for (BlockPos pos : getAllFireBlock(blockPos, check, MAXSIZE)) {
                changeToPortalFire(pos, serverLevel, targetName);
            }
        }
    }

    private static void changeToPortalFire(BlockPos blockPos, ServerLevel level, String targetName) {
        level.setBlock(blockPos, CCMain.PORTAL_FIRE_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        if (level.getBlockEntity(blockPos) instanceof PortalFireBlockEntity blockEntity)
            blockEntity.setTargetName(targetName);
    }


    public static boolean canChangeToFakeFire(BlockPos blockPos, Level level) {
        return level.getBlockState(blockPos.below()).is(CCMain.PORTAL_POINT_BLOCK.get()) && level.getBlockState(blockPos).getBlock() instanceof BaseFireBlock;
    }

    public static void changeAllToFakeFire(BlockPos blockPos, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            final int MAXSIZE = CCConfig.maxConversion.get();
            Predicate<BlockPos> check = pos -> canChangeToFakeFire(pos, serverLevel);
            for (BlockPos pos : getAllFireBlock(blockPos, check, MAXSIZE)) {
                changeToFakeFire(pos, serverLevel);
            }
        }
    }

    private static void changeToFakeFire(BlockPos blockPos, ServerLevel level) {
        // 实现让终点的火焰变为绿色火焰“FakePortalFireBlock”（仅装饰的传送火焰，并无传送逻辑），一段时间后会被熄灭
        // 下面的代码具体原理不大清楚，但可以实现等待时再等待可以重置等待进度，而且退出再进去后也会继续等待时间
        level.setBlock(blockPos, CCMain.FAKE_PORTAL_FIRE_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        if (level.getBlockTicks().hasScheduledTick(blockPos, CCMain.FAKE_PORTAL_FIRE_BLOCK.get())) {
            level.getBlockTicks().clearArea(new BoundingBox(blockPos));
        }
        level.scheduleTick(blockPos, CCMain.FAKE_PORTAL_FIRE_BLOCK.get(), 60);
    }


    public static boolean canChangeToLastingFire(BlockPos blockPos, Level level) {
        return level.getBlockState(blockPos).is(CCMain.PORTAL_FIRE_BLOCK.get());
    }

    public static void changeAllToLastingFire(BlockPos blockPos, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            final int MAXSIZE = CCConfig.maxConversion.get();
            Predicate<BlockPos> check = pos -> canChangeToLastingFire(pos, serverLevel);
            for (BlockPos pos : getAllFireBlock(blockPos, check, MAXSIZE)) {
                changeToLastingFire(pos, serverLevel);
            }
        }
    }

    private static void changeToLastingFire(BlockPos blockPos, ServerLevel level) {
        PortalFireBlock.setLasting(level, blockPos, Boolean.TRUE);
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
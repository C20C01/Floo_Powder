package io.github.c20c01.cc_fp.block.portalFire;

import io.github.c20c01.cc_fp.client.particles.PortalFireParticle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FakePortalFireBlock extends BasePortalFireBlock {
    // 装饰用的绿火，没有传送逻辑
    public FakePortalFireBlock() {
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(TEMPORARY, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TEMPORARY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        serverLevel.removeBlock(blockPos, Boolean.FALSE);
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
        super.animateTick(blockState, level, blockPos, random);
        playParticle(new PortalFireParticle.Option(PortalFireParticle.Option.TYPE.ONT), level, Vec3.atCenterOf(blockPos), random);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean b) {
        removeTemporaryFire(level, blockPos, Boolean.FALSE);
    }
}

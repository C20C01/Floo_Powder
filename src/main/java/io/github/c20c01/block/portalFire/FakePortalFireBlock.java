package io.github.c20c01.block.portalFire;

import io.github.c20c01.client.particles.PortalFireParticle;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FakePortalFireBlock extends BasePortalFireBlock {
    // 装饰用的绿火，没有传送逻辑
    public FakePortalFireBlock() {
        super();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        serverLevel.removeBlock(blockPos, Boolean.FALSE);
    }

    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        super.animateTick(blockState, level, blockPos, random);
        playParticle(new PortalFireParticle.Option(PortalFireParticle.Option.TYPE.ONT), level, Vec3.atCenterOf(blockPos), random);
    }
}

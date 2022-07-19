package io.github.c20c01.block.portalFire;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FakePortalFireBlock extends BasePortalFireBlock {
    // 装饰用的绿火，没有传送逻辑
    public FakePortalFireBlock(Properties properties, float damage) {
        super(properties, damage);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        serverLevel.removeBlock(blockPos, false);
    }
}

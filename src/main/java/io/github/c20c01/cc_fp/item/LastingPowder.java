package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.config.CCConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LastingPowder extends Item implements IDestroyByFireToUse {
    public LastingPowder(Properties properties) {
        super(properties);
    }

    @Override
    public void destroyByFire(ItemEntity itemEntity) {
        Level level = itemEntity.level;
        BlockPos blockPos = new BlockPos(itemEntity.position());

        if (BasePortalFireBlock.canChangeToLastingFire(blockPos, level)) {
            IDestroyByFireToUse.changeFireLasting(blockPos, level);
            return;
        }

        BlockState blockState = level.getBlockState(blockPos);
        if (CCConfig.lastingPowderCanSummonLava.get() && blockState.is(Blocks.LAVA) && blockState.getValue(BlockStateProperties.LEVEL) != 0) {
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LEVEL, 0), Block.UPDATE_ALL);
        }
    }
}

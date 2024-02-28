package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import io.github.c20c01.cc_fp.config.CCConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class LastingPowder extends Item implements IDestroyByFireToUse {
    public LastingPowder(Properties properties) {
        super(properties);
    }

    @Override
    public void destroyByFire(ItemEntity itemEntity) {
        Level level = itemEntity.level();
        BlockPos blockPos = IDestroyByFireToUse.getBlockPos(itemEntity);
        if (BasePortalFireBlock.canChangeToLastingFire(blockPos, level)) {
            IDestroyByFireToUse.changeFireLasting(blockPos, level);
            return;
        }

        BlockState blockState = level.getBlockState(blockPos);
        if (CCConfig.lastingPowderCanSummonLava.get() && blockState.is(Blocks.LAVA) && blockState.getValue(BlockStateProperties.LEVEL) != 0) {
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LEVEL, 0), Block.UPDATE_ALL);
            return;
        }

        if (blockState.getBlock() instanceof BaseFireBlock) {
            for (ItemEntity itemEntityInFire : level.getEntitiesOfClass(ItemEntity.class, new AABB(blockPos))) {
                ItemStack itemStack = itemEntityInFire.getItem();
                if (itemStack.is(CCMain.PORTAL_WAND_ITEM.get())) {
                    PortalWand.addPower(itemStack, 4 * itemEntity.getItem().getCount());
                    float pitch = NoteBlock.getPitchFromNote(PortalWand.getPower(itemStack) / 4);
                    level.playSound(null, blockPos, SoundEvents.NOTE_BLOCK_PLING.get(), SoundSource.BLOCKS, 3.0F, pitch);
                    break;
                }
            }
        }
    }
}

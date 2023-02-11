package io.github.c20c01.item;

import io.github.c20c01.block.portalFire.BasePortalFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class LastingPowder extends Item implements DestroyByFireToUse {
    public LastingPowder(Properties properties) {
        super(properties);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.isFire()) {
            Level level = itemEntity.level;
            BlockPos blockPos = new BlockPos(itemEntity.position());

            if (BasePortalFireBlock.canChangeToLastingFire(blockPos, level)) {
                changeFireLasting(blockPos, level);
            }
        }
    }

    private void changeFireLasting(BlockPos blockPos, Level level) {
        level.playSound(null, blockPos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 5.0F, 0.9F + level.random.nextFloat() * 0.2F);
        BasePortalFireBlock.changeAllToLastingFire(blockPos, level);
    }
}

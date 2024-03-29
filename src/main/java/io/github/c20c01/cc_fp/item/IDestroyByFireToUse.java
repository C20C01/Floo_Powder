package io.github.c20c01.cc_fp.item;

import io.github.c20c01.cc_fp.block.portalFire.BasePortalFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;

public interface IDestroyByFireToUse extends IForgeItem {

    void destroyByFire(ItemEntity itemEntity);

    @Override
    default void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        IForgeItem.super.onDestroyed(itemEntity, damageSource);
        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            destroyByFire(itemEntity);
        }
    }

    static BlockPos getBlockPos(ItemEntity itemEntity) {
        Position position = itemEntity.position();
        return BlockPos.containing(position.x(), position.y(), position.z());
    }

    static void changeFireBlock(BlockPos blockPos, Level level, String targetName) {
        BasePortalFireBlock.changeAllToPortalFire(blockPos, level, targetName);
        level.playSound(null, blockPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
    }

    static void changeFireLasting(BlockPos blockPos, Level level) {
        BasePortalFireBlock.changeAllToLastingFire(blockPos, level);
        level.playSound(null, blockPos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 5.0F, 0.9F + level.random.nextFloat() * 0.2F);
    }
}
package io.github.c20c01.item;

import io.github.c20c01.CCMain;
import io.github.c20c01.block.PortalFireBlock;
import io.github.c20c01.block.PortalFireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;

public class FlooPowder extends Item {
    private Level level;
    private String name;
    private HashSet<BlockPos> allFire;

    public FlooPowder(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        if (damageSource.isFire()) {
            level = itemEntity.level;
            name = itemEntity.getItem().getDisplayName().getString();
            try {
                changeFireBlock(itemEntity.position());
            } catch (Exception ignore) {
            }
        }
    }

    private void explode(Vec3 inPos) {
        if (level instanceof ServerLevel serverWorld) {
            serverWorld.explode(null, inPos.x(), inPos.y(), inPos.z(), 0.1F, false, Explosion.BlockInteraction.NONE);
        }
    }

    private void changeFireBlock(Vec3 inPos) {
        BlockPos pos = findFireBlock(inPos);
        if (pos != null && level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP)) {
            if (!(level.getBlockState(pos).getBlock() instanceof PortalFireBlock)) {
                getAllFireBlock(pos);
                for (BlockPos p : allFire) {
                    change(p);
                }
                allFire.clear();
                level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 5.0F, 0.9F + level.random.nextFloat() * 0.2F);
                return;
            }
        }
        explode(inPos);
    }

    private void getAllFireBlock(BlockPos inPos) {
        allFire = new HashSet<>();
        allFire.add(inPos);
        loop(inPos);
    }

    private void loop(BlockPos inPos) {
        BlockPos pos = inPos.north();
        if (isFireBlock(pos) && allFire.add(pos)) {
            loop(pos);
        }
        pos = inPos.west();
        if (isFireBlock(pos) && allFire.add(pos)) {
            loop(pos);
        }
        pos = inPos.south();
        if (isFireBlock(pos) && allFire.add(pos)) {
            loop(pos);
        }
        pos = inPos.east();
        if (isFireBlock(pos) && allFire.add(pos)) {
            loop(pos);
        }
    }

    private void change(BlockPos inPos) {
        if (level instanceof ServerLevel serverWorld) {
            serverWorld.setBlock(inPos, CCMain.PORTAL_FIRE_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
            if (serverWorld.getBlockEntity(inPos) instanceof PortalFireBlockEntity portalFireBlockEntity) {
                portalFireBlockEntity.name = this.name;
                portalFireBlockEntity.setLevel(serverWorld);
            }

        }
    }

    private BlockPos findFireBlock(Vec3 inPos) {
        BlockPos pos = new BlockPos(inPos);
        if (isFireBlock(pos)) {
            return pos;
        } else if (isFireBlock(pos.north())) {
            return pos.north();
        } else if (isFireBlock(pos.south())) {
            return pos.south();
        } else if (isFireBlock(pos.west())) {
            return pos.west();
        } else if (isFireBlock(pos.east())) {
            return pos.east();
        } else if (isFireBlock(pos.above())) {
            return pos.above();
        } else {
            explode(inPos);
        }
        return null;
    }

    private boolean isFireBlock(BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return (block instanceof BaseFireBlock && !(block instanceof PortalFireBlock));
    }
}

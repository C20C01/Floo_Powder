package io.github.c20c01.block.portalFire;

import io.github.c20c01.CCMain;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFireBlockEntity extends BlockEntity {
    public String name;
    public boolean lasting = false; // 传送后不熄灭

    public PortalFireBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_FIRE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Name", name);
        tag.putBoolean("Lasting", lasting);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        name = tag.getString("Name");
        lasting = tag.getBoolean("Lasting");
    }
}
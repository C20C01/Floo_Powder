package io.github.c20c01.block;

import io.github.c20c01.CCMain;
import io.github.c20c01.pos.PosMap;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalPointBlockEntity extends BlockEntity {
    public String name = "";

    public PortalPointBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_POINT_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void setLevel(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            PosMap.set(name, serverLevel, null);
            super.setLevel(serverLevel);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!Objects.equals(name, ""))
            tag.putString("Name", name);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        name = tag.getString("Name");
    }
}
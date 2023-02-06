package io.github.c20c01.block.portalFire;

import io.github.c20c01.CCMain;
import io.github.c20c01.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalFireBlockEntity extends BlockEntity {
    private String name = "";

    public PortalFireBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_FIRE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!Objects.equals(name, "")) tag.putString("Name", name);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Name")) name = tag.getString("Name");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void teleportEntity(Level level, BlockPos blockPos, Entity entity, Vec3 movement, boolean lasting) {
        switch (TpTool.tryTeleportEntity(entity, name, level.getBlockState(blockPos.below()).getBlock(), movement, false)) {
            case fail -> BasePortalFireBlock.removeAllPortalFire(blockPos, level, level.getBlockState(blockPos));
            case success -> {
                if (!lasting) BasePortalFireBlock.removeAllPortalFire(blockPos, level, level.getBlockState(blockPos));
            }

        }
    }
}
package io.github.c20c01.cc_fp.block.portalFire;

import io.github.c20c01.cc_fp.tp.TpTool;
import io.github.c20c01.cc_fp.CCMain;
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
    private String targetName = "";

    public PortalFireBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_FIRE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!Objects.equals(targetName, "")) tag.putString("TargetName", targetName);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("TargetName")) targetName = tag.getString("TargetName");
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void teleportEntity(Level level, BlockPos blockPos, Entity entity, Vec3 movement, boolean temporary, boolean lasting) {
        switch (TpTool.tryTeleportEntity(entity, targetName, level.getBlockState(blockPos.below()).getBlock(), movement, temporary, Boolean.FALSE)) {
            case fail -> BasePortalFireBlock.removeAllPortalFire(blockPos, level, level.getBlockState(blockPos));
            case success -> {
                if (!lasting) BasePortalFireBlock.removeAllPortalFire(blockPos, level, level.getBlockState(blockPos));
            }

        }
    }
}
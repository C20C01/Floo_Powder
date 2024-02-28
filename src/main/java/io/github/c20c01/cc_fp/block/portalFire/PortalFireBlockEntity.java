package io.github.c20c01.cc_fp.block.portalFire;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.tp.TpTool;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
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
        setChanged();
    }

    public String getTargetName() {
        return targetName;
    }

    public TpTool.Result teleportEntity(Entity entity, @Nullable Vec3 movement, boolean temporary) {
        return TpTool.tryTeleportEntity(entity, targetName, movement, temporary, Boolean.FALSE);
    }
}
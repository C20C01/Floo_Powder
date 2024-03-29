package io.github.c20c01.cc_fp.block.powderGiver;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderGiverBlockEntity extends BlockEntity {
    private String publicGroup = "";
    private PortalPointManager.CheckType checkType = PortalPointManager.CheckType.ALL;

    public PowderGiverBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.POWDER_GIVER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!Objects.equals(publicGroup, "")) tag.putString("PublicGroup", publicGroup);
        tag.putString("CheckType", checkType.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("PublicGroup")) publicGroup = tag.getString("PublicGroup");
        if (tag.contains("CheckType")) checkType = PortalPointManager.CheckType.valueOf(tag.getString("CheckType"));
    }

    public void setPublicGroup(String groupName) {
        if (!this.publicGroup.equals(groupName)) {
            this.publicGroup = groupName;
            setChanged();
        }
    }

    public String getPublicGroup() {
        return publicGroup;
    }

    public void changeToNextCheckType() {
        checkType = checkType.nextType();
        setChanged();
    }

    public PortalPointManager.CheckType getCheckType() {
        return checkType;
    }

    public String getDesc() {
        return checkType.name() + (checkType == PortalPointManager.CheckType.PUBLIC && !publicGroup.isEmpty() ? ": " + publicGroup : "");
    }
}
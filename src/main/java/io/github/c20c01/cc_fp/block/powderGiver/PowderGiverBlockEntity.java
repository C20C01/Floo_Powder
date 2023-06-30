package io.github.c20c01.cc_fp.block.powderGiver;

import io.github.c20c01.cc_fp.CCMain;
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

    public PowderGiverBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.POWDER_GIVER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!Objects.equals(publicGroup, "")) tag.putString("PublicGroup", publicGroup);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("PublicGroup")) publicGroup = tag.getString("PublicGroup");
    }

    public void setPublicGroup(String groupName) {
        this.publicGroup = groupName;
    }

    public String getPublicGroup() {
        return publicGroup;
    }
}
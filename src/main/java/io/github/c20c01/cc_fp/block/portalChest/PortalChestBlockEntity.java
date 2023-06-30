package io.github.c20c01.cc_fp.block.portalChest;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.item.flooReel.FlooReel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PortalChestBlockEntity extends ChestBlockEntity {
    private final PortalChestLidController chestLidController = new PortalChestLidController();

    public PortalChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_CHEST_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public float getOpenNess(float v) {
        return chestLidController.getOpenness(v);
    }

    public static void lidAnimateTick(PortalChestBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    @Override
    public boolean triggerEvent(int a, int b) {
        if (a == 1 && b == 0) {
            this.chestLidController.setOpen(Boolean.TRUE);
            return true;
        }
        return super.triggerEvent(a, b);
    }

    public boolean isNamedChest() {
        return !this.getDisplayName().equals(this.getDefaultName());
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return isNamedChest() ? FlooReel.getNamedReel(this.getDisplayName().getString()) : ItemStack.EMPTY;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return NonNullList.withSize(27, getItem(0));
    }
}

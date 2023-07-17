package io.github.c20c01.cc_fp.block.powderPot;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.item.FlooPowder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PowderPotBlockEntity extends BlockEntity {
    private static final int MAX_NUM = 32;
    private String name = "";
    private int num = 0;
    private boolean unlimited = false;

    public PowderPotBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.POWDER_POT_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (!name.isEmpty()) tag.putString("Name", name);
        if (num > 0) tag.putInt("Num", num);
        if (unlimited) tag.putBoolean("Unlimited", Boolean.TRUE);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Name")) name = tag.getString("Name");
        if (tag.contains("Num")) num = tag.getInt("Num");
        if (tag.contains("Unlimited")) unlimited = true;
        super.load(tag);
    }

    public boolean addPowder(ItemStack itemStack) {
        if (num < MAX_NUM) {
            int use = Math.min(MAX_NUM - num, itemStack.getCount());
            num += use;
            itemStack.shrink(use);
            return true;
        }
        return false;
    }

    public boolean getPowder(Player player) {
        if (isEmpty()) {
            return true;
        }

        int count = player.isShiftKeyDown() ? num : 1;
        ItemStack itemStack = FlooPowder.getNamedPowder(name);
        itemStack.setCount(count);
        player.getInventory().add(itemStack);
        player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        num -= unlimited ? 0 : count;
        return isEmpty();
    }

    public boolean setName(String name) {
        boolean flag = !this.name.equals(name);
        this.name = name;
        return flag;
    }

    public boolean setUnlimited() {
        unlimited = !unlimited;
        return unlimited;
    }

    public ItemStack getItems() {
        ItemStack itemStack = FlooPowder.getNamedPowder(name);
        itemStack.setCount(num);
        return itemStack;
    }

    public boolean isEmpty() {
        return num <= 0;
    }
}
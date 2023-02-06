package io.github.c20c01.block.portalPoint;

import io.github.c20c01.CCMain;
import io.github.c20c01.client.gui.menu.PortalPointMenu;
import io.github.c20c01.item.flooReel.ExpansionReel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalPointBlockEntity extends BaseContainerBlockEntity {
    private String pointName = "";
    private final NonNullList<ItemStack> items;

    public PortalPointBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_POINT_BLOCK_ENTITY.get(), blockPos, blockState);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);
    }

    @Override
    protected Component getDefaultName() {
        return new TextComponent("传送核心");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PortalPointMenu(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setItem(int slot, ItemStack newItemStack) {
        ItemStack oldItemstack = this.items.get(slot);

        this.items.set(slot, newItemStack);
        if (newItemStack.getCount() > this.getMaxStackSize()) {
            newItemStack.setCount(this.getMaxStackSize());
        }

        if (slot == 0) {
            boolean changed = !newItemStack.sameItem(oldItemstack) || !ItemStack.tagMatches(newItemStack, oldItemstack);
            if (changed) {
                PortalPointBlock.disable(Objects.requireNonNull(getLevel()), getBlockPos());
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr(Vec3.atCenterOf(this.worldPosition)) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return false;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (isPointNamed()) tag.putString("Point", getPointName());

        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Point")) setPointName(tag.getString("Point"));

        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String name) {
        this.pointName = name;
    }

    public boolean isPointNamed() {
        return !pointName.equals("");
    }

    public boolean checkNoneReel(Predicate<ExpansionReel> predicate) {
        for (ItemStack itemStack : items) {
            Item item = itemStack.getItem();
            if (item instanceof ExpansionReel reel) {
                if (predicate.test(reel)) return false;
            }
        }
        return true;
    }

    protected ItemStack getPowder() {
        return items.get(0);
    }
}
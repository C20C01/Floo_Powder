package io.github.c20c01.cc_fp.block.portalPoint;

import io.github.c20c01.cc_fp.CCMain;
import io.github.c20c01.cc_fp.client.gui.menu.PortalPointMenu;
import io.github.c20c01.cc_fp.item.flooReel.ExpansionReel;
import io.github.c20c01.cc_fp.savedData.PortalPoint;
import io.github.c20c01.cc_fp.savedData.PortalPointManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PortalPointBlockEntity extends BaseContainerBlockEntity {
    private String pointName = "";
    private boolean signal = false;
    private final NonNullList<ItemStack> items;

    public PortalPointBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CCMain.PORTAL_POINT_BLOCK_ENTITY.get(), blockPos, blockState);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (isPointNamed()) tag.putString("Point", getPointName());
        if (signal) tag.putBoolean("Signal", Boolean.TRUE);

        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Point")) setPointName(tag.getString("Point"));
        if (tag.contains("Signal")) signal = true;

        this.items.clear();
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(CCMain.PORTAL_POINT_BLOCK.get().getDescriptionId());
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
        this.items.set(slot, newItemStack);
        if (newItemStack.getCount() > this.getMaxStackSize()) {
            newItemStack.setCount(this.getMaxStackSize());
        }

        if (slot == 0) {
            PortalPointBlock.disable(Objects.requireNonNull(getLevel()), getBlockPos());
        }

        setChanged();
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

    // 避免被漏斗吸东西
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (!this.remove && facing != null && capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> EmptyHandler.INSTANCE).cast();
        }
        return super.getCapability(capability, facing);
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String name) {
        this.pointName = name;
        setChanged();
    }

    public boolean isPointNamed() {
        return !pointName.isEmpty();
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

    public boolean canUse(MinecraftServer server, Player player) {
        if (!isPointNamed()) {
            return true;
        }
        PortalPointManager portalPointManager = PortalPointManager.get(server);
        PortalPoint point = portalPointManager.get(getPointName(), Boolean.FALSE);
        if (point == null) {
            return true;
        }
        return point.ownerUid().equals(player.getUUID());
    }

    public boolean hasSignal() {
        return signal;
    }

    public void setSignal(boolean signal) {
        this.signal = signal;
    }

    protected ItemStack getPowder() {
        return items.get(0);
    }
}
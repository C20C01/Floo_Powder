package io.github.c20c01.client.gui.menu;

import io.github.c20c01.CCMain;
import io.github.c20c01.item.destroyByFireToUse.FlooPowder;
import io.github.c20c01.item.flooReel.ExpansionReel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault

public class PortalPointMenu extends AbstractContainerMenu {
    private final Container container;

    public PortalPointMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(6));
    }

    public PortalPointMenu(int containerId, Inventory inventory, Container container) {
        super(CCMain.PORTAL_POINT_MENU.get(), containerId);
        this.container = container;

        this.addSlot(new PowderSlot(container, 0, 80, 22));

        for (int i = 0; i < 5; i++) {
            addSlot(new ReelSlot(container, i + 1, 44 + i * 18, 44));
        }

        // Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (i < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemStack1, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private static class PowderSlot extends Slot {
        public PowderSlot(Container container, int i, int x, int y) {
            super(container, i, x, y);
        }

        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() instanceof FlooPowder;
        }

        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class ReelSlot extends Slot {
        public ReelSlot(Container container, int i, int x, int y) {
            super(container, i, x, y);
        }

        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() instanceof ExpansionReel;
        }

        public int getMaxStackSize() {
            return 1;
        }
    }
}

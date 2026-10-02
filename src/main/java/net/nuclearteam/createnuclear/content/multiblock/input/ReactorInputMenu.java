package net.nuclearteam.createnuclear.content.multiblock.input;

import com.zurrtum.create.foundation.gui.menu.MenuBase;
import com.zurrtum.create.foundation.gui.menu.MenuSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.CNMenus;


public class ReactorInputMenu extends MenuBase<ReactorInputEntity> {


    public ReactorInputMenu(int id, Inventory inv, ReactorInputEntity contentHolder) {
        super(CNMenus.SLOT_ITEM_STORAGE, id, inv, contentHolder);
    }

    public static ReactorInputMenu create(int id, Inventory inv, ReactorInputEntity contentHolder) {
        return new ReactorInputMenu(id, inv, contentHolder);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = clickedSlot.getItem();
        if (index < 2) moveItemStackTo(stack, 2, slots.size(), false);
        else moveItemStackTo(stack, 0, 2, false);
        return ItemStack.EMPTY;
    }



    // the client side copy of the block entity is resolved by ReactorInputScreen.create

    @Override
    protected void initAndReadInventory(ReactorInputEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
        // player Slots
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(player.getInventory(), hotbarSlot, -31 + hotbarSlot * 18, 155));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(player.getInventory(), col + row * 9 + 9, -31 + col * 18, 97 + row * 18));
            }
        }

        Slot slot1 = new MenuSlot(contentHolder.inventory, 0, 24, 29);
        Slot slot2 = new MenuSlot(contentHolder.inventory, 1, 57, 29);

        addSlot(slot1);
        addSlot(slot2);


    }

    @Override
    protected void saveData(ReactorInputEntity contentHolder) {
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput clickType, Player player) {
        if (clickType == ContainerInput.THROW) {
            int[] targetSlotIds = {9, 18, 27, 0, 1, 28, 19, 10, 16, 17, 26, 25, 34, 35, 8, 7};
            for (int id : targetSlotIds) {
                if (slotId == id) {
                    clickType = ContainerInput.PICKUP;
                    super.clicked(slotId, button, clickType, player);
                }
            }
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }
}
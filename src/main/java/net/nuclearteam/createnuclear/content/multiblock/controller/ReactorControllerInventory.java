package net.nuclearteam.createnuclear.content.multiblock.controller;

import com.zurrtum.create.infrastructure.items.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.CNItems;

/** Single slot holding the configured blueprint (was a Create SmartInventory with a stack size of 1). */
public class ReactorControllerInventory extends ItemStackHandler {
    private final ReactorControllerBlockEntity be;

    public ReactorControllerInventory(ReactorControllerBlockEntity be) {
        super(1);
        this.be = be;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        be.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack resource) {
        return slot == 0 && CNItems.REACTOR_BLUEPRINT.isIn(resource);
    }
}

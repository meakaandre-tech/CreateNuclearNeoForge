package net.nuclearteam.createnuclear.content.multiblock.input;

import com.zurrtum.create.infrastructure.items.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.CNItems;

public class ReactorInputInventory extends ItemStackHandler {
    private final ReactorInputEntity be;

    public ReactorInputInventory(ReactorInputEntity be) {
        super(2);
        this.be = be;
    }

    @Override
    public void setChanged() {
        be.setChanged();
    }

    // was isItemValid
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> CNItems.URANIUM_ROD.isIn(stack);
            case 1 -> CNItems.GRAPHITE_ROD.isIn(stack);
            default -> false;
        };
    }
}

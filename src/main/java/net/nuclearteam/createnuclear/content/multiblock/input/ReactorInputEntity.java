package net.nuclearteam.createnuclear.content.multiblock.input;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.gui.menu.MenuBase;
import com.zurrtum.create.foundation.gui.menu.MenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class ReactorInputEntity extends SmartBlockEntity implements MenuProvider {
    protected BlockPos block;

    public ReactorInputInventory inventory;

    public ReactorInputEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new ReactorInputInventory(this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) { }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        if (!clientPacket) {
            inventory.readSlots(tag.childOrEmpty("Inventory"));
        }
        super.read(tag, clientPacket);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        if (!clientPacket) {
            inventory.writeSlots(tag.child("Inventory"));
        }
        super.write(tag, clientPacket);
    }

    @Override
    public void destroy() {
        super.destroy();
        Containers.dropContents(level, worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.createnuclear.reactor_input.title");
    }

    @Override
    public MenuBase<?> createMenu(int i, Inventory inventory, Player player, RegistryFriendlyByteBuf extraData) {
        sendToMenu(extraData);
        return ReactorInputMenu.create(i, inventory, this);
    }

    @Override
    public void tick() {
        super.tick();
    }
}

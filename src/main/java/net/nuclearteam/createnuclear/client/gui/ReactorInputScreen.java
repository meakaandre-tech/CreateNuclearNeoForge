package net.nuclearteam.createnuclear.client.gui;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputEntity;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputMenu;
import org.jspecify.annotations.Nullable;

import static com.zurrtum.create.client.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class ReactorInputScreen extends AbstractSimiContainerScreen<ReactorInputMenu> {

    protected static final CNGuiTextures background = CNGuiTextures.REACTOR_SLOT_INVENTOR;

    public ReactorInputScreen(ReactorInputMenu container, Inventory inv, Component title) {
        // was setWindowSize in init
        super(container, inv, title, background.width, background.height + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
    }

    // was ReactorInputMenu.createOnClient
    @Nullable
    public static ReactorInputScreen create(Minecraft mc, MenuType<ReactorInputEntity> type, int syncId, Inventory inventory,
                                            Component title, RegistryFriendlyByteBuf extraData) {
        ReactorInputEntity entity = getBlockEntity(mc, extraData);
        if (entity == null) {
            return null;
        }
        try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(entity.problemPath(), CreateNuclear.LOGGER)) {
            ValueInput view = TagValueInput.create(logging, extraData.registryAccess(), extraData.readNbt());
            entity.readClient(view);
            return type.create(ReactorInputScreen::new, syncId, inventory, title, entity);
        }
    }

    @Override
    protected void init() {
        setWindowOffset(0,0);
        super.init();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.height + 4;
        renderPlayerInventory(guiGraphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(guiGraphics, x, y);

    }
}

package net.nuclearteam.createnuclear.client.gui;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;
import com.zurrtum.create.foundation.gui.menu.MenuType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.CNDataComponents;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintItemPacket;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintMenu;
import org.jspecify.annotations.Nullable;

public class ReactorBluePrintItemScreen extends AbstractSimiContainerScreen<ReactorBluePrintMenu> {
    protected static final CNGuiTextures BG = CNGuiTextures.CONFIGURED_PATTERN_GUI;

    public ReactorBluePrintItemScreen(ReactorBluePrintMenu menu, Inventory inv, Component title) {
        // was setWindowSize in init
        super(menu, inv, title, BG.width, BG.height + AllGuiTextures.PLAYER_INVENTORY.getHeight());
    }

    @Nullable
    public static ReactorBluePrintItemScreen create(Minecraft mc, MenuType<ItemStack> type, int syncId, Inventory inventory,
                                                    Component title, RegistryFriendlyByteBuf extraData) {
        return type.create(ReactorBluePrintItemScreen::new, syncId, inventory, title, getStack(extraData));
    }

    @Override
    protected void init() {
        setWindowOffset(0, 0);
        super.init();
        clearWidgets();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        int x = leftPos;
        int y = topPos+38;

        BG.render(guiGraphics, x+23, y-19);
        renderPlayerInventory(guiGraphics, x+23, y+175);

        guiGraphics.text(font, title, x+26, y-12, 0xFF592424, false); //ici pour le titre

    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (!ItemStack.matches(minecraft.player.getMainHandItem(), menu.contentHolder)) {
            minecraft.player.closeContainer();
        }

        float coef = 0.1F;

        CompoundTag tag = menu.contentHolder.getOrDefault(CNDataComponents.PATTERN, new CompoundTag());

        sendValueUpdate(tag, coef,
                tag.getIntOr("graphiteTime", 0),
                tag.getIntOr("uraniumTime", 0),
                tag.getIntOr("countUraniumRod", 0),
                tag.getIntOr("countGraphiteRod", 0)+3
        );


    }


    private static void sendValueUpdate(CompoundTag tag, float heat, int graphiteTime, int uraniumTime, int countGraphiteRod, int countUraniumRod) {
        ClientPlayNetworking.send(new ReactorBluePrintItemPacket(tag, heat, graphiteTime, uraniumTime, countGraphiteRod, countGraphiteRod));
    }
}

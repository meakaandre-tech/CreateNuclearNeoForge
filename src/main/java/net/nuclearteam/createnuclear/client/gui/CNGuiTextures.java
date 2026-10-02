package net.nuclearteam.createnuclear.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.nuclearteam.createnuclear.CreateNuclear;

public enum CNGuiTextures {
    //    REACTOR_CONTROLLER("toolbox", 188, 171),
    REACTOR_CONTROLLER("reactor-controller", 222, 207),
    REACTOR_CONTROLLER_PROGRESS("reactor-controller-components", 24, 13, 20, 162),
    REACTOR_SLOT_INVENTOR("storage-slot", 97, 75),
    CONFIGURED_PATTERN_GUI("configured-pattern-gui", 222, 194),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final Identifier location;
    public int width, height;
    public int startX, startY;

    CNGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    CNGuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateNuclear.MOD_ID, location, startX, startY, width, height);
    }

    CNGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = Identifier.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    public void render(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, x, y, startX, startY, width, height, 256, 256);
    }
}

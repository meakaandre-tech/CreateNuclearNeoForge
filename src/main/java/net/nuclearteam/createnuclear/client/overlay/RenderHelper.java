package net.nuclearteam.createnuclear.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/**
 * Helper class for rendering full-screen overlays.
 */
public class RenderHelper {
    /**
     * Renders a full-screen texture with given transparency.
     *
     * @param graphics      GUI graphics context
     * @param texture       the texture to render
     * @param alpha         transparency [0,1]
     * @param coverage      scale factor (unused by the original as well: the scaling was commented out)
     * @param onlyFirstPerson  if true, renders only in first-person camera mode
     */
    public static void renderOverlay(GuiGraphicsExtractor graphics, Identifier texture,
                                     float alpha, float coverage, boolean onlyFirstPerson) {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

        // If rendering is restricted to first-person and we're not in it, skip
        if (onlyFirstPerson && !isFirstPerson) return;

        renderTextureOverlay(graphics, texture, alpha);
    }

    /**
     * Convenience overload: always renders in any camera mode, no scaling.
     */
    public static void renderOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha) {
        renderOverlay(graphics, texture, alpha, 1f, false);
    }

    /**
     * Convenience overload: only in first-person, no scaling.
     */
    public static void renderFirstPersonOverlay(GuiGraphicsExtractor graphics,
                                                Identifier texture, float alpha, float coverage) {
        renderOverlay(graphics, texture, alpha, coverage, true);
    }

    /**
     * Renders a full-screen texture with given transparency.
     */
    public static void renderTextureOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        // the whole texture stretched over the screen, tinted with the alpha
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0f, 0f, width, height, width, height, ARGB.white(alpha));
    }
}

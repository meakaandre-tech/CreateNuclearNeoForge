package net.nuclearteam.createnuclear.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import net.nuclearteam.createnuclear.CNEffects;
import net.nuclearteam.createnuclear.CreateNuclear;

/**
 * Handles the client-side rendering of a radiation vision overlay
 * when radiation affects the player.
 */
public class IrradiatedOverlayRendererVision {

    /**
     * The current transparency level of the irradiated vision overlay.
     */
    private static float irradiatedVisionAlpha = 0.0f;

    /**
     * The texture resource location for the irradiated vision overlay.
     */
    public static final Identifier IRRADIATED_VISION =
            CreateNuclear.asResource("textures/misc/irradiated_vision/irradiated_vision.png");

    /**
     * Renders the overlay if radiation affects the player.
     * This method increases or decreases the alpha level based on the presence of the radiation effect.
     * It is invoked each frame by the HUD.
     */
    public static void renderOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();

        // a hidden HUD (F1) is not extracted at all
        if (mc.gameMode == null || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        LocalPlayer localPlayer = mc.player;
        if (localPlayer == null)
            return;

        // Adjust the overlay alpha depending on whether the radiation effect is active
        if (localPlayer.hasEffect(CNEffects.RADIATION)) {
            irradiatedVisionAlpha = Math.min(1.0f, irradiatedVisionAlpha + 0.01f);
        } else {
            irradiatedVisionAlpha = Math.max(0.0f, irradiatedVisionAlpha - 0.01f);
        }

        // Render the overlay if the alpha is greater than 0
        if (irradiatedVisionAlpha > 0.0f) {
            renderTextureOverlay(guiGraphics, IRRADIATED_VISION, irradiatedVisionAlpha, true);
        }
    }

    /**
     * Renders a full-screen texture overlay with the specified alpha and rendering conditions.
     */
    public static void renderTextureOverlay(GuiGraphicsExtractor guiGraphics, Identifier texture, float alpha, boolean onlyFirstPerson) {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType().isFirstPerson();

        // Skip rendering if restricted to first-person and not in first-person view
        if (onlyFirstPerson && !isFirstPerson)
            return;

        RenderHelper.renderTextureOverlay(guiGraphics, texture, alpha);
    }
}

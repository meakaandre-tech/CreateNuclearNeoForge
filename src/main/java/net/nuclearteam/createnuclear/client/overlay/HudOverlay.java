package net.nuclearteam.createnuclear.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * Base interface for all HUD overlays.
 */
public interface HudOverlay {
    Identifier getAfterOverlay();

    Identifier getOverlayId();

    boolean isActive();

    int getPriority();

    void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker);

    default void register() {
        HudElementRegistry.attachElementAfter(
                getAfterOverlay(),
                getOverlayId(),
                this::render
        );
    }
}

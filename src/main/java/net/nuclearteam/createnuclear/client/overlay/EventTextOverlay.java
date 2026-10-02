package net.nuclearteam.createnuclear.client.overlay;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.nuclearteam.createnuclear.CreateNuclear;

/**
 * HUD overlay for displaying localized text when a specific event occurs.
 */
public class EventTextOverlay implements HudOverlay {
    private static int timer = 0;

    /**
     * Called via a network packet to activate the overlay for a specific duration.
     * @param displayDuration duration in ticks
     */
    public static void triggerEvent(int displayDuration) {
        timer = displayDuration;
    }

    @Override
    public Identifier getAfterOverlay() {
        return VanillaHudElements.MISC_OVERLAYS;
    }

    @Override
    public Identifier getOverlayId() {
        return Identifier.parse("event_text_overlay");
    }

    // disabled in the original as well
    @Override
    public boolean isActive() {
        return timer > 0 && false;
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!isActive()) return;
        if (timer-- <= 0) return;
        CreateNuclear.LOGGER.warn("hum EventTextOverlay: {}", timer);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        Component text = Component.translatable("overlay.event_message", timer).withStyle(ChatFormatting.RED);
        int widths = guiGraphics.guiWidth();
        int x = (widths - Minecraft.getInstance().font.width(text)) / 2;
        guiGraphics.text(Minecraft.getInstance().font, text, x, 10, 0xFFFFFFFF);
    }

    @Override
    public int getPriority() {
        return 300; // render on top of other overlays
    }
}

package net.nuclearteam.createnuclear.client.overlay;

import java.util.Comparator;
import java.util.List;

public class HudRenderer {
    private static final List<HudOverlay> overlays = List.of(
            new HelmetOverlay(),
            //new RadiationOverlay(),
            new EventTextOverlay()
    );

    public void onHudRender() {
        overlays.stream()
                .sorted(Comparator.comparingInt(HudOverlay::getPriority))
                .forEach(HudOverlay::register);
    }
}

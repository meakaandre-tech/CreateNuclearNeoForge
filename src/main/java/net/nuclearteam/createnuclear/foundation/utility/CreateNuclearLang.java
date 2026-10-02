package net.nuclearteam.createnuclear.foundation.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.nuclearteam.createnuclear.CreateNuclear;

import java.util.Locale;

/**
 * Side-independent part of the mod's Lang helper. The LangBuilder based helpers are client only
 * in Create Fly and live in client.CNLang.
 */
public class CreateNuclearLang {
    public static String asId(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public static MutableComponent translateDirect(String key, Object... args) {
        return Component.translatable(CreateNuclear.MOD_ID + "." + key, args);
    }
}

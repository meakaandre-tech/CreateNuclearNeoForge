package net.nuclearteam.createnuclear.client;

import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.catnip.lang.LangNumberFormat;
import net.nuclearteam.createnuclear.CreateNuclear;

/** The LangBuilder half of the mod's CreateNuclearLang (LangBuilder is a client class in Create Fly). */
public class CNLang {
    public static LangBuilder builder() {
        return new LangBuilder(CreateNuclear.MOD_ID);
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }
}

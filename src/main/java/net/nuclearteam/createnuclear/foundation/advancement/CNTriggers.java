package net.nuclearteam.createnuclear.foundation.advancement;

import com.zurrtum.create.foundation.advancement.CreateTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * The built-in triggers of the advancements that have no vanilla criterion (the advancement files themselves
 * are data now). As in the original they are registered in Create's namespace: create:&lt;id&gt;_builtin.
 */
public class CNTriggers {
    public static final CreateTrigger FULL_ANTI_RADIATION_ARMOR = addSimple("full_anti_radiation_armor");
    public static final CreateTrigger AUTOMATIC_URANIUM = addSimple("automatic_uranium");

    public static CreateTrigger addSimple(String id) {
        Identifier location = Identifier.fromNamespaceAndPath("create", id);
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, location.withSuffix("_builtin"), new CreateTrigger(location));
    }

    public static void register() {}
}

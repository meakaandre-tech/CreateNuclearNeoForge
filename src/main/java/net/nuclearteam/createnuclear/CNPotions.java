package net.nuclearteam.createnuclear;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class CNPotions {

    public static final Holder<Potion> POTION_1 = register("potion_of_radiation_1",
            new MobEffectInstance(CNEffects.RADIATION, 900));
    public static final Holder<Potion> POTION_AUGMENT_1 = register("potion_of_radiation_augment_1",
            new MobEffectInstance(CNEffects.RADIATION, 1800));
    public static final Holder<Potion> POTION_2 = register("potion_of_radiation_2",
            new MobEffectInstance(CNEffects.RADIATION, 410, 1));

    private static Holder<Potion> register(String name, MobEffectInstance effect) {
        // the potion name is what the translation key is built from (item.minecraft.potion.effect.<name>)
        return Registry.registerForHolder(BuiltInRegistries.POTION, CreateNuclear.asResource(name), new Potion(name, effect));
    }

    public static void register() {
        // 26.3: brewing is data (data/createnuclear/recipe/brewing/*.json), nothing to register in code
    }
}

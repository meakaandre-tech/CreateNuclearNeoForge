package net.nuclearteam.createnuclear;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

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
        FabricPotionBrewingBuilder.BUILD.register(CNPotions::registerPotionsRecipes);
    }

    public static void registerPotionsRecipes(PotionBrewing.Builder builder) {
        builder.addMix(Potions.AWKWARD, CNItems.ENRICHED_YELLOWCAKE.get(), POTION_1);
        builder.addMix(POTION_1, Items.REDSTONE, POTION_AUGMENT_1);
        builder.addMix(POTION_1, Items.GLOWSTONE_DUST, POTION_2);
    }
}

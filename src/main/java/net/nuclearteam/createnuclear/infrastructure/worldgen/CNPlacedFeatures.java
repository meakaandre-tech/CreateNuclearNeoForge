package net.nuclearteam.createnuclear.infrastructure.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.nuclearteam.createnuclear.CreateNuclear;

/**
 * The configured and placed features are data files (data/createnuclear/worldgen). This adds them to the
 * overworld biomes, which the NeoForge biome modifiers did.
 */
public class CNPlacedFeatures {
    public static final ResourceKey<PlacedFeature>
        URANIUM_ORE = key("uranium_ore"),
        LEAD_ORE = key("lead_ore"),
        STRIATED_ORES_OVERWORLD = key("striated_ores_overworld")
    ;

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, CreateNuclear.asResource(name));
    }

    public static void register() {
        // #minecraft:is_overworld as in the original biome modifier, plus every biome the overworld dimension actually
        // generates: world generation packs that replace the overworld (Still Life) leave many of their biomes out of the tag
        for (ResourceKey<PlacedFeature> feature : new ResourceKey[]{URANIUM_ORE, LEAD_ORE, STRIATED_ORES_OVERWORLD})
            BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_OVERWORLD).or(BiomeSelectors.foundInOverworld()),
                    GenerationStep.Decoration.UNDERGROUND_ORES, feature);
    }
}

package net.nuclearteam.createnuclear;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.nuclearteam.createnuclear.content.decoration.palettes.CNPaletteBlocks;
import net.nuclearteam.createnuclear.content.equipment.armor.CNArmorMaterials;
import net.nuclearteam.createnuclear.content.kinetics.fan.processing.CNFanProcessingTypes;
import net.nuclearteam.createnuclear.foundation.advancement.CNTriggers;
import net.nuclearteam.createnuclear.infrastructure.config.CNConfigs;
import net.nuclearteam.createnuclear.infrastructure.worldgen.CNPlacedFeatures;
import org.slf4j.Logger;

public class CreateNuclear implements ModInitializer {
    public static final String MOD_ID = "createnuclear";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("{} initializing!", MOD_ID);

        CNConfigs.register();

        CNTags.init();
        CNDataComponents.register();
        CNEffects.register();
        CNArmorMaterials.register();
        CNBlocks.register();
        CNPaletteBlocks.register();
        CNEntityType.register();
        CNItems.register();
        CNFluids.register();
        CNBlockEntityTypes.register();
        CNPackets.register();
        CNMenus.register();

        CNCreativeModeTabs.register();
        CNPotions.register();
        CNRecipeTypes.register();

        CNFanProcessingTypes.register();
        CNTriggers.register();
        CNPlacedFeatures.register();

        CNFluids.registerFluidInteractions();
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}

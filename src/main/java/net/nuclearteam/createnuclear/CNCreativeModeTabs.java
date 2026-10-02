package net.nuclearteam.createnuclear;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.nuclearteam.createnuclear.registry.entry.BlockEntry;
import net.nuclearteam.createnuclear.registry.entry.ItemEntry;

public class CNCreativeModeTabs {
    public static CreativeModeTab MAIN;

    public static void register() {
        MAIN = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CreateNuclear.asResource("main"),
            FabricCreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.createnuclear.main"))
                    .icon(CNItems.URANIUM_POWDER::asStack)
                    .displayItems((parameters, output) -> {
                        // Registrate's generator: the blocks in registration order, then the items
                        for (BlockEntry<?> entry : CNBlocks.ALL) {
                            Item item = entry.asItem();
                            if (item != Items.AIR)
                                output.accept(item);
                        }
                        for (ItemEntry<?> entry : CNItems.ALL)
                            output.accept(entry.get());
                        output.accept(CNFluids.URANIUM.getBucket());
                    })
                    .build());
    }
}

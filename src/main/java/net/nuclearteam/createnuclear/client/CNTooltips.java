package net.nuclearteam.createnuclear.client;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.catnip.lang.Lang;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.client.foundation.item.TooltipHelper;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.CNItems;
import net.nuclearteam.createnuclear.CNTags.CNItemTags;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.multiblock.IHeat.HeatLevel;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlockEntity;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutputEntity;

import java.util.List;

/**
 * Goggle overlays of the mod's block entities. Create Fly keeps these in client-side behaviours
 * instead of on the block entity (IHaveGoggleInformation#addToGoggleTooltip in the original).
 */
public class CNTooltips {

    // ---- IHeat.HeatLevel's formatted texts

    public static LangBuilder getFormattedHeatText(int heat) {
        HeatLevel heatLevel = HeatLevel.of(heat);
        LangBuilder builder = Lang.builder(CreateNuclear.MOD_ID).text(TooltipHelper.makeProgressBar(5, heatLevel.ordinal()+1));

        builder.translate("tooltip.heatLevel." + Lang.asId(heatLevel.name()))
                .space()
                .text("(")
                .add(CNLang.number(Math.abs(heat)))
                .space()
                .translate("generic.unit.heat")
                .text(")")
                .space();

        if (heatLevel == HeatLevel.DANGER) builder.style(HeatLevel.DANGER.getTextColor()).style(ChatFormatting.STRIKETHROUGH);
        else builder.style(heatLevel.getTextColor());

        return builder;
    }

    public static LangBuilder getFormattedItemText(ItemStack itemRod, Boolean IsEmpty) {
        LangBuilder builder = Lang.builder(CreateNuclear.MOD_ID);

        String tooltip = "unknown";

        if (itemRod.is(CNItemTags.FUEL.tag)) {
            tooltip = "uranium";
        }

        if (itemRod.is(CNItemTags.COOLER.tag)) {
            tooltip = "graphene";
        }

        builder.translate("tooltip.item." + tooltip + ".rod")
                // when it's empty, we show the number minus one to display zero because we fake the item count as 1
                .add(CNLang.number(Math.abs((IsEmpty ? itemRod.getCount() - 1 : itemRod.getCount()))))
                .style(ChatFormatting.BLUE)
        ;

        return builder;
    }

    public static LangBuilder getName(String name) {
        LangBuilder builder = CNLang.builder();
        builder.translate("gui." + name + ".info_header.title");

        return builder;
    }

    // ---- block entities

    public static class ReactorController extends TooltipBehaviour<ReactorControllerBlockEntity> implements IHaveGoggleInformation {
        public ReactorController(ReactorControllerBlockEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
            ItemStack fuelItem = blockEntity.getFuelItem();
            ItemStack coolerItem = blockEntity.getCoolerItem();
            if (fuelItem == null) fuelItem = ItemStack.EMPTY;
            if (coolerItem == null) coolerItem = ItemStack.EMPTY;

            if(!blockEntity.configuredPattern.isEmpty()) {
                CreateLang.translate("gui.gauge.info_header").style(ChatFormatting.GRAY).forGoggles(tooltip);
                getName("reactor_controller").style(ChatFormatting.GRAY).forGoggles(tooltip);

                getFormattedHeatText(blockEntity.heat).forGoggles(tooltip);

                if (fuelItem.isEmpty()) {
                    // if rod empty we initialize it at 1 (and display it as 0) to avoid having air item displayed instead of the rod
                    getFormattedItemText(new ItemStack(CNItems.URANIUM_ROD.asItem(), 1), true).forGoggles(tooltip);
                } else {
                    getFormattedItemText(fuelItem, false).forGoggles(tooltip);
                }

                if (fuelItem.isEmpty()) {
                    // if rod empty we initialize it at 1 (and display it as 0) to avoid having air item displayed instead of the rod
                    getFormattedItemText(new ItemStack(CNItems.GRAPHITE_ROD.asItem(), 1), true).forGoggles(tooltip);
                } else {
                    getFormattedItemText(coolerItem, false).forGoggles(tooltip);
                }
            }

            return true;
        }
    }

    public static class ReactorOutput extends KineticTooltipBehaviour<ReactorOutputEntity> {
        public ReactorOutput(ReactorOutputEntity be) {
            super(be);
        }

        @Override
        public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

            float stressBase = blockEntity.calculateAddedStressCapacity();

            CreateLang.translate("gui.goggles.generator_stats")
                    .forGoggles(tooltip);
            CreateLang.translate("tooltip.capacityProvided")
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            float speed = blockEntity.getTheoreticalSpeed();
            speed = Math.abs(speed);

            float stressTotal = stressBase * speed;

            CreateLang.number(stressTotal)
                    .translate("generic.unit.stress")
                    .style(ChatFormatting.AQUA)
                    .space()
                    .add(CreateLang.translate("gui.goggles.at_current_speed")
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
            return true;
        }
    }
}

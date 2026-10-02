package net.nuclearteam.createnuclear.client;

import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.AllBlockEntityRenders;
import com.zurrtum.create.client.AllCasings;
import com.zurrtum.create.client.AllFluidConfigs;
import com.zurrtum.create.client.AllItemTooltips;
import com.zurrtum.create.client.AllMenuScreens;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.client.content.kinetics.base.OrientedRotatingVisual;
import com.zurrtum.create.client.foundation.block.connected.HorizontalCTBehaviour;
import com.zurrtum.create.client.foundation.block.connected.RotatedPillarCTBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import com.zurrtum.create.client.infrastructure.model.CTModel;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.nuclearteam.createnuclear.CNBlockEntityTypes;
import net.nuclearteam.createnuclear.CNBlocks;
import net.nuclearteam.createnuclear.CNEntityType;
import net.nuclearteam.createnuclear.CNFluids;
import net.nuclearteam.createnuclear.CNMenus;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.client.entity.CNModelLayers;
import net.nuclearteam.createnuclear.client.entity.IrradiatedCatModel;
import net.nuclearteam.createnuclear.client.entity.IrradiatedCatRenderer;
import net.nuclearteam.createnuclear.client.entity.IrradiatedChickenModel;
import net.nuclearteam.createnuclear.client.entity.IrradiatedChickenRenderer;
import net.nuclearteam.createnuclear.client.entity.IrradiatedWolfModel;
import net.nuclearteam.createnuclear.client.entity.IrradiatedWolfRenderer;
import net.nuclearteam.createnuclear.client.gui.ReactorBluePrintItemScreen;
import net.nuclearteam.createnuclear.client.gui.ReactorInputScreen;
import net.nuclearteam.createnuclear.client.overlay.EventTextOverlay;
import net.nuclearteam.createnuclear.client.overlay.HudRenderer;
import net.nuclearteam.createnuclear.client.overlay.IrradiatedOverlayRendererVision;
import net.nuclearteam.createnuclear.client.ponder.CreateNuclearPonderPlugin;
import net.nuclearteam.createnuclear.client.render.ReactorOutputRenderer;
import net.nuclearteam.createnuclear.content.decoration.palettes.CNPaletteBlocks;
import net.nuclearteam.createnuclear.content.multiblock.controller.EventTriggerPacket;

/**
 * Client entry point for the Fabric port: everything Registrate and the NeoForge client events
 * used to hook up (renderers, screens, connected textures, overlays, tooltips, ponder).
 */
public class CreateNuclearClient implements ClientModInitializer {
    private static final HudRenderer HUD_RENDERER = new HudRenderer();

    @Override
    public void onInitializeClient() {
        CNSpriteShifts.init();

        registerFluids();
        registerModels();
        registerRenderers();
        registerBehaviours();
        registerScreens();
        registerOverlays();
        registerPackets();

        PonderIndex.addPlugin(new CreateNuclearPonderPlugin());

        // item descriptions (hold Shift) and kinetic stats, as Registrate attached to every item of the mod
        for (Item item : BuiltInRegistries.ITEM)
            if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(CreateNuclear.MOD_ID))
                AllItemTooltips.register(item);
    }

    private static void registerFluids() {
        // world tint 0x38FF08, no tint in tanks and pipes; fog colour 0x38FF08 with a distance modifier of 1/32
        AllFluidConfigs.MODEL.put(CNFluids.URANIUM.still, new FluidModel.Unbaked(
                new Material(CreateNuclear.asResource("fluid/uranium_still")),
                new Material(CreateNuclear.asResource("fluid/uranium_flow")),
                null,
                BlockTintSources.constant(0xFF38FF08)));
        AllFluidConfigs.tint(CNFluids.URANIUM.still, (fluid, components) -> -1);
        AllFluidConfigs.fog(CNFluids.URANIUM.still, 0x38FF08, () -> 96.0f * (1f / 32f));
    }

    private static void registerModels() {
        // connected textures
        AllModels.register(CNBlocks.REACTOR_CASING.get(), CTModel.of(new EncasedCTBehaviour(CNSpriteShifts.REACTOR_CASING)));
        AllCasings.make(CNBlocks.REACTOR_CASING.get(), CNSpriteShifts.REACTOR_CASING);
        AllModels.register(CNBlocks.REINFORCED_GLASS.get(), CTModel.of(new EncasedCTBehaviour(CNSpriteShifts.REACTOR_GLASS)));
        AllCasings.make(CNBlocks.REINFORCED_GLASS.get(), CNSpriteShifts.REACTOR_GLASS);
        AllModels.register(CNPaletteBlocks.LAYERED_AUTUNITE.get(),
                CTModel.of(new HorizontalCTBehaviour(CNSpriteShifts.AUTUNITE_LAYERED, CNSpriteShifts.AUTUNITE_CAP)));
        AllModels.register(CNPaletteBlocks.AUTUNITE_PILLAR.get(),
                CTModel.of(new RotatedPillarCTBehaviour(CNSpriteShifts.AUTUNITE_PILLAR, CNSpriteShifts.AUTUNITE_CAP)));
    }

    private static void registerRenderers() {
        AllBlockEntityRenders.visual(CNBlockEntityTypes.REACTOR_OUTPUT.get(), ReactorOutputRenderer::new,
                OrientedRotatingVisual.of(AllPartialModels.SHAFT_HALF));

        ModelLayerRegistry.registerModelLayer(CNModelLayers.IRRADIATED_CAT, IrradiatedCatModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(CNModelLayers.IRRADIATED_CHICKEN, IrradiatedChickenModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(CNModelLayers.IRRADIATED_WOLF, IrradiatedWolfModel::createBodyLayer);

        EntityRendererRegistry.register(CNEntityType.IRRADIATED_CAT.get(), IrradiatedCatRenderer::new);
        EntityRendererRegistry.register(CNEntityType.IRRADIATED_CHICKEN.get(), IrradiatedChickenRenderer::new);
        EntityRendererRegistry.register(CNEntityType.IRRADIATED_WOLF.get(), IrradiatedWolfRenderer::new);
    }

    private static void registerBehaviours() {
        AllBlockEntityBehaviours.add(CNBlockEntityTypes.REACTOR_OUTPUT.get(), KineticAudioBehaviour::new,
                CNTooltips.ReactorOutput::new, CNScrollBehaviours::reactorOutput);
        AllBlockEntityBehaviours.add(CNBlockEntityTypes.REACTOR_CONTROLLER.get(), CNTooltips.ReactorController::new);
    }

    private static void registerScreens() {
        AllMenuScreens.register(CNMenus.REACTOR_BLUEPRINT_MENU, ReactorBluePrintItemScreen::create);
        AllMenuScreens.register(CNMenus.SLOT_ITEM_STORAGE, ReactorInputScreen::create);
    }

    // was CNClientEvent.onRegisterGui: each layer goes directly above the camera overlays
    private static void registerOverlays() {
        HUD_RENDERER.onHudRender();
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, CreateNuclear.asResource("irradiated_vision"),
                IrradiatedOverlayRendererVision::renderOverlay);
    }

    private static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(EventTriggerPacket.TYPE,
                (packet, ctx) -> ctx.client().execute(() -> EventTextOverlay.triggerEvent(packet.duration())));
    }
}

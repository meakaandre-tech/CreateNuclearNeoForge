package net.nuclearteam.createnuclear.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.contraptions.irradiated.chicken.IrradiatedChicken;

public class IrradiatedChickenRenderer extends MobRenderer<IrradiatedChicken, IrradiatedChickenRenderState, IrradiatedChickenModel> {
    private static final Identifier IRRADIATED_CHICKEN_LOCATION = CreateNuclear.asResource("textures/entity/irradiated_chicken.png");

    public IrradiatedChickenRenderer(EntityRendererProvider.Context context) {
        super(context, new IrradiatedChickenModel(context.bakeLayer(CNModelLayers.IRRADIATED_CHICKEN)), 0.3F);
    }

    @Override
    public Identifier getTextureLocation(IrradiatedChickenRenderState state) {
        return IRRADIATED_CHICKEN_LOCATION;
    }

    @Override
    public IrradiatedChickenRenderState createRenderState() {
        return new IrradiatedChickenRenderState();
    }

    @Override
    public void extractRenderState(IrradiatedChicken livingBase, IrradiatedChickenRenderState state, float partialTicks) {
        super.extractRenderState(livingBase, state, partialTicks);
        // was getBob
        float f = Mth.lerp(partialTicks, livingBase.oFlap, livingBase.flap);
        float g = Mth.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.flapSpeed);
        state.flap = (Mth.sin(f) + 1.0F) * g;
    }
}

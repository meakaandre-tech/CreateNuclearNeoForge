package net.nuclearteam.createnuclear.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.contraptions.irradiated.wolf.IrradiatedWolf;

public class IrradiatedWolfRenderer extends MobRenderer<IrradiatedWolf, IrradiatedWolfRenderState, IrradiatedWolfModel> {
    private static final Identifier WOLF_LOCATION = CreateNuclear.asResource("textures/entity/irradiated_wolf.png");
    private static final Identifier WOLF_TAME_LOCATION = CreateNuclear.asResource("textures/entity/irradiated_wolf.png");
    private static final Identifier WOLF_ANGRY_LOCATION = CreateNuclear.asResource("textures/entity/irradiated_wolf_angry.png");

    public IrradiatedWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new IrradiatedWolfModel(context.bakeLayer(CNModelLayers.IRRADIATED_WOLF)), 0.5F);
    }

    @Override
    public IrradiatedWolfRenderState createRenderState() {
        return new IrradiatedWolfRenderState();
    }

    @Override
    public void extractRenderState(IrradiatedWolf entity, IrradiatedWolfRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isAngry = entity.isAngry();
        state.isTame = entity.isTame();
        // was getBob
        state.tailAngle = entity.getTailAngle();
        // the original model passed the head pitch as the partial tick of the roll angles
        state.bodyRollAngle = entity.getBodyRollAngle(state.xRot, -0.16F);
        state.tailRollAngle = entity.getBodyRollAngle(state.xRot, -0.2F);
    }

    @Override
    public Identifier getTextureLocation(IrradiatedWolfRenderState state) {
        if (state.isTame) {
            return WOLF_TAME_LOCATION;
        } else {
            return state.isAngry ? WOLF_ANGRY_LOCATION : WOLF_LOCATION;
        }
    }
}

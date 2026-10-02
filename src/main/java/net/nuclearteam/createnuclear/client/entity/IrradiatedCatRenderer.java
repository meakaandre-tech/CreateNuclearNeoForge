package net.nuclearteam.createnuclear.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.contraptions.irradiated.cat.IrradiatedCat;

import java.util.List;

public class IrradiatedCatRenderer extends MobRenderer<IrradiatedCat, IrradiatedCatRenderState, IrradiatedCatModel> {
    private static final Identifier IRRADIATED_CAT_LOCATION = CreateNuclear.asResource("textures/entity/irradiated_cat.png");

    public IrradiatedCatRenderer(EntityRendererProvider.Context context) {
        super(context, new IrradiatedCatModel(context.bakeLayer(CNModelLayers.IRRADIATED_CAT)), 0.4f);
    }

    @Override
    public Identifier getTextureLocation(IrradiatedCatRenderState state) {
        return IRRADIATED_CAT_LOCATION;
    }

    @Override
    public IrradiatedCatRenderState createRenderState() {
        return new IrradiatedCatRenderState();
    }

    @Override
    public void extractRenderState(IrradiatedCat entity, IrradiatedCatRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.lieDownAmount = entity.getLieDownAmount(partialTick);
        state.nextToSleepingPlayer = false;
        if (state.lieDownAmount > 0.0F) {
            BlockPos blockPos = entity.blockPosition();
            List<Player> list = entity.level().getEntitiesOfClass(Player.class, (new AABB(blockPos)).inflate(2.0, 2.0, 2.0));

            for (Player player : list) {
                if (player.isSleeping()) {
                    state.nextToSleepingPlayer = true;
                    break;
                }
            }
        }
    }

    @Override
    protected void scale(IrradiatedCatRenderState state, PoseStack matrixStack) {
        super.scale(state, matrixStack);
        matrixStack.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    protected void setupRotations(IrradiatedCatRenderState state, PoseStack poseStack, float yBodyRot, float scale) {
        super.setupRotations(state, poseStack, yBodyRot, scale);

        float f = state.lieDownAmount;
        if (f > 0.0F) {
            poseStack.translate(0.4F * f, 0.15F * f, 0.1F * f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(f, 0.0F, 90.0F)));
            if (state.nextToSleepingPlayer) {
                poseStack.translate(0.15F * f, 0.0F, 0.0F);
            }
        }

    }
}

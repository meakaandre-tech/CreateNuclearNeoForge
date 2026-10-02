package net.nuclearteam.createnuclear.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.scrollValue.KineticScrollValueBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutput;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutputEntity;
import net.nuclearteam.createnuclear.foundation.utility.CreateNuclearLang;

/** Client halves of the scroll values (the value boxes players interact with). */
public class CNScrollBehaviours {

    public static KineticScrollValueBehaviour reactorOutput(ReactorOutputEntity be) {
        return new KineticScrollValueBehaviour(
                CreateNuclearLang.translateDirect("kinetics.reactor_output.rotation_speed"), be, new ReactorOutputValue());
    }

    // was ReactorOutputEntity.ReactorOutputValue
    public static class ReactorOutputValue extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 12.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(ReactorOutput.FACING);
            return super.getLocalOffset(state).add(Vec3.atLowerCornerOf(facing.getUnitVec3i())
                    .scale(-1 / 16f));
        }

        @Override
        public void rotate(BlockState state, PoseStack ms) {
            super.rotate(state, ms);
            Direction facing = state.getValue(ReactorOutput.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return;
            if (getSide() != Direction.UP)
                return;
            TransformStack.of(ms)
                    .rotateZDegrees(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(ReactorOutput.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN)
                return false;
            return direction.getAxis() != facing.getAxis();
        }

    }
}

package net.nuclearteam.createnuclear.content.multiblock.output;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerKineticScrollValueBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollValueBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nuclearteam.createnuclear.CNBlocks;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlock;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlockEntity;

import java.util.List;
import java.util.Objects;

import static net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutput.DIR;

public class ReactorOutputEntity extends GeneratingKineticBlockEntity {
    // hides KineticBlockEntity.speed, as in the original mod
    public int speed = 1;
    public float heat = 0;

    ReactorControllerBlock controller = null;
    ReactorControllerBlockEntity controllerEntity = null;

    // the value box (label, position) is the client half: client.CNScrollBehaviours
    protected ServerScrollValueBehaviour generatedSpeed;

    public ReactorOutputEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //KineticBlockEntity
    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        super.addBehaviours(behaviours);
        generatedSpeed = new ServerKineticScrollValueBehaviour(this);
        generatedSpeed.between(-1500000, 1500000);
        generatedSpeed.setValue(speed);
        generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
        behaviours.add(generatedSpeed);

    }

    @Override
    public void tick() {
        super.tick();

        BlockGetter level = getLevel();

        if (level.getBlockState(getBlockPos().above(3)).getBlock() == CNBlocks.REACTOR_CONTROLLER.get()) {
            controller = (ReactorControllerBlock) level.getBlockState(getBlockPos().above(3)).getBlock();
            controllerEntity = (ReactorControllerBlockEntity) level.getBlockEntity(getBlockPos().above(3));
            if (controllerEntity != null) {
                if (!controllerEntity.getAssembled() && getSpeed() != 0) {
                    setSpeed(0);
                }
            }
        } else setSpeed(0);
    }

    // the goggle overlay lives in client.CNTooltips.ReactorOutput

    @Override
    public void initialize() {
        super.initialize();

        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
        {
            FindController(getBlockPos(), Objects.requireNonNull(getLevel()));
        }
    }

    public void FindController(BlockPos pos, Level level){
        if (level.getBlockState(pos.above(3)).getBlock() == CNBlocks.REACTOR_CONTROLLER.get()){
            ReactorControllerBlock controller = (ReactorControllerBlock)level.getBlockState(pos.above(3)).getBlock();
            controller.Verify(controller.defaultBlockState(), pos.above(3), level, level.players(), false);
        }
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDir() {
        BlockState state = getBlockState();
        return state.getValue(DIR);
    }

    public void setDir(int dir, Level level, BlockPos pos) {
        BlockState state = getBlockState();
        level.setBlockAndUpdate(pos, state.setValue(DIR, dir));
    }

    @Override
    public float getGeneratedSpeed() {
        if (!CNBlocks.REACTOR_OUTPUT.has(getBlockState()))
            return 0;
        return speed; //convertToDirection(speed, getBlockState().getValue(ReactorOutput.FACING));
    }
}

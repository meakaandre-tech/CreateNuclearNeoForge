package net.nuclearteam.createnuclear.content.multiblock.input;

import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nuclearteam.createnuclear.CNBlockEntityTypes;
import net.nuclearteam.createnuclear.CNBlocks;
import net.nuclearteam.createnuclear.CNShapes;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlock;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlockEntity;
import net.nuclearteam.createnuclear.foundation.block.HorizontalDirectionalReactorBlock;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ReactorInput extends HorizontalDirectionalReactorBlock implements IWrenchable, IBE<ReactorInputEntity>, ItemInventoryProvider<ReactorInputEntity> {

    public ReactorInput(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    // the item handler capability of the block entity
    @Override
    public Container getInventory(LevelAccessor world, BlockPos pos, BlockState state, ReactorInputEntity blockEntity, @Nullable Direction context) {
        return blockEntity.inventory;
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (itemInHand.getItem() instanceof BlockItem) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }


        if (level.isClientSide()) {return InteractionResult.SUCCESS;}

        withBlockEntityDo(level, pos, be -> be.openHandledScreen((ServerPlayer) player));
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        List<? extends Player> players = level.players();
        FindController(pos, level, players, true);
    }

    @Override
    public void playerDestroy(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        List<? extends Player> players = level.players();
        FindController(pos, level, players, false);
    }

    // replaces onRemove; the rods are dropped by the block entity (ReactorInputEntity#destroy)
    @Override
    public void affectNeighborsAfterRemoval(BlockState pState, ServerLevel pLevel, BlockPos pPos, boolean pIsMoving) {
        super.affectNeighborsAfterRemoval(pState, pLevel, pPos, pIsMoving);

        List<? extends Player> players = pLevel.players();
        FindController(pPos, pLevel, players, false);
    }

    public ReactorControllerBlock FindController(BlockPos blockPos, Level level, List<? extends Player> players, boolean first){ // Function that checks the surrounding blocks in order
        BlockPos newBlock;                                                   // to find the controller and verify the pattern
        Vec3i pos = new Vec3i(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        for (int x = pos.getX()-5; x != pos.getX()+5; x+=1) {
            for (int z = pos.getZ()-5; z != pos.getZ()+5; z+=1) {
                newBlock = new BlockPos(x, pos.getY(), z);
                if (level.getBlockState(newBlock).is(CNBlocks.REACTOR_CONTROLLER.get())) { // verifying the pattern
                    ReactorControllerBlock controller = (ReactorControllerBlock) level.getBlockState(newBlock).getBlock();
                    controller.Verify(level.getBlockState(newBlock), newBlock, level, players, first);
                    ReactorControllerBlockEntity entity = controller.getBlockEntity(level, newBlock);
                    if (entity.created) {
                        return controller;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return CNShapes.REACTOR_INPUT.get(state.getValue(FACING));
    }

    @Override
    public Class<ReactorInputEntity> getBlockEntityClass() {
        return ReactorInputEntity.class;
    }

    @Override
    public BlockEntityType<? extends ReactorInputEntity> getBlockEntityType() {
        return CNBlockEntityTypes.REACTOR_INPUT.get();
    }
}

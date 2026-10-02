package net.nuclearteam.createnuclear;

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.nuclearteam.createnuclear.content.enriching.campfire.EnrichingCampfireBlockEntity;
import net.nuclearteam.createnuclear.content.multiblock.casing.ReactorCasingEntity;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlockEntity;
import net.nuclearteam.createnuclear.content.multiblock.core.ReactorCoreEntity;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInputEntity;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutputEntity;
import net.nuclearteam.createnuclear.registry.entry.BlockEntityEntry;
import net.nuclearteam.createnuclear.registry.entry.BlockEntry;

import java.util.HashSet;
import java.util.Set;

public class CNBlockEntityTypes {
    @FunctionalInterface
    private interface Factory<T extends BlockEntity> {
        T create(BlockEntityType<?> type, BlockPos pos, BlockState state);
    }

    public static final BlockEntityEntry<EnrichingCampfireBlockEntity> ENRICHING_CAMPFIRE_BLOCK =
            register("enriching_campfire_block", EnrichingCampfireBlockEntity::new, CNBlocks.ENRICHING_CAMPFIRE);

    public static final BlockEntityEntry<ReactorCasingEntity> REACTOR_CASING =
            register("reactor_casing", ReactorCasingEntity::new, CNBlocks.REACTOR_CASING);

    public static final BlockEntityEntry<ReactorCoreEntity> REACTOR_CORE =
            register("reactor_core", ReactorCoreEntity::new, CNBlocks.REACTOR_CORE);

    public static final BlockEntityEntry<ReactorInputEntity> REACTOR_INPUT =
            register("reactor_input", ReactorInputEntity::new, CNBlocks.REACTOR_INPUT);

    // renderer and visual are registered by the client entrypoint
    public static final BlockEntityEntry<ReactorOutputEntity> REACTOR_OUTPUT =
            register("reactor_output", ReactorOutputEntity::new, CNBlocks.REACTOR_OUTPUT);

    public static final BlockEntityEntry<ReactorControllerBlockEntity> REACTOR_CONTROLLER =
            register("reactor_controller", ReactorControllerBlockEntity::new, CNBlocks.REACTOR_CONTROLLER);

    private static <T extends BlockEntity> BlockEntityEntry<T> register(String name, Factory<T> factory, BlockEntry<?>... blocks) {
        BlockEntityEntry<T> entry = new BlockEntityEntry<>();
        Set<Block> set = new HashSet<>();
        for (BlockEntry<?> block : blocks)
            set.add(block.get());
        BlockEntityType<T> type = new BlockEntityType<>((pos, state) -> factory.create(entry.get(), pos, state), set);
        entry.set(Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateNuclear.asResource(name), type));
        return entry;
    }

    public static void register() {
        // the item handler capability of the reactor input, for other mods' pipes (Create goes through ItemInventoryProvider)
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> ContainerStorage.of(be.inventory, side), REACTOR_INPUT.get());
    }
}

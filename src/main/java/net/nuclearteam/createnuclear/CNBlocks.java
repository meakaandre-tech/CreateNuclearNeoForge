package net.nuclearteam.createnuclear;

import com.zurrtum.create.api.stress.BlockStressValues;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.nuclearteam.createnuclear.content.enriching.campfire.EnrichingCampfireBlock;
import net.nuclearteam.createnuclear.content.enriching.fire.EnrichingFireBlock;
import net.nuclearteam.createnuclear.content.multiblock.casing.ReactorCasing;
import net.nuclearteam.createnuclear.content.multiblock.controller.ReactorControllerBlock;
import net.nuclearteam.createnuclear.content.multiblock.core.ReactorCore;
import net.nuclearteam.createnuclear.content.multiblock.frame.ReactorFrame;
import net.nuclearteam.createnuclear.content.multiblock.frame.ReactorframeItem;
import net.nuclearteam.createnuclear.content.multiblock.input.ReactorInput;
import net.nuclearteam.createnuclear.content.multiblock.output.ReactorOutput;
import net.nuclearteam.createnuclear.content.multiblock.reactorCooler.ReactorCooler;
import net.nuclearteam.createnuclear.content.multiblock.reinforced.ReinforcedGlassBlock;
import net.nuclearteam.createnuclear.content.uraniumOre.UraniumOreBlock;
import net.nuclearteam.createnuclear.registry.entry.BlockEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CNBlocks {
    /** Every block of the mod in registration order, as Registrate kept them (creative tab order). */
    public static final List<BlockEntry<?>> ALL = new ArrayList<>();

    // Registrate's default initial properties and Create's SharedProperties.stone()
    private static Properties defaults() { return Properties.ofFullCopy(Blocks.STONE); }
    private static Properties stone() { return Properties.ofFullCopy(Blocks.ANDESITE); }

    public static final BlockEntry<ReactorCasing> REACTOR_CASING = block("reactor_casing",
            p -> new ReactorCasing(p, ReactorCasing.TypeBlock.CASING),
            () -> defaults().explosionResistance(3F).destroyTime(4F), BlockItem::new);

    public static final BlockEntry<ReactorCore> REACTOR_CORE = block("reactor_core", ReactorCore::new,
            () -> defaults().explosionResistance(6F).destroyTime(4F), BlockItem::new);

    public static final BlockEntry<ReactorFrame> REACTOR_FRAME = block("reactor_frame", ReactorFrame::new,
            () -> stone().explosionResistance(3F).destroyTime(2F), ReactorframeItem::new);

    public static final BlockEntry<ReactorCooler> REACTOR_COOLER = block("reactor_cooler", ReactorCooler::new,
            () -> defaults().explosionResistance(3F).destroyTime(4F), BlockItem::new);

    public static final BlockEntry<ReactorInput> REACTOR_INPUT = block("reactor_input", ReactorInput::new,
            () -> stone().explosionResistance(6F).destroyTime(2F), BlockItem::new);

    public static final BlockEntry<ReactorOutput> REACTOR_OUTPUT = block("reactor_output", ReactorOutput::new,
            () -> stone().explosionResistance(6F).destroyTime(4F).mapColor(MapColor.COLOR_PURPLE).forceSolidOn(), BlockItem::new);

    public static final BlockEntry<ReactorControllerBlock> REACTOR_CONTROLLER = block("reactor_controller", ReactorControllerBlock::new,
            () -> stone().explosionResistance(6F).destroyTime(4F), BlockItem::new);

    public static final BlockEntry<ReinforcedGlassBlock> REINFORCED_GLASS = block("reinforced_glass", ReinforcedGlassBlock::new,
            () -> Properties.ofFullCopy(Blocks.GLASS).explosionResistance(7.0F).destroyTime(2F), BlockItem::new);

    public static final BlockEntry<EnrichingFireBlock> ENRICHING_FIRE = block("enriching_fire",
            p -> new EnrichingFireBlock(p, 3.0f),
            () -> Properties.ofFullCopy(Blocks.FIRE).replaceable().noCollision().noOcclusion().lightLevel(a -> 15), null);

    public static final BlockEntry<EnrichingCampfireBlock> ENRICHING_CAMPFIRE = block("enriching_campfire",
            p -> new EnrichingCampfireBlock(p, true, 5),
            () -> defaults().mapColor(MapColor.PODZOL)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sound(SoundType.WOOD)
                    .lightLevel(EnrichingCampfireBlock::getLight)
                    .noOcclusion()
                    .ignitedByLava(), BlockItem::new);

    public static final BlockEntry<Block> DEEPSLATE_LEAD_ORE = block("deepslate_lead_ore", Block::new,
            () -> Properties.ofFullCopy(Blocks.DIAMOND_ORE), BlockItem::new);

    public static final BlockEntry<Block> LEAD_ORE = block("lead_ore", Block::new, CNBlocks::stone, BlockItem::new);

    public static final BlockEntry<Block> RAW_URANIUM_BLOCK = block("raw_uranium_block", Block::new, CNBlocks::stone, BlockItem::new);

    public static final BlockEntry<Block> RAW_LEAD_BLOCK = block("raw_lead_block", Block::new, CNBlocks::stone, BlockItem::new);

    public static final BlockEntry<Block> LEAD_BLOCK = block("lead_block", Block::new, CNBlocks::stone, BlockItem::new);

    public static final BlockEntry<Block> ENRICHED_SOUL_SOIL = block("enriched_soul_soil", Block::new,
            () -> Properties.ofFullCopy(Blocks.SOUL_SOIL), BlockItem::new);

    public static final BlockEntry<UraniumOreBlock> DEEPSLATE_URANIUM_ORE = block("deepslate_uranium_ore", UraniumOreBlock::new,
            () -> UraniumOreBlock.litBlockEmission(Properties.ofFullCopy(Blocks.DIAMOND_ORE)), BlockItem::new);

    public static final BlockEntry<UraniumOreBlock> URANIUM_ORE = block("uranium_ore", UraniumOreBlock::new,
            () -> UraniumOreBlock.litBlockEmission(stone()), BlockItem::new);

    public static final BlockEntry<Block> STEEL_BLOCK = block("steel_block", Block::new, CNBlocks::stone, BlockItem::new);

    public static <T extends Block> BlockEntry<T> block(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            Supplier<BlockBehaviour.Properties> properties,
            BiFunction<Block, Item.Properties, ? extends Item> itemFactory
    ) {
        Identifier id = CreateNuclear.asResource(name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        T block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.get().setId(blockKey)));
        if (itemFactory != null) {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
            Item item = itemFactory.apply(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            if (item instanceof BlockItem blockItem) {
                blockItem.registerBlocks(Item.BY_BLOCK, item);
            }
            Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        }
        BlockEntry<T> entry = new BlockEntry<>(block);
        ALL.add(entry);
        return entry;
    }

    public static void register() {
        CreateNuclear.LOGGER.info("Registering ModBlocks for " + CreateNuclear.MOD_ID);
        BlockStressValues.CAPACITIES.register(REACTOR_OUTPUT.get(), () -> 10240.0);
    }
}

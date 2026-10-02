package net.nuclearteam.createnuclear.content.decoration.palettes;

import com.zurrtum.create.content.decoration.palettes.ConnectedPillarBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.nuclearteam.createnuclear.CNBlocks;
import net.nuclearteam.createnuclear.registry.entry.BlockEntry;

import java.util.function.Function;

/**
 * The autunite stone type and its palette variants. Registrate generated these from
 * PaletteBlockPattern/PaletteBlockPartial; they are listed out here in the same order and with the same ids.
 */
public class CNPaletteBlocks {
    public static final BlockEntry<Block> AUTUNITE = CNBlocks.block("autunite", Block::new,
            () -> Properties.ofFullCopy(Blocks.ANDESITE).destroyTime(1.25f).mapColor(MapColor.COLOR_GREEN), BlockItem::new);

    public static final BlockEntry<Block> CUT_AUTUNITE = full("cut_autunite", Block::new);
    public static final BlockEntry<StairBlock> CUT_AUTUNITE_STAIRS = stairs("cut_autunite_stairs", CUT_AUTUNITE);
    public static final BlockEntry<SlabBlock> CUT_AUTUNITE_SLAB = slab("cut_autunite_slab", CUT_AUTUNITE);
    public static final BlockEntry<WallBlock> CUT_AUTUNITE_WALL = wall("cut_autunite_wall", CUT_AUTUNITE);

    public static final BlockEntry<Block> POLISHED_CUT_AUTUNITE = full("polished_cut_autunite", Block::new);
    public static final BlockEntry<StairBlock> POLISHED_CUT_AUTUNITE_STAIRS = stairs("polished_cut_autunite_stairs", POLISHED_CUT_AUTUNITE);
    public static final BlockEntry<SlabBlock> POLISHED_CUT_AUTUNITE_SLAB = slab("polished_cut_autunite_slab", POLISHED_CUT_AUTUNITE);
    public static final BlockEntry<WallBlock> POLISHED_CUT_AUTUNITE_WALL = wall("polished_cut_autunite_wall", POLISHED_CUT_AUTUNITE);

    public static final BlockEntry<Block> CUT_AUTUNITE_BRICKS = full("cut_autunite_bricks", Block::new);
    public static final BlockEntry<StairBlock> CUT_AUTUNITE_BRICK_STAIRS = stairs("cut_autunite_brick_stairs", CUT_AUTUNITE_BRICKS);
    public static final BlockEntry<SlabBlock> CUT_AUTUNITE_BRICK_SLAB = slab("cut_autunite_brick_slab", CUT_AUTUNITE_BRICKS);
    public static final BlockEntry<WallBlock> CUT_AUTUNITE_BRICK_WALL = wall("cut_autunite_brick_wall", CUT_AUTUNITE_BRICKS);

    public static final BlockEntry<Block> SMALL_AUTUNITE_BRICKS = full("small_autunite_bricks", Block::new);
    public static final BlockEntry<StairBlock> SMALL_AUTUNITE_BRICK_STAIRS = stairs("small_autunite_brick_stairs", SMALL_AUTUNITE_BRICKS);
    public static final BlockEntry<SlabBlock> SMALL_AUTUNITE_BRICK_SLAB = slab("small_autunite_brick_slab", SMALL_AUTUNITE_BRICKS);
    public static final BlockEntry<WallBlock> SMALL_AUTUNITE_BRICK_WALL = wall("small_autunite_brick_wall", SMALL_AUTUNITE_BRICKS);

    public static final BlockEntry<Block> LAYERED_AUTUNITE = full("layered_autunite", Block::new);
    public static final BlockEntry<ConnectedPillarBlock> AUTUNITE_PILLAR = full("autunite_pillar", ConnectedPillarBlock::new);

    private static <T extends Block> BlockEntry<T> full(String name, Function<Properties, T> factory) {
        return CNBlocks.block(name, factory, () -> Properties.ofFullCopy(AUTUNITE.get()), BlockItem::new);
    }

    private static BlockEntry<StairBlock> stairs(String name, BlockEntry<? extends Block> base) {
        return CNBlocks.block(name, p -> new StairBlock(base.get().defaultBlockState(), p),
                () -> Properties.ofFullCopy(base.get()), BlockItem::new);
    }

    private static BlockEntry<SlabBlock> slab(String name, BlockEntry<? extends Block> base) {
        return CNBlocks.block(name, SlabBlock::new, () -> Properties.ofFullCopy(base.get()), BlockItem::new);
    }

    private static BlockEntry<WallBlock> wall(String name, BlockEntry<? extends Block> base) {
        return CNBlocks.block(name, WallBlock::new, () -> Properties.ofFullCopy(base.get()).forceSolidOn(), BlockItem::new);
    }

    public static void register() {}
}

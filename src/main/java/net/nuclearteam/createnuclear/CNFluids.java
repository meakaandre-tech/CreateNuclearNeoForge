package net.nuclearteam.createnuclear;

import com.zurrtum.create.AllFluidItemInventory;
import com.zurrtum.create.infrastructure.fluids.BucketFluidInventory;
import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import com.zurrtum.create.infrastructure.fluids.FluidBlock;
import com.zurrtum.create.infrastructure.fluids.FluidInteractionRegistry;
import com.zurrtum.create.infrastructure.fluids.FluidInteractionRegistry.InteractionInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.sounds.SoundSource;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.nuclearteam.createnuclear.content.decoration.palettes.CNPaletteBlocks;
import net.nuclearteam.createnuclear.registry.entry.FluidEntry;

import java.util.Optional;
import java.util.function.Supplier;

public class CNFluids {
    /** Create Fly counts fluids in droplets (81000 per bucket); the mod's numbers are millibuckets. */
    public static final int MB = 81;

    // levelDecreasePerBlock 2, tickRate 15, slopeFindDistance 6, explosionResistance 100
    public static final FluidEntry URANIUM = register("uranium", new FluidEntry(2, 15, 6) {
        @Override
        protected FlowableFluid createStill() {
            return new UraniumStill(this);
        }

        @Override
        protected FlowableFluid createFlowing() {
            return new UraniumFlowing(this);
        }
    });

    private static FluidEntry register(String name, FluidEntry entry) {
        Identifier id = CreateNuclear.asResource(name);
        Registry.register(BuiltInRegistries.FLUID, id, entry.still);
        Registry.register(BuiltInRegistries.FLUID, CreateNuclear.asResource("flowing_" + name), entry.flowing);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        entry.block = Registry.register(BuiltInRegistries.BLOCK, blockKey,
                new FluidBlock(entry.still, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable().setId(blockKey)));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, CreateNuclear.asResource(name + "_bucket"));
        entry.bucket = Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BucketItem(entry.still, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).setId(itemKey)) {
                    // the fluid type's bucket sounds were the lava ones
                    @Override
                    protected void playEmptySound(@Nullable LivingEntity user, LevelAccessor level, BlockPos pos) {
                        level.playSound(user, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
                        level.gameEvent(user, GameEvent.FLUID_PLACE, pos);
                    }
                });
        DispenserBlock.registerBehavior(entry.bucket, DISPENSE_FLUID);
        AllFluidItemInventory.ALL.put(entry.bucket, new AllFluidItemInventory.Entry(BucketFluidInventory::new));
        return entry;
    }

    public static void register() {}

    /**
     * Replaces the LivingVisibilityEvent listener of the original (entities standing in liquid uranium become
     * irradiated): the fluid applies the effect to what is inside it.
     */
    private static void handleFluidEffect(Level level, Entity entity) {
        if (level.isClientSide() || !(entity instanceof LivingEntity living))
            return;
        if (living.isAlive() && !(living.isSpectator())) {
            if (living.tickCount % 20 == 0) return;
            living.addEffect(new MobEffectInstance(CNEffects.RADIATION, 100, 0));
        }
    }

    public static void registerFluidInteractions() {
        // Supplier for the common BlockState to return (Autunite)
        Supplier<BlockState> autuniteState = () -> CNPaletteBlocks.AUTUNITE.get().defaultBlockState();

        // lava and water touching uranium turn into autunite
        for (Fluid source : new Fluid[]{Fluids.LAVA, Fluids.WATER}) {
            FluidInteractionRegistry.addInteraction(source, new InteractionInformation(URANIUM.still, fs -> autuniteState.get()));
        }
    }

    private static class UraniumStill extends FluidEntry.Still {
        UraniumStill(FluidEntry entry) {
            super(entry);
        }

        @Override
        protected void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier) {
            handleFluidEffect(level, entity);
        }

        @Override
        public Optional<SoundEvent> getPickupSound() {
            return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
        }
    }

    private static class UraniumFlowing extends FluidEntry.Flowing {
        UraniumFlowing(FluidEntry entry) {
            super(entry);
        }

        @Override
        protected void entityInside(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier) {
            handleFluidEffect(level, entity);
        }

        @Override
        public Optional<SoundEvent> getPickupSound() {
            return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
        }
    }

    // from Create

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
    private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
            DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
            BlockPos pos = pSource.pos().relative(pSource.state().getValue(DispenserBlock.FACING));
            Level level = pSource.level();
            if (dispensibleContainerItem.emptyContents(null, level, pos, null)) {
                return new ItemStack(Items.BUCKET);
            }
            return DEFAULT.dispense(pSource, pStack);
        }
    };
}

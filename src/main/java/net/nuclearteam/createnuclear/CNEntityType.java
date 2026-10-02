package net.nuclearteam.createnuclear;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.nuclearteam.createnuclear.content.contraptions.irradiated.cat.IrradiatedCat;
import net.nuclearteam.createnuclear.content.contraptions.irradiated.chicken.IrradiatedChicken;
import net.nuclearteam.createnuclear.content.contraptions.irradiated.wolf.IrradiatedWolf;

import java.util.function.Supplier;

public class CNEntityType {

    /** Small stand-in for Registrate's EntityEntry. */
    public record EntityEntry<T extends Entity>(EntityType<T> type) implements Supplier<EntityType<T>> {
        @Override
        public EntityType<T> get() {
            return type;
        }

        public T create(Level level) {
            return type.create(level, EntitySpawnReason.BREEDING);
        }

        public boolean is(Entity entity) {
            return entity != null && entity.getType() == type;
        }
    }

    public static final EntityEntry<IrradiatedCat> IRRADIATED_CAT = register("irradiated_cat",
            EntityType.Builder.of(IrradiatedCat::new, MobCategory.CREATURE).sized(0.6f, 0.7f));

    public static final EntityEntry<IrradiatedChicken> IRRADIATED_CHICKEN = register("irradiated_chicken",
            EntityType.Builder.of(IrradiatedChicken::new, MobCategory.CREATURE).sized(0.6f, 0.7f));

    public static final EntityEntry<IrradiatedWolf> IRRADIATED_WOLF = register("irradiated_wolf",
            EntityType.Builder.of(IrradiatedWolf::new, MobCategory.CREATURE).sized(0.6f, 0.85f));

    private static <T extends Entity> EntityEntry<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, CreateNuclear.asResource(name));
        return new EntityEntry<>(Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key)));
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(IRRADIATED_CAT.get(), IrradiatedCat.createAttributes());
        FabricDefaultAttributeRegistry.register(IRRADIATED_CHICKEN.get(), IrradiatedChicken.createAttributes());
        FabricDefaultAttributeRegistry.register(IRRADIATED_WOLF.get(), IrradiatedWolf.createAttributes());
    }
}

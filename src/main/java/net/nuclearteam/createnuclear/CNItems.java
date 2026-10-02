package net.nuclearteam.createnuclear;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem;
import net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem.Boot;
import net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem.Chestplate;
import net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem.Helmet;
import net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem.Leggings;
import net.nuclearteam.createnuclear.content.equipment.cloth.ClothItem;
import net.nuclearteam.createnuclear.content.equipment.cloth.ClothItem.DyeItemList;
import net.nuclearteam.createnuclear.content.multiblock.bluePrintItem.ReactorBluePrintItem;
import net.nuclearteam.createnuclear.foundation.item.DyedItemsList;
import net.nuclearteam.createnuclear.registry.entry.ItemEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused"})
public class CNItems {
    /** Every non-block item of the mod in registration order, as Registrate kept them (creative tab order). */
    public static final List<ItemEntry<?>> ALL = new ArrayList<>();

    public static final ItemEntry<Item>
        // nutrition 20, saturation modifier 0.3, always edible, Radiation III for 30 s
        YELLOWCAKE = item("yellowcake", p -> new Item(p.food(
                new FoodProperties(20, 20 * 0.3F * 2.0F, true),
                Consumables.defaultFood()
                        .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(CNEffects.RADIATION, 600, 2), 1.0F))
                        .build()))),

        RAW_LEAD = item("raw_lead", Item::new),

        RAW_URANIUM = item("raw_uranium", Item::new),

        URANIUM_POWDER = item("uranium_powder", Item::new),

        STEEL_INGOT = item("steel_ingot", Item::new),

        COAL_DUST = item("coal_dust", Item::new),

        GRAPHITE_ROD = item("graphite_rod", Item::new),

        LEAD_INGOT = item("lead_ingot", Item::new),

        STEEL_NUGGET = item("steel_nugget", Item::new),

        URANIUM_ROD = item("uranium_rod", Item::new),

        LEAD_NUGGET = item("lead_nugget", Item::new),

        GRAPHENE = item("graphene", Item::new),

        ENRICHED_YELLOWCAKE = item("enriched_yellowcake", Item::new)
    ;

    public static final DyedItemsList<Helmet> ANTI_RADIATION_HELMETS = new DyedItemsList<>(color -> {
        String colorName = color.getSerializedName();
        return item(colorName + "_anti_radiation_helmet", p -> new Helmet(p, color));
    });

    public static final DyedItemsList<Chestplate> ANTI_RADIATION_CHESTPLATES = new DyedItemsList<>(color -> {
        String colorName = color.getSerializedName();
        return item(colorName + "_anti_radiation_chestplate", p -> new Chestplate(p, color));
    });

    public static final DyedItemsList<Leggings> ANTI_RADIATION_LEGGINGS = new DyedItemsList<>(color -> {
        String colorName = color.getSerializedName();
        return item(colorName + "_anti_radiation_leggings", p -> new Leggings(p, color));
    });

    public static final ItemEntry<? extends AntiRadiationArmorItem.Boot>
        ANTI_RADIATION_BOOTS = item("anti_radiation_boots", Boot::new);

    public static final DyeItemList<ClothItem> CLOTHS = new ClothItem.DyeItemList<>(color -> {
        String colorName = color.getSerializedName();
        return item(colorName + "_cloth", p -> new ClothItem(p, color));
    });

    public static final ItemEntry<SpawnEggItem> SPAWN_WOLF = registerSpawnEgg("wolf_irradiated_spawn_egg", CNEntityType.IRRADIATED_WOLF);
    public static final ItemEntry<SpawnEggItem> SPAWN_CAT = registerSpawnEgg("cat_irradiated_spawn_egg", CNEntityType.IRRADIATED_CAT);
    public static final ItemEntry<SpawnEggItem> SPAWN_CHICKEN = registerSpawnEgg("chicken_irradiated_spawn_egg", CNEntityType.IRRADIATED_CHICKEN);

    public static final ItemEntry<ReactorBluePrintItem> REACTOR_BLUEPRINT = item("reactor_blueprint_item",
            p -> new ReactorBluePrintItem(p.stacksTo(1)));

    // the egg colours (wolf 0x42452B/0x4C422B, cat 0x382C19/0x742728, chicken 0x6B9455/0x95393C) are tints in the item model definitions
    private static ItemEntry<SpawnEggItem> registerSpawnEgg(String name, Supplier<? extends EntityType<? extends Mob>> entity) {
        return item(name, p -> new SpawnEggItem(p.spawnEgg(entity.get())));
    }

    public static <T extends Item> ItemEntry<T> item(String name, Function<Item.Properties, T> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, CreateNuclear.asResource(name));
        T item = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
        ItemEntry<T> entry = new ItemEntry<>(item);
        ALL.add(entry);
        return entry;
    }

    public static void register() {}
}

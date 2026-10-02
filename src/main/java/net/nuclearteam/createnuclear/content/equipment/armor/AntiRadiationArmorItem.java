package net.nuclearteam.createnuclear.content.equipment.armor;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.nuclearteam.createnuclear.CNItems;
import net.nuclearteam.createnuclear.CNTags.CNItemTags;
import net.nuclearteam.createnuclear.registry.entry.ItemEntry;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("unused")
public class AntiRadiationArmorItem {

    public static final ArmorType HELMET = ArmorType.HELMET;
    public static final ArmorType CHESTPLATE = ArmorType.CHESTPLATE;
    public static final ArmorType LEGGINGS = ArmorType.LEGGINGS;
    public static final ArmorType BOOTS = ArmorType.BOOTS;
    public static final ArmorMaterial ARMOR_MATERIAL = CNArmorMaterials.ANTI_RADIATION_SUIT;


    public static class Helmet extends Item {
        protected final DyeColor color;

        public Helmet(Properties properties, DyeColor color) {
            super(properties.humanoidArmor(CNArmorMaterials.antiRadiationSuit(color), HELMET));
            this.color = color;
        }

        public static TagKey<Item> getHelmetTag(String key) {
            return key.equals("white")
                    ? CNItemTags.ANTI_RADIATION_ARMOR.tag
                    : CNItemTags.ANTI_RADIATION_HELMET_DYE.tag;
        }
    }


    public static class Chestplate extends Item {
        protected final DyeColor color;

        public Chestplate(Properties properties, DyeColor color) {
            super(properties.humanoidArmor(CNArmorMaterials.antiRadiationSuit(color), CHESTPLATE));
            this.color = color;

        }

        public static TagKey<Item> getChestplateTag(String key) {
            return key.equals("white")
                    ? CNItemTags.ANTI_RADIATION_ARMOR.tag
                    : CNItemTags.ANTI_RADIATION_CHESTPLATE_DYE.tag;
        }
    }

    public static class Leggings extends Item {
        protected final DyeColor color;

        public Leggings(Properties properties, DyeColor color) {
            super(properties.humanoidArmor(CNArmorMaterials.antiRadiationSuit(color), LEGGINGS));
            this.color = color;

        }


        public static TagKey<Item> getLeggingsTag(String key) {
            return key.equals("white")
                    ? CNItemTags.ANTI_RADIATION_ARMOR.tag
                    : CNItemTags.ANTI_RADIATION_LEGGINGS_DYE.tag;
        }
    }

    public static class Boot extends Item {
        public Boot(Properties properties) {
            super(properties.humanoidArmor(CNArmorMaterials.antiRadiationSuit(DyeColor.WHITE), BOOTS));
        }
    }


    public enum Armor {
        WHITE_ARMOR(DyeColor.WHITE),
        YELLOW_ARMOR(DyeColor.YELLOW),
        RED_ARMOR(DyeColor.RED),
        BLUE_ARMOR(DyeColor.BLUE),
        GREEN_ARMOR(DyeColor.GREEN),
        BLACK_ARMOR(DyeColor.BLACK),
        ORANGE_ARMOR(DyeColor.ORANGE),
        PURPLE_ARMOR(DyeColor.PURPLE),
        BROWN_ARMOR(DyeColor.BROWN),
        PINK_ARMOR(DyeColor.PINK),
        CYAN_ARMOR(DyeColor.CYAN),
        LIGHT_GRAY_ARMOR(DyeColor.LIGHT_GRAY),
        GRAY_ARMOR(DyeColor.GRAY),
        LIGHT_BLUE_ARMOR(DyeColor.LIGHT_BLUE),
        LIME_ARMOR(DyeColor.LIME),
        MAGENTA_ARMOR(DyeColor.MAGENTA);

        private static final Map<DyeColor, ItemEntry<Helmet>> helmetMap = new EnumMap<>(DyeColor.class);
        private static final Map<DyeColor, ItemEntry<Chestplate>> chestplateMap = new EnumMap<>(DyeColor.class);
        private static final Map<DyeColor, ItemEntry<Leggings>> leggingsMap = new EnumMap<>(DyeColor.class);

        static {
            for (DyeColor color : DyeColor.values()) {
                helmetMap.put(color, CNItems.ANTI_RADIATION_HELMETS.get(color));
                chestplateMap.put(color, CNItems.ANTI_RADIATION_CHESTPLATES.get(color));
                leggingsMap.put(color, CNItems.ANTI_RADIATION_LEGGINGS.get(color));
            }
        }

        private final DyeColor color;

        Armor(DyeColor dyeColor) {
            this.color = dyeColor;
        }

        public ItemEntry<Helmet> getHelmetItem() {
            return helmetMap.get(this.color);
        }

        public static ItemEntry<Helmet> getHelmetByColor(DyeColor color) {
            return helmetMap.get(color);
        }

        public ItemEntry<Chestplate> getChestplateItem() {
            return chestplateMap.get(this.color);
        }

        public static ItemEntry<Chestplate> getChestplateByColor(DyeColor color) {
            return chestplateMap.get(color);
        }

        public ItemEntry<Leggings> getLeggingsItem() {
            return leggingsMap.get(this.color);
        }

        public static ItemEntry<Leggings> getLeggingsByColor(DyeColor color) {
            return leggingsMap.get(color);
        }

        public static boolean isArmored(ItemStack item) {
            return helmetMap.values().stream().anyMatch(entry -> entry.is(item.getItem())) ||
                    chestplateMap.values().stream().anyMatch(entry -> entry.is(item.getItem())) ||
                    leggingsMap.values().stream().anyMatch(entry -> entry.is(item.getItem())) ||
                    CNItems.ANTI_RADIATION_BOOTS.is(item.getItem())
                    ;
        }

        public static boolean isArmored2(ItemStack item) {
            return CNItems.ANTI_RADIATION_HELMETS.contains(item.getItem())
                    || CNItems.ANTI_RADIATION_CHESTPLATES.contains(item.getItem())
                    || CNItems.ANTI_RADIATION_LEGGINGS.contains(item.getItem())
                    || CNItems.ANTI_RADIATION_BOOTS.is(item.getItem())
                    ;
        }
    }

}

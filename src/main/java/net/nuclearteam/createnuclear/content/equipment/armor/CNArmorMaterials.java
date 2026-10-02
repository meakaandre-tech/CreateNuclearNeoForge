package net.nuclearteam.createnuclear.content.equipment.armor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.nuclearteam.createnuclear.CNTags;
import net.nuclearteam.createnuclear.CreateNuclear;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * The anti radiation suit material. Armour is data-component based now, and the worn texture comes from the
 * material's equipment asset, so there is one material per suit colour (the original passed a texture per item).
 */
public class CNArmorMaterials {
    /** Items that repair the suit: the lead ingot, as the original material's repair ingredient. */
    public static final TagKey<Item> REPAIRS_ANTI_RADIATION_SUIT =
            CNTags.optionalTag(net.minecraft.core.registries.BuiltInRegistries.ITEM, CreateNuclear.asResource("repairs_anti_radiation_suit"));

    private static final Map<DyeColor, ArmorMaterial> ANTI_RADIATION_SUITS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values())
            ANTI_RADIATION_SUITS.put(color, register(
                    String.format(Locale.ROOT, "%s_anti_radiation_suit", color.getName()),
                    new int[]{2, 4, 3, 1, 4},
                    12,
                    0.0f,
                    0.0f
            ));
    }

    public static final ArmorMaterial ANTI_RADIATION_SUIT = ANTI_RADIATION_SUITS.get(DyeColor.WHITE);

    public static ArmorMaterial antiRadiationSuit(DyeColor color) {
        return ANTI_RADIATION_SUITS.get(color);
    }

    private static ArmorMaterial register(String asset, int[] defense, int enchantmentValue, float toughness, float knockbackResistance) {
        EnumMap<ArmorType, Integer> enumMap = new EnumMap<>(ArmorType.class);
        // helmet, chestplate, leggings, boots, body: the order of the 1.21 ArmorItem.Type the array was written for
        ArmorType[] order = {ArmorType.HELMET, ArmorType.CHESTPLATE, ArmorType.LEGGINGS, ArmorType.BOOTS, ArmorType.BODY};
        for (int i = 0; i < order.length; i++)
            enumMap.put(order[i], defense[i]);

        ResourceKey<EquipmentAsset> assetId = ResourceKey.create(EquipmentAssets.ROOT_ID, CreateNuclear.asResource(asset));
        // durability multiplier 15: helmet 165, chestplate 240, leggings 225, boots 195 (durabilityForType below)
        return new ArmorMaterial(15, enumMap, enchantmentValue, SoundEvents.ARMOR_EQUIP_NETHERITE, toughness, knockbackResistance,
                REPAIRS_ANTI_RADIATION_SUIT, assetId);
    }

    public static void register() {}

    public static int durabilityForType(ArmorType type) {
        int[] BASE_DURABILITY = {11, 16, 15, 13};
        int durabilityMultiplier = 15;
        return BASE_DURABILITY[type.ordinal()] * durabilityMultiplier;
    }
}

package net.nuclearteam.createnuclear.content.effects;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.nuclearteam.createnuclear.CNEffects;
import net.nuclearteam.createnuclear.CNTags;
import net.nuclearteam.createnuclear.CreateNuclear;
import net.nuclearteam.createnuclear.content.equipment.armor.AntiRadiationArmorItem;

public class RadiationEffect extends MobEffect {
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    /**
     * Constructs the RadiationEffect with harmful category and color.
     * Also applies attribute modifiers to reduce speed, attack damage, and attack speed.
     */
    public RadiationEffect() {
        super(MobEffectCategory.HARMFUL, 15453236); // Custom color (hex value)

        // Reduces movement speed by 20%
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                Identifier.fromNamespaceAndPath(CreateNuclear.MOD_ID, "radiation_movement_speed"), -0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        // Reduces attack damage by 20%
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                Identifier.fromNamespaceAndPath(CreateNuclear.MOD_ID, "radiation_attack_damage"), -0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        // Reduces attack speed by 20%
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                Identifier.fromNamespaceAndPath(CreateNuclear.MOD_ID, "radiation_attack_speed"), -0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /**
     * Applies the radiation effect to the entity.
     * - Does nothing if the entity is immune via tag.
     * - Skips damage if the entity wears any anti-radiation armor.
     * - Otherwise, applies magic damage based on the amplifier.
     *
     * @param livingEntity The affected living entity.
     * @param amplifier    The strength (level) of the effect.
     */
    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        // If the entity is immune to radiation, remove the effect
        if (CNTags.CNEntityTags.IRRADIATED_IMMUNE.matches(livingEntity)) {
            livingEntity.removeEffect(CNEffects.RADIATION);
            return true;
        }

        // Check if the entity is wearing any anti-radiation armor
        boolean isWearingAntiRadiationArmor = false;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armor = livingEntity.getItemBySlot(slot);
            if (AntiRadiationArmorItem.Armor.isArmored(armor)) {
                isWearingAntiRadiationArmor = true;
                break;
            }
        }

        // If protected by armor, do not apply damage
        if (isWearingAntiRadiationArmor) {
            return false;

        }

        // Apply radiation damage (magic type), scaled by amplifier
        int damage = 1 << amplifier;
        livingEntity.hurtServer(level, livingEntity.damageSources().magic(), damage);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration >= 1;
    }
}

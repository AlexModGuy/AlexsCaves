package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GingerbreadArmorItem extends ArmorItem {

    private static final double MIN_SPEED_BOOST = 0.1D;
    private static final double MAX_SPEED_BOOST = 1.0D;
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B77"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E12"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B43F"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB111")};
    private Map<Integer, Multimap<Attribute, AttributeModifier>> gingerbreadDurabilityDependentAttributes = new HashMap<>();
    private final Multimap<Attribute, AttributeModifier> defaultAttributes;

    public GingerbreadArmorItem(ArmorMaterial armorMaterial, Type slot) {
        super(armorMaterial, slot, new Properties());
        UUID uuid = ARMOR_MODIFIERS[type.ordinal()];
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.getDefense(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Movement speed", MIN_SPEED_BOOST, AttributeModifier.Operation.MULTIPLY_BASE));
        defaultAttributes = builder.build();
    }

    private Multimap<Attribute, AttributeModifier> getOrCreateDurabilityAttributes(int durabilityIn, int maxDurability) {
        if (gingerbreadDurabilityDependentAttributes.containsKey(durabilityIn)) {
            return gingerbreadDurabilityDependentAttributes.get(durabilityIn);
        } else {
            float scaledDurability = durabilityIn / (float) maxDurability;
            double speed = MIN_SPEED_BOOST + (MAX_SPEED_BOOST - MIN_SPEED_BOOST) * scaledDurability;
            UUID uuid = ARMOR_MODIFIERS[type.ordinal()];
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.getDefense(), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Movement speed", speed, AttributeModifier.Operation.MULTIPLY_BASE));
            Multimap<Attribute, AttributeModifier> attributeModifierMultimap = builder.build();
            gingerbreadDurabilityDependentAttributes.put(durabilityIn, attributeModifierMultimap);
            return attributeModifierMultimap;
        }
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == this.type.getSlot() ? defaultAttributes : ImmutableMultimap.of();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == this.type.getSlot() ? getOrCreateDurabilityAttributes(stack.getDamageValue(), stack.getMaxDamage()) : super.getAttributeModifiers(slot, stack);
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return AlexsCaves.MODID + ":textures/armor/gingerbread_armor_1.png";
        } else {
            return AlexsCaves.MODID + ":textures/armor/gingerbread_armor_0.png";
        }
    }
}

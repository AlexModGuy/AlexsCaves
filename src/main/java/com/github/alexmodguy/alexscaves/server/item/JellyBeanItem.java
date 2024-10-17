package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class JellyBeanItem extends PotionItem {

    public JellyBeanItem() {
        super(new Item.Properties().food(ACFoods.JELLY_BEAN).stacksTo(16));
    }

    public static int getBeanColor(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().getBoolean("Rainbow")) {
            float hue = (System.currentTimeMillis() % 4000) / 4000f;
            int rainbow = Color.HSBtoRGB(hue, 1f, 0.8f);
            return rainbow;
        }
        return PotionUtils.getColor(stack);
    }

    public int getUseDuration(ItemStack itemStack) {
        return 16;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return this.getDescriptionId();
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int useDir) {
        Vec3 vec3 = new Vec3(((double) level.getRandom().nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
        vec3 = vec3.xRot(-living.getXRot() * ((float) Math.PI / 180F));
        vec3 = vec3.yRot(-living.getYRot() * ((float) Math.PI / 180F));
        double d0 = (double) (-level.getRandom().nextFloat()) * 0.6D - 0.3D;
        Vec3 vec31 = new Vec3(((double) level.getRandom().nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
        vec31 = vec31.xRot(-living.getXRot() * ((float) Math.PI / 180F));
        vec31 = vec31.yRot(-living.getYRot() * ((float) Math.PI / 180F));
        vec31 = vec31.add(living.getX(), living.getEyeY(), living.getZ());
        if (level instanceof ServerLevel) {
            ((ServerLevel) level).sendParticles(new ItemParticleOption(ACParticleRegistry.JELLY_BEAN_EAT.get(), stack), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05D, vec3.z, 0.0D);
        } else {
            level.addParticle(new ItemParticleOption(ACParticleRegistry.JELLY_BEAN_EAT.get(), stack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
        }

    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flags) {
        List<MobEffectInstance> effectInstanceList = PotionUtils.getMobEffects(itemStack);
        List<Pair<Attribute, AttributeModifier>> list = Lists.newArrayList();

        for (MobEffectInstance mobeffectinstance : effectInstanceList) {
            MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
            MobEffect mobeffect = mobeffectinstance.getEffect();
            Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
            if (!map.isEmpty()) {
                for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                    AttributeModifier attributemodifier = entry.getValue();
                    AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                    list.add(new Pair<>(entry.getKey(), attributemodifier1));
                }
            }

            if (mobeffectinstance.getAmplifier() > 0) {
                mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
            }

            if (!mobeffectinstance.endsWithin(20)) {
                mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, 1.0F));
            }

            tooltip.add(Component.translatable("item.alexscaves.jelly_bean.desc", mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting())).withStyle(ChatFormatting.GRAY));
        }

        if (!list.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
            tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            for (Pair<Attribute, AttributeModifier> pair : list) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    tooltip.add(Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    tooltip.add(Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        Player player = living instanceof Player ? (Player)living : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!level.isClientSide) {
            for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(stack)) {
                if (mobeffectinstance.getEffect().isInstantenous()) {
                    mobeffectinstance.getEffect().applyInstantenousEffect(player, player, living, mobeffectinstance.getAmplifier(), 1.0D);
                } else {
                    living.addEffect(new MobEffectInstance(mobeffectinstance));
                }
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        living.gameEvent(GameEvent.EAT);
        return stack;
    }
}

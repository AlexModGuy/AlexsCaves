package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.WaveEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class OrtholanceItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public OrtholanceItem(Item.Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 5.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double) -2.4F, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    public UseAnim getUseAnimation(ItemStack p_43417_) {
        return UseAnim.BOW;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    public int getEnchantmentValue() {
        return 1;
    }

    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int useTime) {
        int i = Mth.clamp(this.getUseDuration(stack) - useTime, 0, 60);
        int flinging = stack.getEnchantmentLevel(ACEnchantmentRegistry.FLINGING.get());
        boolean tsunami = stack.getEnchantmentLevel(ACEnchantmentRegistry.TSUNAMI.get()) > 0;
        if (i > 0) {
            float f = 0.1F * i + flinging * 0.1F;
            Vec3 vec3 = livingEntity.getDeltaMovement().add(livingEntity.getViewVector(1.0F).normalize().multiply(f, f * 0.15F, f));
            if (i >= 10 && !level.isClientSide) {
                level.playSound(null, livingEntity, ACSoundRegistry.ORTHOLANCE_WAVE.get(), SoundSource.NEUTRAL, 4.0F, 1.0F);
                stack.hurtAndBreak(1, livingEntity, (player1) -> {
                    player1.broadcastBreakEvent(player1.getUsedItemHand());
                });
                int maxWaves = i / 5;
                if(tsunami){
                    maxWaves = 5;
                    Vec3 waveCenterPos = livingEntity.position().add(vec3);
                    WaveEntity tsunamiWaveEntity = new WaveEntity(level, livingEntity);
                    tsunamiWaveEntity.setPos(waveCenterPos.x, livingEntity.getY(), waveCenterPos.z);
                    tsunamiWaveEntity.setLifespan(20);
                    tsunamiWaveEntity.setWaveScale(5.0F);
                    tsunamiWaveEntity.setWaitingTicks(2);
                    tsunamiWaveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
                    level.addFreshEntity(tsunamiWaveEntity);
                }else{
                    for (int wave = 0; wave < maxWaves; wave++) {
                        float f1 = (float) wave / maxWaves;
                        int lifespan = 3 + (int) ((1F - f1) * 3);
                        Vec3 waveCenterPos = livingEntity.position().add(vec3.scale(f1 * 2));
                        WaveEntity leftWaveEntity = new WaveEntity(level, livingEntity);
                        leftWaveEntity.setPos(waveCenterPos.x, livingEntity.getY(), waveCenterPos.z);
                        leftWaveEntity.setLifespan(lifespan);
                        leftWaveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) + 60 - 15 * wave);
                        level.addFreshEntity(leftWaveEntity);
                        WaveEntity rightWaveEntity = new WaveEntity(level, livingEntity);
                        rightWaveEntity.setPos(waveCenterPos.x, livingEntity.getY(), waveCenterPos.z);
                        rightWaveEntity.setLifespan(lifespan);
                        rightWaveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) - 60 + 15 * wave);
                        level.addFreshEntity(rightWaveEntity);
                    }
                    if(stack.getEnchantmentLevel(ACEnchantmentRegistry.SECOND_WAVE.get()) > 0){
                        int maxSecondWaves = Math.max(1, maxWaves - 1);
                        for (int wave = 0; wave < maxSecondWaves; wave++) {
                            float f1 = (float) wave / maxSecondWaves;
                            int lifespan = 3 + (int) ((1F - f1) * 3);
                            Vec3 waveCenterPos = livingEntity.position().add(vec3.scale(f1 * 2));
                            WaveEntity leftWaveEntity = new WaveEntity(level, livingEntity);
                            leftWaveEntity.setPos(waveCenterPos.x, livingEntity.getY(), waveCenterPos.z);
                            leftWaveEntity.setLifespan(lifespan);
                            leftWaveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) + 60 - 15 * wave);
                            leftWaveEntity.setWaitingTicks(8);
                            level.addFreshEntity(leftWaveEntity);
                            WaveEntity rightWaveEntity = new WaveEntity(level, livingEntity);
                            rightWaveEntity.setPos(waveCenterPos.x, livingEntity.getY(), waveCenterPos.z);
                            rightWaveEntity.setLifespan(lifespan);
                            rightWaveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) - 60 + 15 * wave);
                            rightWaveEntity.setWaitingTicks(8);
                            level.addFreshEntity(rightWaveEntity);
                        }
                    }
                }
                AABB aabb = new AABB(livingEntity.position(), livingEntity.position().add(vec3.scale(maxWaves))).inflate(1);
                DamageSource source = livingEntity.damageSources().mobAttack(livingEntity);
                double d = 0;
                for (AttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
                    d += modifier.getAmount();
                }
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                    if (!livingEntity.isAlliedTo(entity) && !livingEntity.equals(entity) && livingEntity.hasLineOfSight(entity)) {
                        entity.hurt(source, (float) d);
                        entity.stopRiding();
                    }
                }
            }
            livingEntity.setDeltaMovement(vec3.add(0, (livingEntity.onGround() ? 0.2F : 0) + (flinging * 0.1F), 0));
        }
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurt, LivingEntity player) {
        stack.hurtAndBreak(1, player, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        Vec3 vec3 = player.getViewVector(1.0F);
        if(stack.getEnchantmentLevel(ACEnchantmentRegistry.SEA_SWING.get()) > 0){
            WaveEntity waveEntity = new WaveEntity(hurt.level(), player);
            waveEntity.setPos(player.getX(), hurt.getY(), player.getZ());
            waveEntity.setLifespan(5);
            waveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)));
            player.level().addFreshEntity(waveEntity);
        }
        return true;
    }

    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos blockPos, LivingEntity livingEntity) {
        if ((double) state.getDestroySpeed(level, blockPos) != 0.0D) {
            itemStack.hurtAndBreak(2, livingEntity, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }


    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }
}

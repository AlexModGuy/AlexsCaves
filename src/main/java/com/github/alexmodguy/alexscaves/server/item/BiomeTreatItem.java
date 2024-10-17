package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class BiomeTreatItem extends CaveInfoItem {

    public BiomeTreatItem() {
        super(new Item.Properties().stacksTo(1), false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.isEdible() && getCaveBiome(itemstack) == null) {
            if (player.canEat(itemstack.getFoodProperties(player).canAlwaysEat())) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level worldIn, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
        ResourceKey<Biome> biomeResourceKey = getCaveBiome(stack);
        if (biomeResourceKey == null) {
            tooltip.add(Component.translatable("item.alexscaves.biome_treat.desc").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    public static int getBiomeTreatColorOf(Level level, ItemStack stack) {
        float hue = (System.currentTimeMillis() % 4000) / 4000f;
        int rainbow = Color.HSBtoRGB(hue, 1f, 0.8f);
        if (stack.getItem() instanceof BiomeTreatItem) {
            ResourceKey<Biome> biomeResourceKey = getCaveBiome(stack);
            return biomeResourceKey == null ? rainbow : getBiomeColor(level, biomeResourceKey);
        }
        return -1;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(getCaveBiome(stack) == null && (livingEntity instanceof Player player && (player.getFoodData().getFoodLevel() == 0 || player.isCreative()))){
            return create(this, level.getBiome(livingEntity.blockPosition()).unwrapKey().get());
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        return getCaveBiome(stack) == null ? ACFoods.BIOME_TREAT : ACFoods.BIOME_TREAT_DONE;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_EAT;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.DRINK;
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

}

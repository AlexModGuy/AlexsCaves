package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class RaygunItem extends Item {
    public RaygunItem() {
        super(new Item.Properties().defaultDurability(1000));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }


    public static boolean hasCharge(ItemStack stack) {
        return stack.getDamageValue() < stack.getMaxDamage() - 1;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (hasCharge(itemstack)) {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        boolean using = entity instanceof LivingEntity living && living.getUseItem().equals(stack);
        if (level.isClientSide) {
            int useTime = getUseTime(stack);
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.getInt("PrevUseTime") != tag.getInt("UseTime")) {
                tag.putInt("PrevUseTime", getUseTime(stack));
            }
            if (using && useTime < 5.0F) {
                setUseTime(stack, useTime + 1);
            }
            if (!using && useTime > 0.0F) {
                setUseTime(stack, useTime - 1);
            }
        }
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int timeUsing) {
        float time = getUseTime(stack) / 5F;
        if(time > 0.0F){
            float maxDist = 32.0F * time;
            HitResult hitresult = ProjectileUtil.getHitResultOnViewVector(living, Entity::canBeHitByProjectile, maxDist);
            Vec3 vec3 = hitresult.getLocation();
            if(level.isClientSide){
                setRayPosition(stack, vec3.x, vec3.y, vec3.z);
            }
            level.addParticle(ACParticleRegistry.HAZMAT_BREATHE.get(), vec3.x, vec3.y, vec3.z, 0, 0, 0);
        }
    }
    public static void setUseTime(ItemStack stack, int useTime) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("PrevUseTime", getUseTime(stack));
        tag.putInt("UseTime", useTime);
    }

    public static void setRayPosition(ItemStack stack, double x, double y, double z) {
        CompoundTag tag = stack.getOrCreateTag();
        Vec3 prev = getRayPosition(stack);
        tag.putDouble("PrevRayX", prev.x);
        tag.putDouble("PrevRayY", prev.y);
        tag.putDouble("PrevRayZ", prev.z);
        tag.putDouble("RayX", x);
        tag.putDouble("RayY", y);
        tag.putDouble("RayZ", z);
    }

    public static int getUseTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("UseTime") : 0;
    }

    public static Vec3 getRayPosition(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        if(compoundtag != null && compoundtag.contains("RayX")){
            return  new Vec3(compoundtag.getDouble("RayX"), compoundtag.getDouble("RayY"), compoundtag.getDouble("RayZ"));
        }else{
            return Vec3.ZERO;
        }
    }

    public static float getLerpedUseTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        float prev = compoundtag != null ? (float) compoundtag.getInt("PrevUseTime") : 0F;
        float current = compoundtag != null ? (float) compoundtag.getInt("UseTime") : 0F;
        return prev + f * (current - prev);
    }

    @Nullable
    public static Vec3 getLerpedRayPosition(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        if(compoundtag != null){
            double prevX = (float) compoundtag.getDouble("PrevRayX");
            double x = (float) compoundtag.getDouble("RayX");
            double prevY = (float) compoundtag.getDouble("PrevRayY");
            double y = (float) compoundtag.getDouble("RayY");
            double prevZ = (float) compoundtag.getDouble("PrevRayZ");
            double z = (float) compoundtag.getDouble("RayZ");
            return  new Vec3(prevX + f * (x - prevX), prevY + f * (y - prevY), prevZ + f * (z - prevZ));
        }else{
            return null;
        }
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(ACItemRegistry.RAYGUN.get()) || !newStack.is(ACItemRegistry.RAYGUN.get());
    }
}

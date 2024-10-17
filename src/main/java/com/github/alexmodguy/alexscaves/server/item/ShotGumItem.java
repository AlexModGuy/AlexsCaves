package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.GumballEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Predicate;

public class ShotGumItem extends Item implements UpdatesStackTags, AlwaysCombinableOnAnvil {

    public static final Predicate<ItemStack> AMMO = (stack) -> {
        return stack.getItem() == ACItemRegistry.GUMBALL_PILE.get().asItem();
    };
    public static final int MAX_AMMO = 4;

    public ShotGumItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(ACItemRegistry.SHOT_GUM.get()) || !newStack.is(ACItemRegistry.SHOT_GUM.get());
    }

    public static float getLerpedShootTime(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        float prev = compoundtag != null ? (float) compoundtag.getInt("PrevShootTime") : 0F;
        float current = compoundtag != null ? (float) compoundtag.getInt("ShootTime") : 0F;
        return prev + f * (current - prev);
    }

    public static float getLerpedCrankAngle(ItemStack stack, float f) {
        CompoundTag compoundtag = stack.getTag();
        float prev = compoundtag != null ? (float) compoundtag.getFloat("PrevCrankAngle") : 0F;
        float current = compoundtag != null ? (float) compoundtag.getFloat("CrankAngle") : 0F;
        return prev + f * (current - prev);
    }

    public static int getShootTime(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getInt("ShootTime") : 0;
    }

    public static void setShootTime(ItemStack stack, int i) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        compoundtag.putInt("ShootTime", i);
    }

    public static float getCrankAngle(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null ? compoundtag.getFloat("CrankAngle") : 0;
    }

    public static void setCrankAngle(ItemStack stack, float angle) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        compoundtag.putFloat("CrankAngle", angle);
    }

    public static int getGumballsLeft(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.contains("Gumballs") ? compoundtag.getInt("Gumballs") : MAX_AMMO;
    }

    public static void setGumballsLeft(ItemStack stack, int i) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        compoundtag.putInt("Gumballs", i);
    }

    public static boolean isShooting(ItemStack stack) {
        CompoundTag compoundtag = stack.getTag();
        return compoundtag != null && compoundtag.getBoolean("Shooting");
    }

    public static void setShooting(ItemStack stack, boolean shooting) {
        CompoundTag compoundtag = stack.getOrCreateTag();
        compoundtag.putBoolean("Shooting", shooting);
    }

    public static boolean hasAmmo(ItemStack stack) {
        return getGumballsLeft(stack) > 0;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (hasAmmo(itemstack)) {
            if(getShootTime(itemstack) == 0 && !isShooting(itemstack)){
                setShooting(itemstack, true);
            }
            return InteractionResultHolder.pass(itemstack);
        } else {
            ItemStack ammo = findAmmo(player);
            boolean flag = player.isCreative();
            if (!ammo.isEmpty()) {
                ammo.shrink(1);
                flag = true;
            }
            if (flag) {
                setGumballsLeft(itemstack, MAX_AMMO);
                player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), ACSoundRegistry.SHOTGUM_RELOAD.get(), player.getSoundSource(), 1.0F, 1.0F);
            } else {
                player.level().playSound((Player) null, player.getX(), player.getY(), player.getZ(), ACSoundRegistry.SHOTGUM_EMPTY.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
            return InteractionResultHolder.consume(itemstack);
        }
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        boolean shooting = isShooting(stack);
        int shootTime = getShootTime(stack);
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt("PrevShootTime") != tag.getInt("ShootTime")) {
            tag.putInt("PrevShootTime", getShootTime(stack));
        }
        if (tag.getFloat("PrevCrankAngle") != tag.getFloat("CrankAngle")) {
            tag.putFloat("PrevCrankAngle", getCrankAngle(stack));
        }

        if(shootTime == 5 && shooting){
            level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(), ACSoundRegistry.SHOTGUM_SHOOT.get(), entity.getSoundSource(), 1.0F, 1.0F);
            setShooting(stack, false);
            boolean leftHand = false;
            boolean ricochetEnchant = stack.getEnchantmentLevel(ACEnchantmentRegistry.TARGETED_RICOCHET.get()) > 0;
            boolean splitEnchant = stack.getEnchantmentLevel(ACEnchantmentRegistry.TRIPLE_SPLIT.get()) > 0;
            boolean explosiveEnchant = stack.getEnchantmentLevel(ACEnchantmentRegistry.EXPLOSIVE_FLAVOR.get()) > 0;
            int maximumBounces = 4 + stack.getEnchantmentLevel(ACEnchantmentRegistry.BOUNCY_BALL.get()) * 2;
            if(entity instanceof LivingEntity living){
                boolean mainHand = living.getItemInHand(InteractionHand.MAIN_HAND) == stack;
                boolean offHand = living.getItemInHand(InteractionHand.OFF_HAND) == stack;
                leftHand = mainHand && living.getMainArm() == HumanoidArm.LEFT || offHand && living.getMainArm() == HumanoidArm.RIGHT;
                for(int gumballs = 0; gumballs < (explosiveEnchant ? 1 : 2); gumballs++){
                    GumballEntity gumballEntity = new GumballEntity(level, living);
                    Vec3 relativePos = new Vec3((explosiveEnchant ? 0.0F : (gumballs < 1 ? 0.15F : -0.15F)) + (leftHand ? 0.35F : -0.35F), 0, 0.75F).xRot(-entity.getXRot() * ((float) Math.PI / 180F)).yRot(-entity.getYHeadRot() * ((float) Math.PI / 180F));
                    Vec3 gumballPos = entity.position().add(0, entity.getBbHeight() * 0.8F, 0).add(relativePos);
                    gumballEntity.setPos(gumballPos);
                    gumballEntity.setTargetsOnBounce(ricochetEnchant);
                    gumballEntity.setSplitsOnHit(splitEnchant);
                    gumballEntity.setExplosive(explosiveEnchant);
                    gumballEntity.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, explosiveEnchant ? 0.5F : 1.5F, 5.0F);
                    gumballEntity.setMaximumBounces(maximumBounces);
                    gumballEntity.setDamage(4.0F);
                    if(!level.isClientSide){
                        level.addFreshEntity(gumballEntity);
                    }
                }
            }
            if(!(entity instanceof Player player && player.isCreative())){
                setGumballsLeft(stack, Math.max(getGumballsLeft(stack) - 1, 0));
            }
        }
        if (shooting && shootTime < 5) {
            setShootTime(stack, shootTime + 1);
        }
        if (!shooting && shootTime > 0) {
            setShootTime(stack, shootTime - 1);
        }
        if(shooting){
            setCrankAngle(stack, getCrankAngle(stack) + 45);
        }
    }

    public static boolean shouldBeHeldUpright(ItemStack itemStack) {
        return hasAmmo(itemStack);
    }

    private ItemStack findAmmo(Player entity) {
        if (entity.isCreative()) {
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < entity.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack1 = entity.getInventory().getItem(i);
            if (AMMO.test(itemstack1)) {
                return itemstack1;
            }
        }
        return ItemStack.EMPTY;
    }


    public boolean isBarVisible(ItemStack stack) {
        return getGumballsLeft(stack) != MAX_AMMO;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round((float) getGumballsLeft(stack) * 13.0F / (float) MAX_AMMO);
    }

    public int getBarColor(ItemStack stack) {
        return 0XFF9FFF;
    }
}

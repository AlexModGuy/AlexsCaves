package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.WaterBoltEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SeaStaffItem extends Item {
    public SeaStaffItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getISTERProperties());
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), ACSoundRegistry.SEA_STAFF_CAST.get(), SoundSource.PLAYERS, 0.5F, (level.getRandom().nextFloat() * 0.45F + 0.75F));
        player.swing(hand);
        float seekAmount = itemstack.getEnchantmentLevel(ACEnchantmentRegistry.SOAK_SEEKING.get());
        if (!level.isClientSide) {
            double dist = 128;
            Entity closestValid = getClosestLookingAtEntityFor(level, player, dist);
            int bolts = itemstack.getEnchantmentLevel(ACEnchantmentRegistry.TRIPLE_SPLASH.get()) > 0 ? 3 : 1;
            for(int i = 0; i < bolts; i++){
                float shootRot = i == 0 ? 0 : i == 1 ? -50 : 50;
                WaterBoltEntity bolt = new WaterBoltEntity(level, player);
                float rot = player.yHeadRot + (hand == InteractionHand.MAIN_HAND ? 45 : -45);
                bolt.setPos(player.getX() - (double) (player.getBbWidth()) * 1.1F * (double) Mth.sin(rot * ((float) Math.PI / 180F)), player.getEyeY() - (double) 0.4F, player.getZ() + (double) (player.getBbWidth()) * 1.1F * (double) Mth.cos(rot * ((float) Math.PI / 180F)));
                bolt.shootFromRotation(player, player.getXRot(), player.getYRot() + shootRot, -20.0F, i > 0 ? 1F : 2F, 12F);
                if (itemstack.getEnchantmentLevel(ACEnchantmentRegistry.ENVELOPING_BUBBLE.get()) > 0) {
                    bolt.setBubbling(player.getRandom().nextBoolean());
                }
                if (itemstack.getEnchantmentLevel(ACEnchantmentRegistry.BOUNCING_BOLT.get()) > 0) {
                    bolt.ricochet = true;
                }
                bolt.seekAmount = 0.3F + seekAmount * 0.2F;
                if (closestValid != null) {
                    bolt.setArcingTowards(closestValid.getUUID());
                }
                level.addFreshEntity(bolt);
            }

        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.hurtAndBreak(1, player, (player1) -> {
                player1.broadcastBreakEvent(hand);
            });
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public static Entity getClosestLookingAtEntityFor(Level level, Player player, double dist) {
        Entity closestValid = null;
        Vec3 playerEyes = player.getEyePosition(1.0F);
        HitResult hitresult = level.clip(new ClipContext(playerEyes, playerEyes.add(player.getLookAngle().scale(dist)), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        if (hitresult instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult) hitresult).getEntity();
            if (!entity.equals(player) && !player.isAlliedTo(entity) && !entity.isAlliedTo(player) && entity instanceof Mob && player.hasLineOfSight(entity)) {
                closestValid = entity;
            }
        } else {
            Vec3 at = hitresult.getLocation();
            AABB around = new AABB(at.add(-0.5F, -0.5F, -0.5F), at.add(0.5F, 0.5F, 0.5F)).inflate(15);
            for (Entity entity : level.getEntitiesOfClass(LivingEntity.class, around.inflate(dist))) {
                if (!entity.equals(player) && !player.isAlliedTo(entity) && !entity.isAlliedTo(player) && entity instanceof Mob && player.hasLineOfSight(entity)) {
                    if (closestValid == null || entity.distanceToSqr(at) < closestValid.distanceToSqr(at)) {
                        closestValid = entity;
                    }
                }
            }
        }
        return closestValid;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        boolean using = entity instanceof LivingEntity living && living.getUseItem().equals(stack);
        if (!level.isClientSide) {
            if (stack.getEnchantmentLevel(ACEnchantmentRegistry.SEAPAIRING.get()) > 0 && !using) {
                if (level.random.nextFloat() < 0.02F) {
                    if (entity.isInWaterRainOrBubble()) {
                        stack.setDamageValue(Math.min(0, stack.getDamageValue() - 1));
                    }
                }
            }
        }
    }

}

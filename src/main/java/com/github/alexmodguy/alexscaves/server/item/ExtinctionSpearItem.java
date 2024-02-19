package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.ExtinctionSpearEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExtinctionSpearItem extends SpearItem {
    public ExtinctionSpearItem(Item.Properties properties) {
        super(properties, 8.0D);
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i1) {
        if (livingEntity instanceof Player player) {
            int i = this.getUseDuration(itemStack) - i1;
            float f = getPowerForTime(i);
            if (f > 0.1D) {
                itemStack.hurtAndBreak(1, livingEntity, (entity) -> {
                    entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
                ExtinctionSpearEntity spearEntity = new ExtinctionSpearEntity(level, player, itemStack);
                spearEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.5F, 1.0F);
                if (player.getAbilities().instabuild) {
                    spearEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                level.addFreshEntity(spearEntity);
                level.playSound((Player) null, spearEntity, ACSoundRegistry.EXTINCTION_SPEAR_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            killGrottoGhostsFor(player, false);
        }
    }

    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int timeUsing) {
        if(timeUsing == getUseDuration(stack)){
            level.playSound((Player) null, living, ACSoundRegistry.EXTINCTION_SPEAR_SUMMON.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            int grottoHeads = 3 + stack.getEnchantmentLevel(ACEnchantmentRegistry.HERD_PHALANX.get());
            float grottoRotateBy = 360F / grottoHeads;
            for(int i = 0; i < grottoHeads; i++){
                DinosaurSpiritEntity dinosaurSpirit = ACEntityRegistry.DINOSAUR_SPIRIT.get().create(level);
                dinosaurSpirit.copyPosition(living);
                dinosaurSpirit.setDinosaurType(DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS);
                dinosaurSpirit.setPlayerUUID(living.getUUID());
                dinosaurSpirit.setRotateOffset(i * grottoRotateBy);
                level.addFreshEntity(dinosaurSpirit);
            }
        }
    }

    public static boolean killGrottoGhostsFor(Player player, boolean justTheClosest){
        DinosaurSpiritEntity closest = null;
        for(DinosaurSpiritEntity spirit : player.level().getEntitiesOfClass(DinosaurSpiritEntity.class, player.getBoundingBox().inflate(30, 30, 30))){
            if(spirit.getPlayerUUID().equals(player.getUUID()) && spirit.getDinosaurType() == DinosaurSpiritEntity.DinosaurType.GROTTOCERATOPS && !spirit.isFading()){
                if(!justTheClosest){
                    spirit.setFading(true);
                }else if(closest == null || closest.distanceTo(player) > spirit.distanceTo(closest)){
                    closest = spirit;
                }
            }
        }
        if(justTheClosest && closest != null){
            closest.setFading(true);
            return true;
        }
        return !justTheClosest;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity hurtEntity, LivingEntity player) {
        hurtEntity.setSecondsOnFire(5);
        DinosaurSpiritEntity dinosaurSpirit = ACEntityRegistry.DINOSAUR_SPIRIT.get().create(player.level());
        Vec3 between = player.position().add(hurtEntity.position()).scale(0.5F);
        dinosaurSpirit.setPos(between.x, player.getY() + 1.0F, between.z);
        dinosaurSpirit.setDinosaurType(DinosaurSpiritEntity.DinosaurType.TREMORSAURUS);
        dinosaurSpirit.setPlayerUUID(player.getUUID());
        dinosaurSpirit.setEnchantmentLevel(stack.getEnchantmentLevel(ACEnchantmentRegistry.CHOMPING_SPIRIT.get()));
        dinosaurSpirit.setAttackingEntityId(hurtEntity.getId());
        dinosaurSpirit.lookAt(EntityAnchorArgument.Anchor.EYES, hurtEntity.getEyePosition());
        dinosaurSpirit.setDelaySpawn(5);

        player.level().addFreshEntity(dinosaurSpirit);
        return super.hurtEnemy(stack, hurtEntity, player);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public boolean isValidRepairItem(ItemStack item, ItemStack repairItem) {
        return repairItem.is(ACItemRegistry.TECTONIC_SHARD.get()) || super.isValidRepairItem(item, repairItem);
    }
}

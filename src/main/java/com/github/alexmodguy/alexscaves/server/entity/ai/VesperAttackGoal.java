package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.VesperEntity;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;

import java.util.EnumSet;

public class VesperAttackGoal extends Goal {

    private VesperEntity entity;

    private Vec3 startOrbitFrom;
    private int orbitTime;
    private int maxOrbitTime;
    private boolean clockwise;

    public VesperAttackGoal(VesperEntity entity) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = entity.getTarget();
        return target != null && target.isAlive();
    }

    public void tick() {
        if(entity.isHanging()){
            entity.setHanging(false);
            entity.setFlying(true);
        }else if(!entity.isFlying() && entity.groundedFor <= 0){
            entity.setFlying(true);
        }
        LivingEntity target = entity.getTarget();
        if (target != null && target.isAlive()) {
            double distance = entity.distanceTo(target);
            float f = entity.getBbWidth() + target.getBbWidth();
            if (startOrbitFrom == null) {
                entity.getNavigation().moveTo(target, entity.isFlying() ? 2.5D : 1D);
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            } else if (orbitTime < maxOrbitTime && entity.groundedFor <= 0) {
                orbitTime++;
                float zoomIn = 1F - orbitTime / (float) maxOrbitTime;
                Vec3 orbitPos = orbitAroundPos(3.0F + zoomIn * 5.0F).add(0, 4 + zoomIn * 3, 0);
                entity.getNavigation().moveTo(orbitPos.x, orbitPos.y, orbitPos.z, entity.isFlying() ? 2.5D : 1D);
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, orbitPos);
            } else {
                orbitTime = 0;
                startOrbitFrom = null;
            }
            if (distance < f + 2.0D) {
                if (entity.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    entity.setAnimation(VesperEntity.ANIMATION_BITE);
                } else if (entity.getAnimationTick() == 8 && entity.hasLineOfSight(target)) {
                    boolean flag = target.isBlocking();
                    if(target.hurt(target.damageSources().mobAttack(entity), (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue())){
                        if(!target.isAlive() && this.entity.level.getBrightness(LightLayer.BLOCK, this.entity.blockPosition()) > 7){
                            entity.groundedFor += 40 + entity.getRandom().nextInt(20);
                        }
                    }
                    maxOrbitTime = 60 + entity.getRandom().nextInt(80);
                    startOrbitFrom = target.getEyePosition();
                    if(flag){
                        if (target instanceof final Player player) {
                            damageShieldFor(player, (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
                        }
                        entity.groundedFor = 60 + entity.getRandom().nextInt(40);
                        entity.setFlying(false);
                        startOrbitFrom = null;
                    }
                }
            }

        }
    }

    @Override
    public void start() {
        orbitTime = 0;
        maxOrbitTime = 80;
        startOrbitFrom = null;
    }

    public Vec3 orbitAroundPos(float circleDistance) {
        final float angle = 3 * (float) (Math.toRadians((clockwise ? -orbitTime : orbitTime) * 3F));
        final double extraX = circleDistance * Mth.sin((angle));
        final double extraZ = circleDistance * Mth.cos(angle);
        return startOrbitFrom.add(extraX, 0, extraZ);
    }

    protected void damageShieldFor(Player holder, float damage) {
        if (holder.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
            if (!entity.level.isClientSide) {
                holder.awardStat(Stats.ITEM_USED.get(holder.getUseItem().getItem()));
            }

            if (damage >= 3.0F) {
                int i = 1 + Mth.floor(damage);
                InteractionHand hand = holder.getUsedItemHand();
                holder.getUseItem().hurtAndBreak(i, holder, (p_213833_1_) -> {
                    p_213833_1_.broadcastBreakEvent(hand);
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(holder, holder.getUseItem(), hand);
                });
                if (holder.getUseItem().isEmpty()) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        holder.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        holder.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    holder.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + entity.level.random.nextFloat() * 0.4F);
                }
            }

        }
    }
}

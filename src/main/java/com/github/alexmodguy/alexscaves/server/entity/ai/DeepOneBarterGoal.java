package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.AbyssalAltarBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public class DeepOneBarterGoal extends Goal {
    private BlockPos altarPos = null;
    private DeepOneBaseEntity mob;

    private int executionCooldown = 10;

    private boolean groundTarget = false;

    public DeepOneBarterGoal(DeepOneBaseEntity mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if ((target == null || !target.isAlive()) && mob.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            if (executionCooldown-- > 0) {
                return false;
            } else {
                executionCooldown = 150 + mob.getRandom().nextInt(100);
                BlockPos pos = null;
                if (mob.getLastAltarPos() != null) {
                    if (mob.level().getBlockEntity(mob.getLastAltarPos()) instanceof AbyssalAltarBlockEntity altar) {
                        executionCooldown = 10;
                        if (altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)) {
                            pos = mob.getLastAltarPos();
                        }
                    } else {
                        mob.setLastAltarPos(null);
                    }
                }
                if (pos == null) {
                    List<BlockPos> list = getNearbyAltars(mob.blockPosition(), (ServerLevel) mob.level(), 64).sorted(Comparator.comparingDouble(mob.blockPosition()::distSqr)).toList();
                    if (!list.isEmpty()) {
                        pos = list.get(0);
                    }
                }
                if (pos != null) {
                    altarPos = pos;
                    mob.setLastAltarPos(altarPos);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return altarPos != null && (hasPearls(mob.level(), altarPos, false) || mob.getAnimation() == mob.getTradingAnimation()) && (target == null || !target.isAlive());
    }

    private static Stream<BlockPos> getNearbyAltars(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.ABYSSAL_ALTAR.getKey()), blockpos2 -> hasPearls(world, blockpos2, true), blockpos, range, PoiManager.Occupancy.ANY);
    }

    private static boolean hasPearls(Level world, BlockPos pos, boolean timed) {
        if (world.getBlockEntity(pos) instanceof AbyssalAltarBlockEntity altar) {
            if (timed && world.getGameTime() - altar.getLastInteractionTime() < 100) {
                return false;
            }
            return altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS) && altar.getBlockState().getBlock() instanceof AbyssalAltarBlock && !altar.getBlockState().getValue(AbyssalAltarBlock.ACTIVE);
        }
        return false;
    }


    public void tick() {
        Vec3 center = Vec3.atCenterOf(altarPos);
        double distance = Vec3.atBottomCenterOf(altarPos).subtract(mob.position()).horizontalDistance();
        if (distance < 8) {
            mob.getLookControl().setLookAt(center.x, center.y, center.z, 10.0F, (float) this.mob.getMaxHeadXRot());
        }
        if (distance > 3) {
            mob.getNavigation().moveTo(altarPos.getX() + 0.5F, altarPos.getY(), altarPos.getZ() + 0.5F, 1);
        } else {
            mob.setLastAltarPos(altarPos);
            mob.setTradingLockedTime(50);
            mob.getNavigation().stop();
            if (mob.level().getBlockEntity(altarPos) instanceof AbyssalAltarBlockEntity altar) {
                if (altar.getItem(0).is(ACTagRegistry.DEEP_ONE_BARTERS)) {
                    if (altar.queueItemDrop(altar.getItem(0))) {
                        mob.level().broadcastEntityEvent(mob, (byte) 69);
                        altar.onEntityInteract(mob, true);
                        altar.setItem(0, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}
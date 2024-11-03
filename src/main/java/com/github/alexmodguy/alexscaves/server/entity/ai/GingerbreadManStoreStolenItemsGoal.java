package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.GingerbarrelBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.stream.Stream;

public class GingerbreadManStoreStolenItemsGoal extends Goal {

    private int executionCooldown;
    private BlockPos barrelPos = null;
    private final GingerbreadManEntity mob;
    private int openBarrelTicks = 0;

    public GingerbreadManStoreStolenItemsGoal(GingerbreadManEntity mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if ((target == null || !target.isAlive()) && (mob.getItemInHand(InteractionHand.OFF_HAND).is(ACTagRegistry.GINGERBREAD_MAN_STEALS) || mob.isOvenSpawned() && !mob.getItemInHand(InteractionHand.OFF_HAND).isEmpty())) {
            if (executionCooldown-- > 0) {
                return false;
            } else {
                executionCooldown = 20 + mob.getRandom().nextInt(30);
                BlockPos pos = null;
                if (mob.getLastBarrelPos() != null) {
                    if (doesBarrelHaveSpace(mob.level(), mob.getLastBarrelPos(), mob.getItemInHand(InteractionHand.OFF_HAND))) {
                        executionCooldown = 10;
                        pos = mob.getLastBarrelPos();
                    } else {
                        mob.setLastBarrelPos(null);
                    }
                }
                if (pos == null) {
                    List<BlockPos> list = getNearbyBarrels(mob.blockPosition(), (ServerLevel) mob.level(), 32).sorted(Comparator.comparingDouble(mob.blockPosition()::distSqr)).toList();
                    if (!list.isEmpty()) {
                        pos = list.get(0);
                    }
                }
                if (pos != null) {
                    barrelPos = pos;
                    mob.setLastBarrelPos(barrelPos);
                    openBarrelTicks = 0;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return barrelPos != null && !mob.getItemInHand(InteractionHand.OFF_HAND).isEmpty() && doesBarrelHaveSpace(mob.level(), barrelPos, mob.getItemInHand(InteractionHand.OFF_HAND)) && (target == null || !target.isAlive());
    }

    public void stop() {
        if (barrelPos != null) {
            BlockState barrelState = mob.level().getBlockState(barrelPos);
            if (barrelState.is(ACBlockRegistry.GINGERBARREL.get())) {
                mob.level().setBlockAndUpdate(barrelPos, barrelState.setValue(BarrelBlock.OPEN, false));
            }
        }
        barrelPos = null;
    }

    private Stream<BlockPos> getNearbyBarrels(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.GINGERBARREL.getKey()), blockpos2 -> doesBarrelHaveSpace(world, blockpos2, mob.getItemInHand(InteractionHand.OFF_HAND)), blockpos, range, PoiManager.Occupancy.ANY);
    }

    private static boolean doesBarrelHaveSpace(Level world, BlockPos pos, ItemStack addTo) {
        if (world.getBlockEntity(pos) instanceof GingerbarrelBlockEntity barrel) {
            for (int i = 0; i < barrel.getContainerSize(); i++) {
                ItemStack stack = barrel.getItem(i);
                if (stack.isEmpty()) {
                    return true;
                } else if (stack.is(addTo.getItem()) && stack.getCount() + addTo.getCount() <= addTo.getMaxStackSize()) {
                    return true;
                }
            }
        }
        return false;
    }


    public void tick() {
        Vec3 center = Vec3.atCenterOf(barrelPos);
        double distance = Vec3.atBottomCenterOf(barrelPos).subtract(mob.position()).horizontalDistance();
        if (distance < 8.0D) {
            mob.getLookControl().setLookAt(center.x, center.y, center.z, 10.0F, (float) this.mob.getMaxHeadXRot());
        }
        if (distance > 1.5D && hasLineOfSightBarrel()) {
            mob.getNavigation().moveTo(barrelPos.getX() + 0.5F, barrelPos.getY(), barrelPos.getZ() + 0.5F, 1);
        } else {
            if(openBarrelTicks < 5 && (mob.getAnimation() == IAnimatedEntity.NO_ANIMATION || mob.getAnimation() == null)){
                mob.setAnimation(mob.getAnimationForHand(true));
            }
            openBarrelTicks++;
            mob.setLastBarrelPos(barrelPos);
            mob.getNavigation().stop();
            if (openBarrelTicks < 12 && openBarrelTicks > 5) {
                BlockState barrelState = mob.level().getBlockState(barrelPos);
                if (barrelState.is(ACBlockRegistry.GINGERBARREL.get())) {
                    mob.level().setBlockAndUpdate(barrelPos, barrelState.setValue(BarrelBlock.OPEN, true));
                }
            } else if(openBarrelTicks >= 12){
                if (mob.level().getBlockEntity(barrelPos) instanceof GingerbarrelBlockEntity barrel) {
                    ItemStack itemstack = mob.getItemInHand(InteractionHand.OFF_HAND).copy();
                    ItemStack itemstack1 = HopperBlockEntity.addItem(null, barrel, itemstack, null);
                    if(!mob.isOvenSpawned()){
                        int i = barrel.getContainerSize();
                        Set<Item> candyItems = new HashSet<>();
                        for(int j = 0; j < i && !barrel.isEmpty(); ++j) {
                           ItemStack itemStack = barrel.getItem(j);
                           if(itemStack.is(ACTagRegistry.GINGERBREAD_MAN_STEALS)){
                               candyItems.add(itemStack.getItem());
                           }
                        }
                        if(candyItems.size() >= 9){
                            for(int j = 0; j < i && !barrel.isEmpty(); ++j) {
                                ItemStack itemStack = barrel.getItem(j);
                                if(itemStack.is(ACTagRegistry.GINGERBREAD_MAN_STEALS)){
                                    itemStack.shrink(1);
                                }
                            }
                            itemstack1 = HopperBlockEntity.addItem(null, barrel, getRandomArmor(mob.getRandom()), null);
                        }
                    }
                    mob.setItemInHand(InteractionHand.OFF_HAND, itemstack1);
                    stop();
                }
            }
        }
    }

    private ItemStack getRandomArmor(RandomSource random) {
        float f = random.nextFloat();
        if(f < 0.25F){
            return new ItemStack(ACItemRegistry.GINGERBREAD_BOOTS.get());
        }else if(f < 0.5F){
            return new ItemStack(ACItemRegistry.GINGERBREAD_LEGGINGS.get());
        }else if(f < 0.75F){
            return new ItemStack(ACItemRegistry.GINGERBREAD_CHESTPLATE.get());
        }else{
            return new ItemStack(ACItemRegistry.GINGERBREAD_HELMET.get());
        }
    }

    public boolean hasLineOfSightBarrel() {
        HitResult raytraceresult = mob.level().clip(new ClipContext(mob.getEyePosition(1.0F), new Vec3(barrelPos.getX() + 0.5, barrelPos.getY() + 0.5, barrelPos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        if (raytraceresult instanceof BlockHitResult blockRayTraceResult) {
            BlockPos pos = blockRayTraceResult.getBlockPos();
            return pos.equals(barrelPos) || mob.level().isEmptyBlock(pos) || this.mob.level().getBlockEntity(pos) == this.mob.level().getBlockEntity(barrelPos);
        }
        return true;
    }

}
package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.entity.living.GingerbreadManEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GingerbreadManStealGoal extends Goal {

    private final GingerbreadManEntity gingerbreadMan;
    private Entity target;
    private boolean hasStolen;
    private int executionCooldown = 0;
    private int recheckInventoryCooldown = 0;

    public GingerbreadManStealGoal(GingerbreadManEntity gingerbreadMan) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.gingerbreadMan = gingerbreadMan;
    }

    @Override
    public boolean canUse() {
        if(gingerbreadMan.isOvenSpawned()){
            return false;
        }
        if (executionCooldown > 0) {
            executionCooldown--;
            return false;
        } else if (gingerbreadMan.getItemInHand(InteractionHand.OFF_HAND).isEmpty() && gingerbreadMan.getRandom().nextInt(60) == 0) {
            executionCooldown = 120 + gingerbreadMan.getRandom().nextInt(120);
            Entity newTarget = findStealTarget();
            if (newTarget != null && newTarget.isAlive() && canStealFromEntityType(newTarget)) {
                target = newTarget;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !hasStolen && (target != null && target.isAlive()) && canStealFromEntityType(target);
    }


    @Override
    public void start() {
        hasStolen = false;
    }

    @Override
    public void tick() {
        if (recheckInventoryCooldown < 0) {
            recheckInventoryCooldown = 15;
            if (!hasStealableInventory(target)) {
                target = null;
                return;
            }
        }
        if (target != null) {
            gingerbreadMan.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            double dist = gingerbreadMan.distanceTo(target);
            if (dist < target.getBbWidth() + 1.0D && gingerbreadMan.hasLineOfSight(target)) {
                if(gingerbreadMan.getAnimation() == IAnimatedEntity.NO_ANIMATION || gingerbreadMan.getAnimation() == null){
                    gingerbreadMan.setAnimation(gingerbreadMan.getAnimationForHand(false));
                }
                if(gingerbreadMan.getAnimation() == gingerbreadMan.getAnimationForHand(false) && gingerbreadMan.getAnimationTick() == 8){
                    ItemStack stolenItem = stealOneFrom(target);
                    hasStolen = true;
                    gingerbreadMan.setItemInHand(InteractionHand.OFF_HAND, stolenItem);
                    gingerbreadMan.setCarryingItem(true);
                    gingerbreadMan.fleeFromFor(target, 120 + gingerbreadMan.getRandom().nextInt(60));
                }
            } else {
                gingerbreadMan.getNavigation().moveTo(target, 1.0D);
            }
        }
    }

    @Nullable
    public Entity findStealTarget() {
        List<Entity> list = gingerbreadMan.level().getEntities(gingerbreadMan, gingerbreadMan.getBoundingBox().inflate(20.0D), GingerbreadManStealGoal::hasStealableInventory);
        if(list.isEmpty()){
            Player nearest = gingerbreadMan.level().getNearestPlayer(gingerbreadMan, 20.0D);
            if(nearest != null && hasStealableInventory(nearest)){
                return nearest;
            }
            return null;
        }else{
            return Util.getRandom(list, gingerbreadMan.getRandom());
        }
    }

    public static boolean hasStealableInventory(Entity entity) {
        if(!canStealFromEntityType(entity)){
            return false;
        }
        LazyOptional<IItemHandler> lazyOptional = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);
        if (lazyOptional.isPresent()) {
            IItemHandler handler = lazyOptional.orElse(null);
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i).is(ACTagRegistry.GINGERBREAD_MAN_STEALS)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean canStealFromEntityType(Entity entity){
        if(entity instanceof Player player && player.isCreative()){
            return false;
        }
        return !(entity instanceof GingerbreadManEntity);
    }

    public ItemStack stealOneFrom(Entity entity) {
        LazyOptional<IItemHandler> lazyOptional = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);
        if (lazyOptional.isPresent()) {
            IItemHandler handler = lazyOptional.orElse(null);
            List<Integer> validSlots = new ArrayList<>();
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i).is(ACTagRegistry.GINGERBREAD_MAN_STEALS)) {
                    validSlots.add(i);
                }
            }
            if (!validSlots.isEmpty()) {
                int slotId = Util.getRandom(validSlots, gingerbreadMan.getRandom());
                ItemStack stack = handler.getStackInSlot(slotId);
                ItemStack copy = stack.copy();
                copy.setCount(1);
                if (stack.is(ACTagRegistry.GINGERBREAD_MAN_STEALS)) {
                    stack.shrink(1);
                }
                return copy;
            }
        }
        return ItemStack.EMPTY;
    }
}

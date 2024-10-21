package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ConversionCrucibleBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.LicowitchEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.checkerframework.checker.units.qual.C;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public class LicowitchUseCrucibleGoal extends Goal {
    private BlockPos cruciblePos = null;
    private final LicowitchEntity mob;
    private int executionCooldown = 10;
    private int cookTime = 0;

    private ItemEntity tossedItem;

    private int tossItemCooldown = 0;
    private int checkReachCooldown = 0;


    public LicowitchUseCrucibleGoal(LicowitchEntity mob) {
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
                executionCooldown = 350 + mob.getRandom().nextInt(200);
                BlockPos pos = null;
                if (mob.getLastCruciblePos() != null) {
                    if (canWitchUseCrucibleAt(mob.level(), mob.getLastCruciblePos(), true)) {
                        executionCooldown = 10;
                        pos = mob.getLastCruciblePos();
                    } else {
                        mob.setLastCruciblePos(null);
                    }
                }
                if (pos == null) {
                    List<BlockPos> list = getNearbyCrucibles(mob.blockPosition(), (ServerLevel) mob.level(), 32).sorted(Comparator.comparingDouble(mob.blockPosition()::distSqr)).toList();
                    if (!list.isEmpty()) {
                        pos = list.get(0);
                    }
                }
                if (pos != null && mob.getRandom().nextInt(4) == 0) {
                    cruciblePos = pos;
                    mob.setLastCruciblePos(cruciblePos);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = mob.getTarget();
        return cruciblePos != null && canWitchUseCrucibleAt(mob.level(), cruciblePos, true) && (target == null || !target.isAlive()) && cookTime < 300;
    }

    private static Stream<BlockPos> getNearbyCrucibles(BlockPos blockpos, ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.CONVERSION_CRUCIBLE.getKey()), blockpos2 -> canWitchUseCrucibleAt(world, blockpos2, true), blockpos, range, PoiManager.Occupancy.ANY);
    }

    private static boolean canWitchUseCrucibleAt(Level world, BlockPos pos, boolean inUse) {
        if (world.getBlockEntity(pos) instanceof ConversionCrucibleBlockEntity crucible) {
            if (crucible.isWitchMode() && inUse) {
                return true;
            } else if (crucible.getConvertingToBiome() == null && crucible.getFilledLevel() == 0) {
                return !crucible.isWitchMode();
            }
        }
        return false;
    }

    @Override
    public void stop() {
        super.stop();
        cookTime = 0;
        tossItemCooldown = 0;
        checkReachCooldown = 0;
        mob.updateHeldItems = true;
        mob.updateFoldedArms = true;
        if (tossedItem != null) {
            tossedItem.discard();
            tossedItem = null;
        }
        if (mob.getTarget() == null) {
            Vec3 teleportVec = LandRandomPos.getPos(mob, 12, 8);
            if (teleportVec != null) {
                teleportVec = teleportVec.add(0, 1, 0);
                AABB aabb = mob.getBoundingBox().move(teleportVec.subtract(mob.position()));
                if(mob.level().isUnobstructed(mob, Shapes.create(aabb))){
                    mob.setTeleportingToPos(teleportVec);
                }
            }

        }
    }

    @Override
    public void start() {
        super.start();
        mob.updateHeldItems = false;
        mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        mob.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }

    public void tick() {
        if (tossItemCooldown > 0) {
            tossItemCooldown--;
        }
        if (checkReachCooldown > 0) {
            checkReachCooldown--;
        }
        if (checkReachCooldown == 0) {
            checkReachCooldown = 100 + mob.getRandom().nextInt(40);
            if (mob.canTeleport() && !mob.canReach(cruciblePos)) {
                mob.setTeleportingToPos(Vec3.atCenterOf(cruciblePos.relative(Util.getRandom(ACMath.HORIZONTAL_DIRECTIONS, mob.getRandom()))));
                return;
            }
        }
        Vec3 center = Vec3.atCenterOf(cruciblePos);
        double distance = Vec3.atBottomCenterOf(cruciblePos).subtract(mob.position()).horizontalDistance();
        if (distance < 8.0D) {
            mob.getLookControl().setLookAt(center.x, center.y + 1.0F - Math.sin(cookTime * 0.2F) * 0.2F, center.z, 10.0F, (float) this.mob.getMaxHeadXRot());
        }
        if (distance > 2.5D) {
            mob.getNavigation().moveTo(cruciblePos.getX() + 0.5F, cruciblePos.getY(), cruciblePos.getZ() + 0.5F, 1);
        } else {
            if (distance < 1.25D) {
                mob.getMoveControl().strafe(0.1F, 0F);
            } else {
                mob.getMoveControl().strafe(0F, 0F);
            }
            mob.setLastCruciblePos(cruciblePos);
            mob.getNavigation().stop();
            if (mob.level().getBlockEntity(cruciblePos) instanceof ConversionCrucibleBlockEntity crucibleBlock && hasLineOfSightCrucible()) {
                mob.updateHeldItems = false;
                boolean flag = crucibleBlock.isWitchMode();
                crucibleBlock.setWitchModeDuration(10);
                if (!flag && crucibleBlock.isWitchMode()) {
                    crucibleBlock.rerollWantedItem();
                    crucibleBlock.markUpdated();
                }
                if (mob.areArmsVisuallyCrossed(1.0F) && mob.getAnimation() == IAnimatedEntity.NO_ANIMATION && !crucibleBlock.getWantItem().isEmpty() && crucibleBlock.getItemDisplayProgress(1.0F) >= 1.0F) {
                    mob.setItemInHand(InteractionHand.MAIN_HAND, crucibleBlock.getWantItem().copy());
                }
                if (!crucibleBlock.getWantItem().isEmpty() && crucibleBlock.getItemDisplayProgress(1.0F) >= 1.0F && tossItemCooldown == 0) {
                    if (tossedItem != null) {
                        tossedItem.discard();
                    }
                    if (mob.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        mob.setAnimation(mob.getMainArm() == HumanoidArm.RIGHT ? LicowitchEntity.ANIMATION_SWING_RIGHT : LicowitchEntity.ANIMATION_SWING_LEFT);
                    } else if ((mob.getAnimation() == LicowitchEntity.ANIMATION_SWING_RIGHT || mob.getAnimation() == LicowitchEntity.ANIMATION_SWING_LEFT) && mob.getAnimationTick() == 4) {
                        mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        Vec3 from = mob.getSwingArmPosition();
                        ItemStack stack = crucibleBlock.getWantItem().copy();
                        tossedItem = new ItemEntity(mob.level(), from.x, from.y, from.z, stack);
                        Vec3 delta = center.subtract(from).normalize().scale(0.13F).add(0, 0.4F, 0);
                        tossedItem.setNeverPickUp();
                        tossedItem.setDeltaMovement(delta);
                        tossedItem.checkDespawn();
                        mob.level().addFreshEntity(tossedItem);
                        tossItemCooldown = 50;
                    }
                }
                cookTime++;
            }
        }
    }

    public boolean hasLineOfSightCrucible() {
        HitResult raytraceresult = mob.level().clip(new ClipContext(mob.getEyePosition(1.0F), new Vec3(cruciblePos.getX() + 0.5, cruciblePos.getY() + 0.5, cruciblePos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        if (raytraceresult instanceof BlockHitResult blockRayTraceResult) {
            BlockPos pos = blockRayTraceResult.getBlockPos();
            return pos.equals(cruciblePos) || mob.level().isEmptyBlock(pos) || this.mob.level().getBlockEntity(pos) == this.mob.level().getBlockEntity(cruciblePos);
        }
        return true;
    }

}
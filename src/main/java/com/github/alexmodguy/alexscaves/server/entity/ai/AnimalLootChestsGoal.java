package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.ChestThief;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class AnimalLootChestsGoal extends MoveToBlockGoal {
    private final Animal entity;
    private final ChestThief chestLooter;
    private boolean hasOpenedChest = false;

    public AnimalLootChestsGoal(Animal entity, int range) {
        super(entity, 1.0F, range, 6);
        this.entity = entity;
        this.chestLooter = (ChestThief) entity;
    }

    protected int nextStartTick(PathfinderMob mob) {
        return reducedTickDelay(100 + entity.getRandom().nextInt(100));
    }

    public boolean isChestRaidable(LevelReader world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof BaseEntityBlock) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof Container) {
                Container inventory = (Container) entity;
                try {
                    if (!inventory.isEmpty() && chestLooter.isLootable(inventory)) {
                        return true;
                    }
                } catch (Exception e) {
                    AlexsCaves.LOGGER.warn("Alex's Caves stopped a " + entity.getClass().getSimpleName() + " from causing a crash during access");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    protected BlockPos getMoveToTarget() {
        return this.blockPos;
    }

    protected void moveMobToBlock() {
        BlockPos pos = getMoveToTarget();
        this.mob.getNavigation().moveTo((double) ((float) pos.getX()) + 0.5D, (double) (pos.getY() + 1), (double) ((float) pos.getZ()) + 0.5D, this.speedModifier);
    }

    @Override
    public boolean canUse() {
        if (this.entity instanceof TamableAnimal && ((TamableAnimal) entity).isTame()) {
            return false;
        }
        if (!this.entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    public boolean hasLineOfSightChest() {
        HitResult raytraceresult = entity.level().clip(new ClipContext(entity.getEyePosition(1.0F), new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (raytraceresult instanceof BlockHitResult) {
            BlockHitResult blockRayTraceResult = (BlockHitResult) raytraceresult;
            BlockPos pos = blockRayTraceResult.getBlockPos();
            return pos.equals(blockPos) || entity.level().isEmptyBlock(pos) || this.entity.level().getBlockEntity(pos) == this.entity.level().getBlockEntity(blockPos);
        }
        return true;
    }

    public ItemStack getFoodFromInventory(Container inventory, RandomSource random) {
        List<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && chestLooter.shouldLootItem(stack)) {
                items.add(stack);
            }
        }
        if (items.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            return items.get(random.nextInt(items.size() - 1));
        }
    }

    public double acceptedDistance() {
        return Math.pow(entity.getBbWidth(), 2) + 3.0F;
    }

    public boolean shouldRecalculatePath() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        BlockEntity te = this.entity.level().getBlockEntity(this.blockPos);
        if (te instanceof Container) {
            Container feeder = (Container) te;
            double distance = this.entity.distanceToSqr(this.blockPos.getX() + 0.5F, this.blockPos.getY() + 0.5F, this.blockPos.getZ() + 0.5F);
            entity.getNavigation().moveTo(this.blockPos.getX() + 0.5F, this.blockPos.getY() - 1, this.blockPos.getZ() + 0.5F, 1.0F);
            if (hasLineOfSightChest()) {
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(blockPos));
                if (distance <= acceptedDistance()) {
                    entity.getNavigation().stop();
                    chestLooter.startOpeningChest();
                    if (!hasOpenedChest) {
                        hasOpenedChest = true;
                        toggleChest(feeder, true);
                    }
                    if (hasOpenedChest && chestLooter.didOpeningChest()) {
                        toggleChest(feeder, false);
                        ItemStack stack = getFoodFromInventory(feeder, this.entity.level().random);
                        if (stack == ItemStack.EMPTY) {
                            this.stop();
                        } else {
                            ItemStack duplicate = stack.copy();
                            duplicate.setCount(1);
                            if (!this.entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && !this.entity.level().isClientSide) {
                                this.entity.spawnAtLocation(this.entity.getItemInHand(InteractionHand.MAIN_HAND), 0.0F);
                            }
                            this.entity.setItemInHand(InteractionHand.MAIN_HAND, duplicate);
                            stack.shrink(1);
                            chestLooter.afterSteal(blockPos);
                            this.stop();
                        }
                    }
                }
            }
        }
    }


    public void stop() {
        super.stop();
        if (this.blockPos != null) {
            BlockEntity te = this.entity.level().getBlockEntity(this.blockPos);
            if (te instanceof Container) {
                toggleChest((Container) te, false);
            }
        }
        this.blockPos = BlockPos.ZERO;
        this.hasOpenedChest = false;
    }


    @Override
    protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
        return pos != null && isChestRaidable(worldIn, pos);
    }

    public void toggleChest(Container te, boolean open) {
        if (te instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) te;
            if (open) {
                this.entity.level().blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 1);
            } else {
                this.entity.level().blockEvent(this.blockPos, chest.getBlockState().getBlock(), 1, 0);
            }
            this.entity.level().updateNeighborsAt(blockPos, chest.getBlockState().getBlock());
            this.entity.level().updateNeighborsAt(blockPos.below(), chest.getBlockState().getBlock());
        }
    }
}

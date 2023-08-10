package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FertilizerItem extends Item {

    public FertilizerItem() {
        super(new Item.Properties());
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
        if (applyFertilizer(context.getItemInHand(), level, blockpos, context.getPlayer())) {
            if (!level.isClientSide) {
                level.levelEvent(1505, blockpos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            BlockState blockstate = level.getBlockState(blockpos);
            boolean flag = blockstate.isFaceSturdy(level, blockpos, context.getClickedFace());
            if (flag && BoneMealItem.growWaterPlant(context.getItemInHand(), level, blockpos1, context.getClickedFace())) {
                if (!level.isClientSide) {
                    level.levelEvent(1505, blockpos1, 0);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    private static boolean applyFertilizer(ItemStack itemStack, Level level, BlockPos blockPos, Player player) {
        BlockState blockstate = level.getBlockState(blockPos);
        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, level, blockPos, blockstate, itemStack);
        if (hook != 0) {
            return hook > 0;
        }
        if (blockstate.getBlock() instanceof BonemealableBlock) {
            BonemealableBlock bonemealableblock = (BonemealableBlock) blockstate.getBlock();
            if (bonemealableblock.isValidBonemealTarget(level, blockPos, blockstate, level.isClientSide)) {
                if (level instanceof ServerLevel) {
                    for (int boneMealAttempts = 0; boneMealAttempts < 4; boneMealAttempts++) {
                        bonemealableblock.performBonemeal((ServerLevel) level, level.random, blockPos, blockstate);
                        blockstate = level.getBlockState(blockPos);
                        if (!(blockstate.getBlock() instanceof BonemealableBlock)) {
                            return true;
                        }

                    }
                    itemStack.shrink(1);
                }
                return true;
            }
        }

        return false;
    }
}

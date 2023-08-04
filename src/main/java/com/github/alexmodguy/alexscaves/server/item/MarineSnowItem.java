package com.github.alexmodguy.alexscaves.server.item;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;

public class MarineSnowItem extends Item {

    private static Map<Block, Block> GROWS_INTERACTIONS;
    public MarineSnowItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        initGrowth();
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        if(GROWS_INTERACTIONS.containsKey(clickedState.getBlock()) && scanForWater(clickedState, context.getLevel(), context.getClickedPos())){
            if (!context.getLevel().isClientSide) {
                context.getLevel().levelEvent(1505, context.getClickedPos(), 0);
            }
            BlockState transform = GROWS_INTERACTIONS.getOrDefault(clickedState.getBlock(), Blocks.AIR).defaultBlockState();
            for (Property prop : clickedState.getProperties()) {
                transform = transform.hasProperty(prop) ? transform.setValue(prop, clickedState.getValue(prop)) : transform;
            }
            context.getLevel().setBlockAndUpdate(context.getClickedPos(), transform);
            if(!context.getPlayer().isCreative()){
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return InteractionResult.PASS;
    }

    private static boolean scanForWater(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        if (blockState.getFluidState().is(FluidTags.WATER)) {
            return true;
        } else {
            for(Direction direction : Direction.values()) {
                if (blockGetter.getFluidState(blockPos.relative(direction)).is(FluidTags.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void initGrowth() {
        if (GROWS_INTERACTIONS != null) {
            return;
        }
        GROWS_INTERACTIONS = Util.make(Maps.newHashMap(), (map) -> {
            map.put(Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_BLOCK);
            map.put(Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_BLOCK);
            map.put(Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_BLOCK);
            map.put(Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_BLOCK);
            map.put(Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_BLOCK);
            map.put(Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL);
            map.put(Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL);
            map.put(Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL);
            map.put(Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL);
            map.put(Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL);
            map.put(Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_FAN);
            map.put(Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_FAN);
            map.put(Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_FAN);
            map.put(Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_FAN);
            map.put(Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_FAN);
            map.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, Blocks.TUBE_CORAL_WALL_FAN);
            map.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, Blocks.BRAIN_CORAL_WALL_FAN);
            map.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN);
            map.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, Blocks.FIRE_CORAL_WALL_FAN);
            map.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, Blocks.HORN_CORAL_WALL_FAN);
        });
    }
}

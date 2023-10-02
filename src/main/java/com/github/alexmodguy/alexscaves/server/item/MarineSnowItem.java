package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MusselBlock;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class MarineSnowItem extends Item {

    private static Map<Block, Block> GROWS_INTERACTIONS;
    private static Map<Block, ItemStack> DUPLICATES_INTERACTIONS;
    public MarineSnowItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        initGrowth();
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        if(scanForWater(clickedState, context.getLevel(), context.getClickedPos())){
            boolean flag = false;
            if(GROWS_INTERACTIONS.containsKey(clickedState.getBlock())){
                flag = true;
                BlockState transform = GROWS_INTERACTIONS.getOrDefault(clickedState.getBlock(), Blocks.AIR).defaultBlockState();
                for (Property prop : clickedState.getProperties()) {
                    transform = transform.hasProperty(prop) ? transform.setValue(prop, clickedState.getValue(prop)) : transform;
                }
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), transform);
            }else if(DUPLICATES_INTERACTIONS.containsKey(clickedState.getBlock())){
                flag = true;
                if(context.getLevel().getRandom().nextInt(2) == 0){
                    ItemStack spawn = DUPLICATES_INTERACTIONS.getOrDefault(clickedState.getBlock(), ItemStack.EMPTY);
                    Vec3 spawnItemAt = context.getClickLocation();
                    ItemEntity itemEntity = new ItemEntity(context.getLevel(), spawnItemAt.x, spawnItemAt.y, spawnItemAt.z, spawn);
                    context.getLevel().addFreshEntity(itemEntity);
                }
            }else if(clickedState.is(ACBlockRegistry.MUSSEL.get()) && clickedState.getValue(MusselBlock.MUSSELS) < 5){
                flag = true;
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), clickedState.setValue(MusselBlock.MUSSELS, clickedState.getValue(MusselBlock.MUSSELS) + 1));
            }
            if(flag){
                if (!context.getLevel().isClientSide) {
                    context.getLevel().levelEvent(1505, context.getClickedPos(), 0);
                }
                if(!context.getPlayer().isCreative()){
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
            }
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
        if (GROWS_INTERACTIONS != null && DUPLICATES_INTERACTIONS != null) {
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
        DUPLICATES_INTERACTIONS = Util.make(Maps.newHashMap(), (map) -> {
            map.put(Blocks.TUBE_CORAL_BLOCK, new ItemStack(Blocks.TUBE_CORAL_BLOCK));
            map.put(Blocks.BRAIN_CORAL_BLOCK, new ItemStack(Blocks.BRAIN_CORAL_BLOCK));
            map.put(Blocks.BUBBLE_CORAL_BLOCK, new ItemStack(Blocks.BUBBLE_CORAL_BLOCK));
            map.put(Blocks.FIRE_CORAL_BLOCK, new ItemStack(Blocks.FIRE_CORAL_BLOCK));
            map.put(Blocks.HORN_CORAL_BLOCK, new ItemStack(Blocks.HORN_CORAL_BLOCK));
            map.put(Blocks.TUBE_CORAL, new ItemStack(Blocks.TUBE_CORAL));
            map.put(Blocks.BRAIN_CORAL, new ItemStack(Blocks.BRAIN_CORAL));
            map.put(Blocks.BUBBLE_CORAL, new ItemStack(Blocks.BUBBLE_CORAL));
            map.put(Blocks.FIRE_CORAL, new ItemStack(Blocks.FIRE_CORAL));
            map.put(Blocks.HORN_CORAL, new ItemStack(Blocks.HORN_CORAL));
            map.put(Blocks.TUBE_CORAL_FAN, new ItemStack(Blocks.TUBE_CORAL_FAN));
            map.put(Blocks.BRAIN_CORAL_FAN, new ItemStack(Blocks.BRAIN_CORAL_FAN));
            map.put(Blocks.BUBBLE_CORAL_FAN, new ItemStack(Blocks.BUBBLE_CORAL_FAN));
            map.put(Blocks.FIRE_CORAL_FAN, new ItemStack(Blocks.FIRE_CORAL_FAN));
            map.put(Blocks.HORN_CORAL_FAN, new ItemStack(Blocks.HORN_CORAL_FAN));
            map.put(Blocks.TUBE_CORAL_WALL_FAN, new ItemStack(Blocks.TUBE_CORAL_WALL_FAN));
            map.put(Blocks.BRAIN_CORAL_WALL_FAN, new ItemStack(Blocks.BRAIN_CORAL_WALL_FAN));
            map.put(Blocks.BUBBLE_CORAL_WALL_FAN, new ItemStack(Blocks.BUBBLE_CORAL_WALL_FAN));
            map.put(Blocks.FIRE_CORAL_WALL_FAN, new ItemStack(Blocks.FIRE_CORAL_WALL_FAN));
            map.put(Blocks.HORN_CORAL_WALL_FAN, new ItemStack(Blocks.HORN_CORAL_WALL_FAN));
            map.put(Blocks.SPONGE, new ItemStack(Blocks.WET_SPONGE));
            map.put(Blocks.WET_SPONGE, new ItemStack(Blocks.WET_SPONGE));
            map.put(ACBlockRegistry.DUSK_ANEMONE.get(), new ItemStack(ACBlockRegistry.DUSK_ANEMONE.get()));
            map.put(ACBlockRegistry.TWILIGHT_ANEMONE.get(), new ItemStack(ACBlockRegistry.TWILIGHT_ANEMONE.get()));
            map.put(ACBlockRegistry.MIDNIGHT_ANEMONE.get(), new ItemStack(ACBlockRegistry.MIDNIGHT_ANEMONE.get()));
        });
    }
}

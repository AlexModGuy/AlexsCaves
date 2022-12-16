package com.github.alexmodguy.alexscaves.server.block;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class SmoothLimestoneBlock extends Block {

    private static final List<RegistryObject<Block>> CAVE_PAINTINGS = Util.make(Lists.newArrayList(), (list) -> {
        list.add(ACBlockRegistry.CAVE_PAINTING_TREE_STARS);
        list.add(ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS);
        list.add(ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS_FRIEND);
        list.add(ACBlockRegistry.CAVE_PAINTING_DINO_NUGGETS);
        list.add(ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_CHEST);
        list.add(ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_FRIEND);
    });

    public SmoothLimestoneBlock(Properties properties) {
        super(properties);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (itemstack.is(Items.CHARCOAL)) {
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            if(!level.isClientSide){
                BlockState cavePainting = Util.getRandom(CAVE_PAINTINGS, player.getRandom()).get().defaultBlockState();
                level.setBlockAndUpdate(blockPos, cavePainting.setValue(CavePaintingBlock.FACING, blockHitResult.getDirection()));
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
            }
            return InteractionResult.SUCCESS;

        }

        return super.use(state, level, blockPos, player, interactionHand, blockHitResult);
    }

}

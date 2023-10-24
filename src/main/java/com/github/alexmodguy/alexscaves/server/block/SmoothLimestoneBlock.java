package com.github.alexmodguy.alexscaves.server.block;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
        list.add(ACBlockRegistry.CAVE_PAINTING_AMBERSOL);
        list.add(ACBlockRegistry.CAVE_PAINTING_DARK);
        list.add(ACBlockRegistry.CAVE_PAINTING_FOOTPRINT);
        list.add(ACBlockRegistry.CAVE_PAINTING_FOOTPRINTS);
        list.add(ACBlockRegistry.CAVE_PAINTING_TREE_STARS);
        list.add(ACBlockRegistry.CAVE_PAINTING_PEWEN);
        list.add(ACBlockRegistry.CAVE_PAINTING_TRILOCARIS);
        list.add(ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS);
        list.add(ACBlockRegistry.CAVE_PAINTING_GROTTOCERATOPS_FRIEND);
        list.add(ACBlockRegistry.CAVE_PAINTING_DINO_NUGGETS);
        list.add(ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_CHEST);
        list.add(ACBlockRegistry.CAVE_PAINTING_VALLUMRAPTOR_FRIEND);
        list.add(ACBlockRegistry.CAVE_PAINTING_RELICHEIRUS);
        list.add(ACBlockRegistry.CAVE_PAINTING_RELICHEIRUS_SLASH);
        list.add(ACBlockRegistry.CAVE_PAINTING_ENDERMAN);
        list.add(ACBlockRegistry.CAVE_PAINTING_PORTAL);
        list.add(ACBlockRegistry.CAVE_PAINTING_SUBTERRANODON);
        list.add(ACBlockRegistry.CAVE_PAINTING_SUBTERRANODON_RIDE);
        list.add(ACBlockRegistry.CAVE_PAINTING_TREMORSAURUS);
        list.add(ACBlockRegistry.CAVE_PAINTING_TREMORSAURUS_FRIEND);
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
            if (!level.isClientSide) {
                if(player instanceof ServerPlayer serverPlayer){
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, itemstack);
                }
                BlockState cavePainting = Util.getRandom(CAVE_PAINTINGS, player.getRandom()).get().defaultBlockState();
                level.setBlockAndUpdate(blockPos, cavePainting.setValue(CavePaintingBlock.FACING, blockHitResult.getDirection()));
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
            }
            return InteractionResult.SUCCESS;

        }

        return super.use(state, level, blockPos, player, interactionHand, blockHitResult);
    }

}

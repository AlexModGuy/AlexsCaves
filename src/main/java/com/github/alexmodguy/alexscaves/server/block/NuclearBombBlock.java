package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class NuclearBombBlock extends Block {
    public NuclearBombBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE).strength(8, 1001).sound(SoundType.METAL));
    }

    public void onCaughtFire(BlockState state, Level level, BlockPos blockPos, @Nullable net.minecraft.core.Direction face, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            NuclearBombEntity bomb = ACEntityRegistry.NUCLEAR_BOMB.get().create(level);
            bomb.setPos((double) blockPos.getX() + 0.5D, (double) blockPos.getY(), (double) blockPos.getZ() + 0.5D);
            level.addFreshEntity(bomb);
            level.playSound((Player) null, bomb.getX(), bomb.getY(), bomb.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(igniter, GameEvent.PRIME_FUSE, blockPos);
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos blockPos, BlockState blockState, boolean b) {
        if (!blockState.is(state.getBlock())) {
            if (level.hasNeighborSignal(blockPos)) {
                onCaughtFire(state, level, blockPos, null, null);
                level.removeBlock(blockPos, false);
            }

        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos blockPos, Block block, BlockPos blockPos1, boolean forced) {
        if (level.hasNeighborSignal(blockPos)) {
            onCaughtFire(state, level, blockPos, null, null);
            level.removeBlock(blockPos, false);
        }
    }

    public void onProjectileHit(Level level, BlockState state, BlockHitResult blockHitResult, Projectile projectile) {
        if (!level.isClientSide) {
            BlockPos blockpos = blockHitResult.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.mayInteract(level, blockpos)) {
                onCaughtFire(state, level, blockpos, null, entity instanceof LivingEntity ? (LivingEntity) entity : null);
                level.removeBlock(blockpos, false);
            }
        }

    }

    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!itemstack.is(Items.FLINT_AND_STEEL) && !itemstack.is(Items.FIRE_CHARGE)) {
            return super.use(state, level, blockPos, player, hand, result);
        } else {
            onCaughtFire(state, level, blockPos, result.getDirection(), player);
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
            Item item = itemstack.getItem();
            if (!player.isCreative()) {
                if (itemstack.is(Items.FLINT_AND_STEEL)) {
                    itemstack.hurtAndBreak(1, player, (p_57425_) -> {
                        p_57425_.broadcastBreakEvent(hand);
                    });
                } else {
                    itemstack.shrink(1);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(item));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

}

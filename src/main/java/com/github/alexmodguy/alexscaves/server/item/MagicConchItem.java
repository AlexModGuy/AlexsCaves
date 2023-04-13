package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class MagicConchItem extends Item {
    public MagicConchItem(Item.Properties properties) {
        super(properties);
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity player, int useTimeLeft) {
        int i = this.getUseDuration(stack) - useTimeLeft;
        if(i > 25){
            stack.hurtAndBreak(1, player, (player1) -> {
                player1.broadcastBreakEvent(player1.getUsedItemHand());
            });
            RandomSource randomSource = player.getRandom();
            if(!level.isClientSide){
                int maxNormal = 3 + randomSource.nextInt(1);
                int maxKnights = 2 + randomSource.nextInt(1);
                int maxMage = 1 + randomSource.nextInt(1);
                int normal = 0;
                int knights = 0;
                int mage = 0;
                int tries = 0;
                while(normal < maxNormal && tries < 99){
                    tries++;
                    if(summonDeepOne(ACEntityRegistry.DEEP_ONE.get(), player)){
                        normal++;
                    }
                }
                tries = 0;
                while(knights < maxKnights && tries < 99){
                    tries++;
                    if(summonDeepOne(ACEntityRegistry.DEEP_ONE_KNIGHT.get(), player)){
                        knights++;
                    }
                }
                tries = 0;
                while(mage < maxMage && tries < 99){
                    tries++;
                    if(summonDeepOne(ACEntityRegistry.DEEP_ONE_MAGE.get(), player)){
                        mage++;
                    }
                }
            }
            if(player instanceof Player realPlayer){
                realPlayer.awardStat(Stats.ITEM_USED.get(this));
                realPlayer.getCooldowns().addCooldown(this, 1200);
            }
        }

    }
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        level.playSound(player, player, SoundEvents.GOAT_HORN_PLAY, SoundSource.RECORDS, 16.0F, 1.0F);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
        return InteractionResultHolder.consume(itemstack);
    }

    public int getUseDuration(ItemStack stack) {
        return 1200;
    }

    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    private boolean summonDeepOne(EntityType type, LivingEntity summoner){
        RandomSource random = summoner.getRandom();
        BlockPos randomPos = summoner.blockPosition().offset(random.nextInt(20) - 10, 7, random.nextInt(20) - 10);
        while((summoner.level.getFluidState(randomPos).is(FluidTags.WATER) || summoner.level.isEmptyBlock(randomPos)) && randomPos.getY() > summoner.level.getMinBuildHeight()){
            randomPos = randomPos.below();
        }
        BlockState state = summoner.level.getBlockState(randomPos);
        if(!state.getFluidState().is(FluidTags.WATER) && !state.entityCanStandOn(summoner.level, randomPos, summoner)){
            return false;
        }
        Vec3 at = Vec3.atCenterOf(randomPos).add(0, 0.5, 0);
        Entity created = type.create(summoner.level);
        if(created instanceof DeepOneBaseEntity deepOne){
            float f = random.nextFloat() * 360;
            deepOne.moveTo(at.x, at.y, at.z, f, -60);
            deepOne.yBodyRot = f;
            deepOne.setYHeadRot(f);
            deepOne.setSummonedBy(summoner, 1200);
            deepOne.finalizeSpawn((ServerLevel)summoner.level, summoner.level.getCurrentDifficultyAt(BlockPos.containing(at)), MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
            if(deepOne.checkSpawnObstruction(summoner.level)){
                summoner.level.addFreshEntity(deepOne);
                deepOne.copyTarget(summoner);
                return true;
            }
        }
        return false;
    }
}

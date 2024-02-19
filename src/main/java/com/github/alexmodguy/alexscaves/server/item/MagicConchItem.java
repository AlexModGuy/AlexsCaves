package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.enchantment.ACEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
        level.playSound(null, player, ACSoundRegistry.MAGIC_CONCH_CAST.get(), SoundSource.RECORDS, 16.0F, 1.0F);
        int i = this.getUseDuration(stack) - useTimeLeft;
        boolean hurtRelations = false;
        if (i > 25) {
            if(stack.getEnchantmentLevel(ACEnchantmentRegistry.TAXING_BELLOW.get()) > 0){
                stack.setDamageValue(Math.min(0, stack.getDamageValue() - 1));
                hurtRelations = true;
            }else{
                stack.hurtAndBreak(1, player, (player1) -> {
                    player1.broadcastBreakEvent(player1.getUsedItemHand());
                });
            }
            RandomSource randomSource = player.getRandom();
            int time = 1200 + stack.getEnchantmentLevel(ACEnchantmentRegistry.LASTING_MORALE.get()) * 400;
            if (!level.isClientSide) {
                int chartingLevel = stack.getEnchantmentLevel(ACEnchantmentRegistry.CHARTING_CALL.get());
                DeepOneBaseEntity lastSummonedDeepOne = null;
                int maxNormal = 3 + randomSource.nextInt(1);
                int maxKnights = 2 + randomSource.nextInt(1);
                int maxMage = 1 + randomSource.nextInt(1);
                if(chartingLevel > 0){
                    maxNormal += randomSource.nextInt(Math.max(chartingLevel - 1, 2));
                    maxKnights += randomSource.nextInt(Math.max(chartingLevel - 2, 0));
                    maxMage += randomSource.nextInt(Math.max(chartingLevel - 3, 0));
                }
                int normal = 0;
                int knights = 0;
                int mage = 0;
                int tries = 0;
                while (normal < maxNormal && tries < 99) {
                    tries++;
                    DeepOneBaseEntity summoned = summonDeepOne(ACEntityRegistry.DEEP_ONE.get(), player, time);
                    if (summoned != null) {
                        normal++;
                        lastSummonedDeepOne = summoned;
                    }
                }
                tries = 0;
                while (knights < maxKnights && tries < 99) {
                    tries++;
                    DeepOneBaseEntity summoned = summonDeepOne(ACEntityRegistry.DEEP_ONE_KNIGHT.get(), player, time);
                    if (summoned != null) {
                        knights++;
                        lastSummonedDeepOne = summoned;
                    }
                }
                tries = 0;
                while (mage < maxMage && tries < 99) {
                    tries++;
                    DeepOneBaseEntity summoned = summonDeepOne(ACEntityRegistry.DEEP_ONE_MAGE.get(), player, time);
                    if (summoned != null) {
                        mage++;
                        lastSummonedDeepOne = summoned;
                    }
                }
                if(hurtRelations && lastSummonedDeepOne != null){
                    lastSummonedDeepOne.addReputation(player.getUUID(), -2);
                }
            }
            if (player instanceof Player realPlayer) {
                realPlayer.awardStat(Stats.ITEM_USED.get(this));
                realPlayer.getCooldowns().addCooldown(this, time);
            }
        }

    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    public int getUseDuration(ItemStack stack) {
        return 1200;
    }

    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    private DeepOneBaseEntity summonDeepOne(EntityType type, LivingEntity summoner, int time) {
        RandomSource random = summoner.getRandom();
        BlockPos randomPos = summoner.blockPosition().offset(random.nextInt(20) - 10, 7, random.nextInt(20) - 10);
        while ((summoner.level().getFluidState(randomPos).is(FluidTags.WATER) || summoner.level().isEmptyBlock(randomPos)) && randomPos.getY() > summoner.level().getMinBuildHeight()) {
            randomPos = randomPos.below();
        }
        BlockState state = summoner.level().getBlockState(randomPos);
        if (!state.getFluidState().is(FluidTags.WATER) && !state.entityCanStandOn(summoner.level(), randomPos, summoner)) {
            return null;
        }
        Vec3 at = Vec3.atCenterOf(randomPos).add(0, 0.5, 0);
        Entity created = type.create(summoner.level());
        if (created instanceof DeepOneBaseEntity deepOne) {
            float f = random.nextFloat() * 360;
            deepOne.moveTo(at.x, at.y, at.z, f, -60);
            deepOne.yBodyRot = f;
            deepOne.setYHeadRot(f);
            deepOne.setSummonedBy(summoner, time);
            deepOne.finalizeSpawn((ServerLevel) summoner.level(), summoner.level().getCurrentDifficultyAt(BlockPos.containing(at)), MobSpawnType.TRIGGERED, (SpawnGroupData) null, (CompoundTag) null);
            if (deepOne.checkSpawnObstruction(summoner.level())) {
                summoner.level().addFreshEntity(deepOne);
                deepOne.copyTarget(summoner);
                return deepOne;
            }
        }
        return null;
    }
}

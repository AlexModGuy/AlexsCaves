package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.item.tooltip.SackOfSatingTooltip;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.checkerframework.checker.units.qual.C;

import java.util.Optional;

public class SackOfSatingItem extends Item {

    public SackOfSatingItem() {
        super(new Item.Properties().stacksTo(1).rarity(ACItemRegistry.RARITY_SWEET));
    }

    public static int getHunger(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTag();
        return compoundtag != null ? compoundtag.getInt("HungerValue") : 0;
    }

    public static boolean isChewing(ItemStack itemStack, long gameTimeIn) {
        CompoundTag compoundtag = itemStack.getTag();
        return compoundtag != null && compoundtag.contains("ChewTimestamp") && gameTimeIn - compoundtag.getLong("ChewTimestamp") < 30;
    }

    public static boolean isExploding(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTag();
        return compoundtag != null && compoundtag.getBoolean("Exploding");
    }

    public static long getFeedTimestamp(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTag();
        return compoundtag != null && compoundtag.contains("FeedTimestamp") ?  compoundtag.getLong("FeedTimestamp") : -1;
    }

    public static void setHunger(ItemStack stack, int hunger) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("HungerValue", hunger);
    }

    public static void setChewTimestamp(ItemStack stack, long timestamp) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("ChewTimestamp", timestamp);
    }

    public static void setFeedTimestamp(ItemStack stack, long timestamp) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("FeedTimestamp", timestamp);
    }

    public static void setExploding(ItemStack stack, boolean exploding) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("Exploding", exploding);
    }


    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new SackOfSatingTooltip(stack));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack sackStack, ItemStack foodStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction != ClickAction.SECONDARY || sackStack.getTag() == null || foodStack.is(ACTagRegistry.RESTRICTED_FROM_SACK_OF_SATING)) {
            return false;
        } else {
            if (!foodStack.isEmpty() && foodStack.getItem().isEdible()) {
                if(foodStack.is(ACTagRegistry.EXPLODES_SACK_OF_SATING)){
                    setExploding(sackStack, true);
                }
                int wholeHunger = calculateWholeStackHungerValue(foodStack, player);
                setHunger(sackStack, getHunger(sackStack) + wholeHunger);
                if(foodStack.getItem() instanceof BowlFoodItem){
                    ItemStack bowlStack = new ItemStack(Items.BOWL, foodStack.getCount());
                    if(!player.addItem(bowlStack)){
                        player.drop(bowlStack, false);
                    }
                }
                if(foodStack.getItem() instanceof HoneyBottleItem || foodStack.getItem() instanceof DrinkableBottledItem){
                    ItemStack bowlStack = new ItemStack(Items.GLASS_BOTTLE, foodStack.getCount());
                    if(!player.addItem(bowlStack)){
                        player.drop(bowlStack, false);
                    }
                }
                foodStack.setCount(0);
                setChewTimestamp(sackStack, player.level().getGameTime());
                return true;
            }
            return false;
        }
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean held) {
        super.inventoryTick(stack, level, entity, i, held);
        if(stack.getTag() == null){
            stack.setTag(new CompoundTag());
        }
        int hungerValue = getHunger(stack);
        long timestamp = getFeedTimestamp(stack);
        if(!level.isClientSide && hungerValue > 0 && entity instanceof Player player && !player.getAbilities().invulnerable && player.tickCount % 100 == 0 && player.canEat(false) && (timestamp == -1 || player.level().getGameTime() - timestamp > 40)){
            player.getFoodData().eat(1, 0.05F);
            setHunger(stack, hungerValue - 1);
            setFeedTimestamp(stack, player.level().getGameTime());
            level.gameEvent(player, GameEvent.EAT, player.blockPosition());
            level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
        if(isChewing(stack, level.getGameTime()) && entity.tickCount % 6 == 0){
            level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.3F + 1.3F);
        }
        if(isExploding(stack)){
            if(!level.isClientSide){
                NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(level);
                explosion.setPos(entity.position().add(0, 4, 0));
                explosion.setSize(0.5F);
                explosion.setIntentionalGameDesign(true);
                level.addFreshEntity(explosion);
                setExploding(stack, false);
                stack.shrink(1);
            }
        }
    }

     public static int calculateWholeStackHungerValue(ItemStack foodStack, LivingEntity eater){
        FoodProperties foodProperties = foodStack.getFoodProperties(eater);
        if(foodProperties != null && !foodStack.is(ACTagRegistry.RESTRICTED_FROM_SACK_OF_SATING)){
            return foodProperties.getNutrition() * foodStack.getCount();
        }
        return 0;
    }

}

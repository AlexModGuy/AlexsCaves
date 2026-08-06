package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ModFishBucketItem extends MobBucketItem {

    private final Supplier<? extends EntityType<?>> fishTypeSupplier;

    public ModFishBucketItem(Supplier<? extends EntityType<?>> fishTypeIn, Supplier<? extends Fluid> fluid, Item.Properties builder) {
        super(fishTypeIn.get(), fluid.get(), SoundEvents.BUCKET_EMPTY_FISH, builder.stacksTo(1));
        this.fishTypeSupplier = fishTypeIn;
    }

    public EntityType<?> getFishType() {
        return this.fishTypeSupplier.get();
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        EntityType<?> fishType = getFishType();
    }

    @Override
    public void checkExtraContent(@Nullable Player player, Level level, ItemStack stack, BlockPos pos) {
        if (level instanceof ServerLevel) {
            this.spawnFish((ServerLevel) level, stack, pos);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
        }
    }

    private void spawnFish(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
        Entity entity = getFishType().spawn(serverLevel, stack, (Player) null, pos, MobSpawnType.BUCKET, true, false);
        if (entity instanceof Bucketable) {
            Bucketable bucketable = (Bucketable) entity;
            bucketable.loadFromBucketTag(stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag());
            bucketable.setFromBucket(true);
        }
        addExtraAttributes(entity, stack);
    }

    protected void addExtraAttributes(Entity entity, ItemStack stack) {

    }


}

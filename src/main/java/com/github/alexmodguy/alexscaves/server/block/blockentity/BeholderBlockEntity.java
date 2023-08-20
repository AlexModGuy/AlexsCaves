package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BeholderBlockEntity extends BlockEntity  {

    private int prevUsingEntityId = -1;
    private int currentlyUsingEntityId = -1;
    private float eyeYRot;
    private float prevEyeYRot;
    private float eyeXRot;
    private float prevEyeXRot;

    public int age;

    public BeholderBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.BEHOLDER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, BeholderBlockEntity entity) {
        entity.prevEyeXRot = entity.eyeXRot;
        entity.prevEyeYRot = entity.eyeYRot;
        entity.age++;
        Entity currentlyUsing = entity.getUsingEntity();
        if(currentlyUsing == null){
            entity.eyeXRot = Mth.approach(entity.eyeXRot, 0, 10F);
            entity.eyeYRot = entity.eyeYRot + 1F;
        }else{
            entity.eyeXRot = Mth.approach(entity.eyeXRot, currentlyUsing.getXRot(), 10F);
            entity.eyeYRot = Mth.approach(entity.eyeYRot, currentlyUsing.getYRot(), 10F);
        }
        if(!level.isClientSide){
            if(entity.prevUsingEntityId != entity.currentlyUsingEntityId){
                level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 2);
                entity.prevUsingEntityId = entity.currentlyUsingEntityId;
            }
        }
    }

    public float getEyeXRot(float partialTicks) {
        return  prevEyeXRot + (eyeXRot - prevEyeXRot) * partialTicks;
    }


    public Entity getUsingEntity() {
        return currentlyUsingEntityId == -1 ? null : level.getEntity(currentlyUsingEntityId);
    }


    public float getEyeYRot(float partialTicks) {
        return  prevEyeYRot + (eyeYRot - prevEyeYRot) * partialTicks;
    }

    public void startObserving(Level level, Player player) {
        BeholderEyeEntity beholderEyeEntity = ACEntityRegistry.BEHOLDER_EYE.get().create(level);
        double dist = Math.sqrt(this.getBlockPos().distSqr(player.blockPosition()));
        if(dist > 1000){
            ACAdvancementTriggerRegistry.BEHOLDER_FAR_AWAY.triggerForEntity(player);
        }
        Vec3 vec = this.getBlockPos().getCenter().add(0, -0.15, 0);
        beholderEyeEntity.setPos(vec);
        beholderEyeEntity.setUsingPlayerUUID(player.getUUID());
        beholderEyeEntity.setYRot(player.getYRot());
        level.addFreshEntity(beholderEyeEntity);
        this.currentlyUsingEntityId = beholderEyeEntity.getId();
        player.displayClientMessage(Component.translatable("item.alexscaves.occult_gem.start_observing"), true);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.currentlyUsingEntityId = tag.getInt("UsingEntityID");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("UsingEntityID", this.currentlyUsingEntityId);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            this.currentlyUsingEntityId = packet.getTag().getInt("UsingEntityID");
        }
    }

    public boolean isFirstPersonView(Entity cameraEntity) {
        return cameraEntity != null && cameraEntity instanceof BeholderEyeEntity && cameraEntity.blockPosition().equals(this.getBlockPos());
    }
}

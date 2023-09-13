package com.github.alexmodguy.alexscaves.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class CommonProxy {

    public void commonInit() {
    }

    public void clientInit() {
    }

    public void blockRenderingEntity(UUID id) {
    }

    public void releaseRenderingEntity(UUID id) {
    }

    public void setVisualFlag(int flag) {
    }

    public Player getClientSidePlayer() {
        return null;
    }

    public Vec3 getCameraRotation() {
        return Vec3.ZERO;
    }

    public boolean isKeyDown(int keyType) {
        return false;
    }

    public boolean checkIfParticleAt(SimpleParticleType simpleParticleType, BlockPos at) {
        return false;
    }

    public float getPartialTicks() {
        return 1.0F;
    }

    public Object getISTERProperties() {
        return null;
    }

    public Object getArmorProperties() {
        return null;
    }

    public void setSpelunkeryTutorialComplete(boolean completedTutorial) {
    }

    public boolean isSpelunkeryTutorialComplete() {
        return true;
    }

    public void setRenderViewEntity(Player player, Entity entity) {
    }

    public void resetRenderViewEntity(Player player) {
    }

    public int getPlayerTime() {
        return 0;
    }

    public void playWorldSound(@Nullable Object soundEmitter, byte type) {

    }

    public Vec3 getDarknessTrailPosFor(LivingEntity living, int pointer, float partialTick) {
        return living.position();
    }

    public float getPossessionStrengthAmount(float partialTick) {
        return 0.0F;
    }

    public boolean isFirstPersonPlayer(Entity entity) {
        return false;
    }

    public void openBookGUI(ItemStack itemStackIn) {
    }

    public void clearSoundCacheFor(Entity entity) {

    }

    public void clearSoundCacheFor(BlockEntity entity) {

    }
}
package com.github.alexmodguy.alexscaves.server;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.misc.ACLoadedMods;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.PathfindingConstants;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

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

    public float getPrimordialBossActiveAmount(float partialTicks) {
        return 0.0F;
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

    public void playWorldEvent(int messageId, Level level, BlockPos blockPos) {
    }

    public void setPrimordialBossActive(Level level, int id, boolean active) {
        ACWorldData worldData = ACWorldData.get(level);
        if (worldData != null) {
            worldData.trackPrimordialBoss(id, active);
        }
    }

    public boolean isPrimordialBossActive(Level level) {
        ACWorldData worldData = ACWorldData.get(level);
        if (worldData != null) {
            return worldData.isPrimordialBossActive(level);
        } else {
            return false;
        }
    }

    public void initPathfinding() {
        //PathfindingConstants.isDebugMode = true;
        PathfindingConstants.pathfindingThreads = Math.max(PathfindingConstants.pathfindingThreads, AlexsCaves.COMMON_CONFIG.pathfindingThreads.get());
    }

    public void removeBossBarRender(UUID bossBar) {
    }

    public void setBossBarRender(UUID bossBar, int renderType) {
    }

    public boolean isTickRateModificationActive(Level level){
        return ServerTickRateTracker.getForServer(level.getServer()).getServerTickLengthMs() != 50;
    }

    public boolean isFarFromCamera(double x, double y, double z) {
        return false;
    }

    public void renderVanillaMapDecoration(MapDecoration mapDecoration, int index) {
    }
}
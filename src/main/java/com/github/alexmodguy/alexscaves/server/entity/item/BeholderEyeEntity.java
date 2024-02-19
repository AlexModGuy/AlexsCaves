package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import com.github.alexmodguy.alexscaves.server.message.BeholderSyncMessage;
import com.github.alexmodguy.alexscaves.server.message.PossessionKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.BitSet;
import java.util.Optional;
import java.util.UUID;

public class BeholderEyeEntity extends Entity implements PossessesCamera {

    private static final int LOAD_CHUNK_DISTANCE = 2;
    private static final EntityDataAccessor<Optional<UUID>> USING_PLAYER_ID = SynchedEntityData.defineId(BeholderEyeEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(BeholderEyeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> X_ROT = SynchedEntityData.defineId(BeholderEyeEntity.class, EntityDataSerializers.FLOAT);

    private boolean loadingChunks = false;
    private boolean stopPossession;
    private boolean prevStopPossession = true;

    private float prevEyeXRot;
    private float prevEyeYRot;

    public boolean hasTakenFullControlOfCamera;

    public BeholderEyeEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public BeholderEyeEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.BEHOLDER_EYE.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(USING_PLAYER_ID, Optional.empty());
        this.getEntityData().define(X_ROT, 0F);
        this.getEntityData().define(Y_ROT, 0F);
    }

    public void tick() {
        super.tick();
        Entity usingPlayer = getUsingPlayer();
        if (usingPlayer == null) {
            if (!level().isClientSide && loadingChunks) {
                loadingChunks = false;
                loadChunksAround(false);
            }
            if (this.tickCount > 3) {
                this.discard();
            }
        } else {
            if (!level().isClientSide && hasTakenFullControlOfCamera && !loadingChunks) {
                loadingChunks = true;
                loadChunksAround(true);
            }
            if (usingPlayer instanceof LivingEntity living) {
                living.zza = 0;
                living.yya = 0;
                living.xxa = 0;
            }
            if (level().isClientSide) {
                Player clientSidePlayer = AlexsCaves.PROXY.getClientSidePlayer();
                if (usingPlayer == clientSidePlayer) {
                    if (AlexsCaves.PROXY.isKeyDown(4)) {
                        AlexsCaves.sendMSGToServer(new PossessionKeyMessage(this.getId(), usingPlayer.getId(), 0));
                    }
                }

            } else {
                if (prevStopPossession != stopPossession) {
                    handleCameraServerSide(usingPlayer, !stopPossession);
                    prevStopPossession = stopPossession;
                }
                if (stopPossession) {
                    this.setUsingPlayerUUID(null);
                }
                if (usingPlayer.isShiftKeyDown()) {
                    stopPossession = true;
                }
            }
        }
    }

    public Vec3 getDeltaMovement() {
        return Vec3.ZERO;
    }

    public void setDeltaMovement(Vec3 vec3) {
    }

    public void handleCameraServerSide(Entity usingPlayer, boolean turnOn) {
        if (usingPlayer.level().equals(this.level())) {
            AlexsCaves.sendMSGToAll(new BeholderSyncMessage(this.getId(), turnOn));
            if (turnOn) {
                this.level().broadcastEntityEvent(this, (byte) 77);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 78);
            }
            if(turnOn){
                hasTakenFullControlOfCamera = true;
            }
        } else {
            this.discard();
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        this.level().broadcastEntityEvent(this, (byte) 78);
        super.remove(removalReason);
    }

    public void handleEntityEvent(byte b) {
        if (b == 77 || b == 78) {
            Entity usingPlayer = getUsingPlayer();
            if (usingPlayer instanceof Player player) {
                if (b == 77) {
                    player.playSound(ACSoundRegistry.BEHOLDER_ENTER.get());
                    AlexsCaves.PROXY.setRenderViewEntity(player, this);
                } else {
                    player.playSound(ACSoundRegistry.BEHOLDER_EXIT.get());
                    AlexsCaves.PROXY.resetRenderViewEntity(player);
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("UsingPlayerUUID")) {
            this.setUsingPlayerUUID(tag.getUUID("UsingPlayerUUID"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid1 = getUsingPlayerUUID();
        if (uuid1 != null) {
            tag.putUUID("UsingPlayerUUID", uuid1);
        }
    }

    public void setUsingPlayerUUID(UUID uuid) {
        this.entityData.set(USING_PLAYER_ID, Optional.ofNullable(uuid));
    }

    public UUID getUsingPlayerUUID() {
        return this.entityData.get(USING_PLAYER_ID).orElse(null);
    }

    public Entity getUsingPlayer() {
        UUID id = getUsingPlayerUUID();
        if (id == null) {
            return null;
        } else {
            if (level().isClientSide) {
                return level().getPlayerByUUID(id);
            } else {
                return level().getServer().getPlayerList().getPlayer(id);
            }
        }
    }

    public float getYRot() {
        return this.entityData.get(Y_ROT);
    }

    public float getXRot() {
        return this.entityData.get(X_ROT);
    }

    public float getViewXRot(float f) {
        return f == 1.0F ? this.getXRot() : Mth.lerp(f, this.prevEyeXRot, this.getXRot());
    }

    public float getViewYRot(float f) {
        return f == 1.0F ? this.getYRot() : Mth.lerp(f, this.prevEyeYRot, this.getYRot());
    }

    public void setEyeYRot(float f) {
        this.entityData.set(Y_ROT, f);
    }

    public void setEyeXRot(float f) {
        this.entityData.set(X_ROT, f);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void onPossessionKeyPacket(Entity keyPresser, int type) {
        Entity possessed = this.getUsingPlayer();
        if (possessed != null && possessed.equals(keyPresser)) {
            stopPossession = true;
        }
    }

    private void loadChunksAround(boolean load) {
        if (this.level() instanceof ServerLevel serverLevel) {
            if(this.getUsingPlayer() instanceof ServerPlayer serverPlayer){
                ChunkPos playerChunkPos = new ChunkPos(serverPlayer.blockPosition());
                ForgeChunkManager.forceChunk(serverLevel, AlexsCaves.MODID, this, playerChunkPos.x, playerChunkPos.z, load, load);
            }
            ChunkPos chunkPos = new ChunkPos(this.blockPosition());
            int dist = Math.max(LOAD_CHUNK_DISTANCE, serverLevel.getServer().getPlayerList().getViewDistance() / 2);
            for (int i = -dist; i <= dist; i++) {
                for (int j = -dist; j <= dist; j++) {
                    ForgeChunkManager.forceChunk(serverLevel, AlexsCaves.MODID, this, chunkPos.x + i, chunkPos.z + j, load, load);
                    if (load && this.getUsingPlayer() instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.send(new ClientboundLevelChunkWithLightPacket(level().getChunk(chunkPos.x + i, chunkPos.z + j), level().getLightEngine(), (BitSet)null, (BitSet)null));
                    }
                }
            }
        }
    }

    @Override
    public float getPossessionStrength(float partialTicks) {
        float age = tickCount + partialTicks;
        if (age > 10) {
            return 0.0F;
        } else {
            float j = age / 10F;
            return 1F - j;
        }
    }

    @Override
    public boolean instant() {
        return true;
    }

    @Override
    public boolean isPossessionBreakable() {
        return true;
    }

    public void setOldRots() {
        prevEyeYRot = getYRot();
        prevEyeXRot = getXRot();
    }
}

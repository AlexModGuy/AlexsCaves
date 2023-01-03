package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.Stack;

public class NuclearExplosionEntity extends Entity {

    private boolean spawnedParticle = false;
    private Stack<BlockPos> destroyingChunks = new Stack<>();
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(NuclearExplosionEntity.class, EntityDataSerializers.FLOAT);

    public NuclearExplosionEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public NuclearExplosionEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.NUCLEAR_EXPLOSION.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        int chunksAffected = (int) Math.ceil(this.getSize());
        int radius = chunksAffected * 15;
        if (!spawnedParticle) {
            spawnedParticle = true;
            int particleY = (int)Math.ceil(this.getY());
            while(particleY > level.getMinBuildHeight() && particleY > this.getY() - radius / 2F && isDestroyable(level.getBlockState(new BlockPos(this.getX(), particleY, this.getZ())))){
                particleY--;
            }
            level.addAlwaysVisibleParticle(ACParticleRegistry.MUSHROOM_CLOUD.get(), true, this.getX(), particleY + 1, this.getZ(), this.getSize() + 0.2F, 0, 0);
        }
        if (tickCount > 40 && destroyingChunks.isEmpty()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            if (!level.isClientSide) {
                if (destroyingChunks.isEmpty()) {
                    BlockPos center = this.blockPosition();
                    int chunks = chunksAffected;
                    for (int i = -chunks; i <= chunks; i++) {
                        for (int j = -chunks; j <= chunks; j++) {
                            for (int k = -chunks; k <= chunks; k++) {
                                destroyingChunks.push(center.offset(i * 16, j * 16, k * 16));
                            }
                        }
                    }
                    destroyingChunks.sort((blockPos1, blockPos2) -> Double.compare(blockPos2.distManhattan(this.blockPosition()), blockPos1.distManhattan(this.blockPosition())));
                } else {
                    int tickChunkCount = Math.min(destroyingChunks.size(), 3);
                    for (int i = 0; i < tickChunkCount; i++) {
                        removeChunk(radius);
                    }
                }
            }
            AABB killBox = this.getBoundingBox().inflate(radius, radius * 0.6, radius);
            for (LivingEntity entity : this.level.getEntitiesOfClass(LivingEntity.class, killBox)) {
                float dist = entity.distanceTo(this);
                float dist2 = dist > 100 ? 5 : 105 - dist;
                entity.setDeltaMovement(entity.position().subtract(this.position()).normalize().scale(dist2 * 0.1F));
                entity.hurt(ACDamageTypes.NUKE, 1.5F * getSize() * dist2);
                entity.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 48000, 2, false, false, true));
            }
        }
    }

    private void removeChunk(int radius) {
        BlockPos chunkCorner = destroyingChunks.pop();
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(chunkCorner);
        carveBelow.set(chunkCorner);
        float itemDropModifier = 0.025F / Math.min(1, this.getSize());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 15; y >= 0; y--) {
                    carve.set(chunkCorner.getX() + x, Mth.clamp(chunkCorner.getY() + y, level.getMinBuildHeight(), level.getMaxBuildHeight()), chunkCorner.getZ() + z);
                    float widthSimplexNoise1 = (ACMath.sampleNoise3D(carve.getX(), carve.getY(), carve.getZ(), radius) - 0.5F) * 0.45F + 0.55F;
                    double yDist = ACMath.smin(0.6F - Math.abs(this.blockPosition().getY() - carve.getY()) / (float) radius, 0.6F, 0.2F);
                    double distToCenter = carve.distToLowCornerSqr(this.blockPosition().getX(), carve.getY() - 1, this.blockPosition().getZ());
                    double targetRadius = yDist * (radius + widthSimplexNoise1 * radius) * radius;
                    if (distToCenter <= targetRadius) {
                        BlockState state = level.getBlockState(carve);
                        if ((!state.isAir() || !state.getFluidState().isEmpty()) && isDestroyable(state)) {
                            carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                            if (random.nextFloat() < itemDropModifier && state.getFluidState().isEmpty()) {
                                level.destroyBlock(carve, true);
                            } else {
                                level.setBlockAndUpdate(carve, Blocks.AIR.defaultBlockState());
                            }
                        }
                    }
                }
                if(random.nextFloat() < 0.15 && !level.getBlockState(carveBelow).isAir()){
                    level.setBlockAndUpdate(carveBelow.above(), Blocks.FIRE.defaultBlockState());
                }
            }
        }
    }

    private boolean isDestroyable(BlockState state) {
        return !state.is(ACTagRegistry.UNMOVEABLE) && state.getBlock().getExplosionResistance() < 1000;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SIZE, 1.0F);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(float f) {
        this.entityData.set(SIZE, f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}

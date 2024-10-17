package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.TremorzillaEggBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.List;
import java.util.Stack;

public class NuclearExplosionEntity extends Entity {

    private boolean spawnedParticle = false;
    private Stack<BlockPos> destroyingChunks = new Stack<>();
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(NuclearExplosionEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> NO_GRIEFING = SynchedEntityData.defineId(NuclearExplosionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> INTENTIONAL_GAME_DESIGN = SynchedEntityData.defineId(NuclearExplosionEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean loadingChunks = false;

    private Explosion dummyExplosion;

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
        int chunksAffected = getChunksAffected();
        int radius = chunksAffected * 15;
        if (!spawnedParticle) {
            spawnedParticle = true;
            int particleY = (int) Math.ceil(this.getY());
            while (particleY > level().getMinBuildHeight() && particleY > this.getY() - radius / 2F && isDestroyable(level().getBlockState(BlockPos.containing(this.getX(), particleY, this.getZ())))) {
                particleY--;
            }
            level().addAlwaysVisibleParticle(ACParticleRegistry.MUSHROOM_CLOUD.get(), true, this.getX(), particleY + 2, this.getZ(), this.getSize(), isIntentionalGameDesign() ? 1.0F : 0.0F, 0);
        }
        if (tickCount > 40 && destroyingChunks.isEmpty()) {
            this.remove(RemovalReason.DISCARDED);
        } else {
            if (!level().isClientSide && !isNoGriefing()) {
                if (!loadingChunks && !this.isRemoved()) {
                    loadingChunks = true;
                    loadChunksAround(true);
                }
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
            AABB killBox = this.getBoundingBox().inflate(radius + radius * 0.5F, radius * 0.6, radius + radius * 0.5F);
            float flingStrength = getSize() * 0.33F;
            float maximumDistance = radius + radius * 0.5F + 1;
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, killBox)) {
                float dist = entity.distanceTo(this);
                float damage = calculateDamage(dist, maximumDistance);
                Vec3 vec3 = entity.position().subtract(this.position()).add(0, 0.3, 0).normalize();
                float playerFling = entity instanceof Player ? 0.5F * flingStrength : flingStrength;
                if (damage > 0) {
                    if (entity instanceof RaycatEntity) {
                        damage = 0;
                    } else if (entity.getType().is(ACTagRegistry.RESISTS_RADIATION)) {
                        damage *= 0.25F;
                        playerFling *= 0.1F;
                        if(entity instanceof TremorzillaEntity){
                            playerFling = 0;
                            damage = 0;
                        }
                    }
                    if(damage > 0){
                        entity.hurt(isIntentionalGameDesign() ? ACDamageTypes.causeIntentionalGameDesign(level().registryAccess()) : ACDamageTypes.causeNukeDamage(level().registryAccess()), damage);
                    }
                }
                entity.setDeltaMovement(vec3.scale(damage * 0.1F * playerFling));
                if(!this.isIntentionalGameDesign()){
                    entity.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 48000, getSize() <= 1.5F ? 1 : 2, false, false, true));
                }
            }
        }
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (!level().isClientSide && loadingChunks) {
            loadingChunks = false;
            loadChunksAround(false);
        }
        super.remove(removalReason);
    }


    private int getChunksAffected() {
        return (int) Math.ceil(this.getSize());
    }

    private void loadChunksAround(boolean load) {
        if (this.level() instanceof ServerLevel serverLevel) {
            ChunkPos chunkPos = new ChunkPos(this.blockPosition());
            int dist = Math.max(getChunksAffected(), serverLevel.getServer().getPlayerList().getViewDistance() / 2);
            for (int i = -dist; i <= dist; i++) {
                for (int j = -dist; j <= dist; j++) {
                    ForgeChunkManager.forceChunk(serverLevel, AlexsCaves.MODID, this, chunkPos.x + i, chunkPos.z + j, load, load);
                }
            }
        }
    }

    private float calculateDamage(float dist, float max) {
        float revert = (max - dist) / max;
        float baseDmg = this.getSize() <= 1.5F ? 100 : 100 + (this.getSize() - 1.5F) * 400;
        return revert * baseDmg;
    }

    private void removeChunk(int radius) {
        BlockPos chunkCorner = destroyingChunks.pop();
        BlockPos.MutableBlockPos carve = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos carveBelow = new BlockPos.MutableBlockPos();
        carve.set(chunkCorner);
        carveBelow.set(chunkCorner);
        float itemDropModifier = 0.025F / Math.min(1, this.getSize());
        if (AlexsCaves.COMMON_CONFIG.nukeMaxBlockExplosionResistance.get() <= 0) {
            return;
        }
        if (dummyExplosion == null) {
            dummyExplosion = new Explosion(level(), null, this.getX(), this.getY(), this.getZ(), 10.0F, List.of());
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 15; y >= 0; y--) {
                    boolean canSetToFire = false;
                    carve.set(chunkCorner.getX() + x, Mth.clamp(chunkCorner.getY() + y, level().getMinBuildHeight(), level().getMaxBuildHeight()), chunkCorner.getZ() + z);
                    float widthSimplexNoise1 = (ACMath.sampleNoise3D(carve.getX(), carve.getY(), carve.getZ(), radius) - 0.5F) * 0.45F + 0.55F;
                    double yDist = ACMath.smin(0.6F - Math.abs(this.blockPosition().getY() - carve.getY()) / (float) radius, 0.6F, 0.2F);
                    double distToCenter = carve.distToLowCornerSqr(this.blockPosition().getX(), carve.getY() - 1, this.blockPosition().getZ());
                    double targetRadius = yDist * (radius + widthSimplexNoise1 * radius) * radius;
                    if (distToCenter <= targetRadius) {
                        BlockState state = level().getBlockState(carve);
                        if ((!state.isAir() || !state.getFluidState().isEmpty()) && isDestroyable(state)) {
                            carveBelow.set(carve.getX(), carve.getY() - 1, carve.getZ());
                            canSetToFire = true;
                            if(state.is(ACBlockRegistry.TREMORZILLA_EGG.get()) && state.getBlock() instanceof TremorzillaEggBlock tremorzillaEggBlock){
                                tremorzillaEggBlock.spawnDinosaurs(level(), carve, state);
                            }else if (AlexsCaves.COMMON_CONFIG.nukesSpawnItemDrops.get() && random.nextFloat() < itemDropModifier && state.getFluidState().isEmpty()) {
                                level().destroyBlock(carve, true);
                            } else {
                                state.onBlockExploded(level(), carve, dummyExplosion);
                            }
                        }
                    }
                    if (canSetToFire && random.nextFloat() < 0.15 && !level().getBlockState(carveBelow).isAir()) {
                        level().setBlockAndUpdate(carveBelow.above(), Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
    }

    private boolean isDestroyable(BlockState state) {
        return (!state.is(ACTagRegistry.NUKE_PROOF) && state.getBlock().getExplosionResistance() < AlexsCaves.COMMON_CONFIG.nukeMaxBlockExplosionResistance.get()) || state.is(ACBlockRegistry.TREMORZILLA_EGG.get());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SIZE, 1.0F);
        this.entityData.define(NO_GRIEFING, false);
        this.entityData.define(INTENTIONAL_GAME_DESIGN, false);
    }

    public float getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(float f) {
        this.entityData.set(SIZE, f);
    }

    public boolean isNoGriefing() {
        return this.entityData.get(NO_GRIEFING);
    }

    public void setNoGriefing(boolean noGriefing) {
        this.entityData.set(NO_GRIEFING, noGriefing);
    }

    public boolean isIntentionalGameDesign() {
        return this.entityData.get(INTENTIONAL_GAME_DESIGN);
    }

    public void setIntentionalGameDesign(boolean intentionalGameDesign) {
        this.entityData.set(INTENTIONAL_GAME_DESIGN, intentionalGameDesign);
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        loadingChunks = compoundTag.getBoolean("WasLoadingChunks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("WasLoadingChunks", loadingChunks);

    }
}

package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class AmberMonolithBlockEntity extends BlockEntity {

    public int tickCount;
    private int spawnsMobIn = 0;
    private int findSpawnsCooldown = 0;
    private EntityType spawnType;
    private int spawnCount;
    private Entity displayEntity;
    private Entity prevDisplayEntity;
    private float switchProgress;
    private float previousRotation;
    private float rotation = (float) (Math.random() * 360F);
    private boolean hasDonePostBossSpawn;

    public AmberMonolithBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.AMBER_MONOLITH.get(), pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AmberMonolithBlockEntity entity) {
        entity.tickCount++;
        entity.previousRotation = entity.rotation;
        entity.rotation += 1F;
        if (entity.spawnsMobIn <= 1000) {
            float f = (1000F - entity.spawnsMobIn) / 1000F;
            entity.rotation += f * 20;
        }
        if (entity.prevDisplayEntity != entity.displayEntity) {
            if (entity.displayEntity == null) {
                if (entity.switchProgress > 0.0F) {
                    entity.switchProgress--;
                } else {
                    entity.prevDisplayEntity = null;
                }
            } else {
                if (entity.switchProgress < 10.0F) {
                    entity.switchProgress++;
                } else {
                    entity.prevDisplayEntity = entity.displayEntity;
                }

            }
        }
        if (!level.isClientSide) {
            if (entity.spawnType == null && entity.findSpawnsCooldown-- <= 0 || !entity.hasDonePostBossSpawn && entity.isMigration()) {
                entity.findSpawnsCooldown = 40 + level.random.nextInt(50);
                entity.generateSpawnData();
                if(!entity.hasDonePostBossSpawn && entity.isMigration()){
                    entity.hasDonePostBossSpawn = true;
                    entity.spawnsMobIn = (int) Math.ceil(entity.spawnsMobIn * 0.25F) + 100;
                }
            }
            if (entity.spawnsMobIn <= 0) {
                if (entity.spawnType == null) {
                    entity.generateSpawnData();
                } else {
                    if (level.getNearestPlayer(entity.getBlockPos().getX() + 0.5F, entity.getBlockPos().getY() + 0.5F, entity.getBlockPos().getZ() + 0.5F, 28, false) != null && entity.spawnMobs()) {
                        level.playSound((Player)null, blockPos, ACSoundRegistry.AMBER_MONOLITH_SUMMON.get(), SoundSource.BLOCKS);
                        entity.generateSpawnData();
                    }
                }
            }
        }
        if (entity.spawnsMobIn > 0) {
            entity.spawnsMobIn--;
        }
    }

    private boolean spawnMobs() {
        SpawnGroupData spawngroupdata = null;
        boolean spawned = false;
        for (int l1 = 0; l1 < spawnCount; l1++) {
            boolean flag = false;
            for (int i2 = 0; !flag && i2 < 6; i2++) {
                BlockPos blockpos = getRandomSpawnPos();
                if (blockpos == null) {
                    continue;
                }
                if (spawnType.canSummon() && NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(spawnType), level, blockpos, spawnType)) {
                    double d0 = blockpos.getX() + 0.5F;
                    double d1 = blockpos.getZ() + 0.5F;
                    if (!level.noCollision(spawnType.getAABB(d0, (double) blockpos.getY(), d1)) || !SpawnPlacements.checkSpawnRules(spawnType, (ServerLevelAccessor) level, MobSpawnType.SPAWNER, BlockPos.containing(d0, (double) blockpos.getY(), d1), level.getRandom())) {
                        continue;
                    }

                    Entity entity;
                    try {
                        entity = spawnType.create(level);
                    } catch (Exception exception) {
                        AlexsCaves.LOGGER.warn("Failed to create mob", (Throwable) exception);
                        continue;
                    }

                    if (entity == null) {
                        continue;
                    }

                    entity.moveTo(d0, (double) blockpos.getY(), d1, level.random.nextFloat() * 360.0F, 0.0F);
                    if (entity instanceof Mob) {
                        Mob mob = (Mob) entity;
                        if (net.minecraftforge.event.ForgeEventFactory.checkSpawnPosition(mob, (ServerLevelAccessor) level, MobSpawnType.CHUNK_GENERATION)) {
                            spawngroupdata = mob.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CHUNK_GENERATION, spawngroupdata, (CompoundTag) null);
                            ((ServerLevel) level).addFreshEntityWithPassengers(mob);
                            spawned = true;
                            flag = true;
                            Vec3 center = this.getBlockPos().getCenter();
                            Vec3 target = entity.getEyePosition();
                            Vec3 distance = target.subtract(center);
                            int maxDist = (int) (distance.length() * 1.5);
                            for (int i = 0; i < maxDist; i++) {
                                Vec3 vec3 = center.add(distance.normalize().scale(distance.length() * (i / (float) maxDist))).add(level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F, level.random.nextFloat() - 0.5F);
                                ((ServerLevel) level).sendParticles(ACParticleRegistry.AMBER_MONOLITH.get(), vec3.x, vec3.y, vec3.z, 0, target.x, target.y, target.z, 1D);
                            }
                            for (int i = 0; i < 5; i++) {
                                ((ServerLevel) level).sendParticles(ACParticleRegistry.AMBER_EXPLOSION.get(), entity.getRandomX(1.0F), entity.getRandomY(), entity.getRandomZ(1.0F), 0, 0, 0, 0, 1D);
                            }
                        }
                    }
                }
            }
        }
        return spawned;
    }

    private BlockPos getRandomSpawnPos() {
        boolean caveCreature = spawnType.getCategory() == ACEntityRegistry.CAVE_CREATURE;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 20; i++) {
            mutableBlockPos.set(this.getBlockPos().getX() + level.getRandom().nextInt(20) - 10, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + level.getRandom().nextInt(20) - 10);
            if (!level.isLoaded(mutableBlockPos)) {
                continue;
            } else {
                while ((level.getBlockState(mutableBlockPos).isAir() || level.getBlockState(mutableBlockPos).canBeReplaced()) && mutableBlockPos.getY() > level.getMinBuildHeight()) {
                    mutableBlockPos.move(0, -1, 0);
                }
                if (Math.abs(mutableBlockPos.getY() - this.getBlockPos().getY()) < 20) {
                    BlockPos pos = mutableBlockPos.immutable();
                    if(!caveCreature || !level.canSeeSky(pos.above())){
                        return pos.above();
                    }
                }
            }
        }
        return null;
    }

    private void generateSpawnData() {
        List<EntityType<?>> forcedEntityList = new ArrayList<>();
        if(isMigration() && !this.hasDonePostBossSpawn){
            forcedEntityList.add(ACEntityRegistry.ATLATITAN.get());
        }
        MobSpawnSettings.SpawnerData spawnerData = getDepopulatedEntitySpawnData(level, this.getBlockPos(), 4 + level.random.nextInt(8), 64, forcedEntityList);
        if (spawnerData != null) {
            spawnType = spawnerData.type;
            int j = Math.max(spawnerData.maxCount - spawnerData.minCount, 0);
            spawnCount = j <= 0 ? spawnerData.minCount : level.random.nextInt(j) + spawnerData.minCount;
        }
        int i = Math.max(1000, AlexsCaves.COMMON_CONFIG.amberMonolithMeanTime.get());
        spawnsMobIn = i / 2 + level.getRandom().nextInt(i);
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }

    private static boolean isEntitySpawnBlocked(MobSpawnSettings.SpawnerData settings, Level level, BlockPos pos, int range) {
        if(settings.type.is(ACTagRegistry.AMBER_MONOLITH_SKIPS)){
            return true;
        }
        if(settings.type == ACEntityRegistry.ATLATITAN.get()){
            ACWorldData worldData = ACWorldData.get(level);
            if(worldData != null && !worldData.isPrimordialBossDefeatedOnce()){
                return true;
            }
        }
        return !level.getEntities(settings.type, (new AABB(pos)).inflate(range), Entity::isAlive).isEmpty();
    }

    private static MobSpawnSettings.SpawnerData getEntitySpawnSettingsForBiome(Level level, BlockPos pos, List<EntityType<?>> forcedEntityTypes) {
        Biome biome = level.getBiome(pos).value();
        if (biome != null) {
            WeightedRandomList<MobSpawnSettings.SpawnerData> spawnList = biome.getMobSettings().getMobs(ACEntityRegistry.CAVE_CREATURE);
            if (spawnList.isEmpty()) {
                spawnList = biome.getMobSettings().getMobs(MobCategory.CREATURE);
            }
            if(!forcedEntityTypes.isEmpty()){
                List<MobSpawnSettings.SpawnerData> matching = new ArrayList<>();
                for(MobSpawnSettings.SpawnerData unwrapped : spawnList.unwrap()){
                    if(forcedEntityTypes.contains(unwrapped.type)){
                        matching.add(unwrapped);
                    }
                }
                if(!matching.isEmpty()){
                    spawnList = WeightedRandomList.create(matching);
                }
            }
            if (!spawnList.isEmpty()) {
                return spawnList.getRandom(level.random).get();
            }
        }
        return null;
    }

    private static MobSpawnSettings.SpawnerData getDepopulatedEntitySpawnData(Level level, BlockPos pos, int rolls, int range, List<EntityType<?>> forcedEntityTypes) {
        MobSpawnSettings.SpawnerData spawnerData = null;
        int roll = 0;
        while (roll < rolls) {
            if (spawnerData != null && !isEntitySpawnBlocked(spawnerData, level, pos, range)) {
                return spawnerData;
            }
            spawnerData = getEntitySpawnSettingsForBiome(level, pos, forcedEntityTypes);
            roll++;
        }
        return spawnerData;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            if (packet.getTag().contains("EntityType")) {
                String str = packet.getTag().getString("EntityType");
                this.spawnType = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.parse(str));
            }
            this.spawnCount = packet.getTag().getInt("SpawnCount");
            this.spawnsMobIn = packet.getTag().getInt("SpawnMobsIn");
            this.hasDonePostBossSpawn = packet.getTag().getBoolean("PostBossSpawn");
        }
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("EntityType")) {
            String str = tag.getString("EntityType");
            this.spawnType = ForgeRegistries.ENTITY_TYPES.getValue(ResourceLocation.parse(str));
        }
        this.spawnCount = tag.getInt("SpawnCount");
        this.spawnsMobIn = tag.getInt("SpawnMobsIn");
        this.hasDonePostBossSpawn = tag.getBoolean("PostBossSpawn");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.spawnType != null) {
            tag.putString("EntityType", ForgeRegistries.ENTITY_TYPES.getKey(this.spawnType).toString());
        }
        tag.putInt("SpawnCount", this.spawnCount);
        tag.putInt("SpawnMobsIn", this.spawnsMobIn);
        tag.putBoolean("PostBossSpawn", this.hasDonePostBossSpawn);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public int getSpawnsMobIn() {
        return spawnsMobIn;
    }

    public Entity getDisplayEntity(Level level) {
        if (displayEntity == null && spawnType != null || displayEntity != null && displayEntity.getType() != spawnType) {
            displayEntity = spawnType.create(level);
        }
        return displayEntity;
    }
    public Entity getPrevDisplayEntity() {
        return prevDisplayEntity;
    }

    public float getRotation(float partialTicks) {
        return previousRotation + (rotation - previousRotation) * partialTicks;
    }

    private boolean isMigration(){
        ACWorldData worldData = ACWorldData.get(level);
        if(worldData != null){
            return worldData.isPrimordialBossDefeatedOnce() && worldData.getFirstPrimordialBossDefeatTimestamp() != -1 && worldData.getFirstPrimordialBossDefeatTimestamp() + 24000 > level.getGameTime();
        }
        return false;
    }
}

package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.GumWormEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GobthumperBlockEntity extends BlockEntity {

    private int thumpTime;
    private int particleColor;
    private boolean hasThumpTicked = false;

    private int summonedWormId = -1;

    public GobthumperBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.GOBTHUMPER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GobthumperBlockEntity entity) {
        entity.thumpTime++;
        if(Math.sin(entity.thumpTime * 0.7F) <= 0.1F){
            if(!entity.hasThumpTicked){
                entity.hasThumpTicked = true;
                entity.level.playSound(null, entity.getBlockPos(), ACSoundRegistry.GOBTHUMPER_THUMP.get(), SoundSource.BLOCKS);
                entity.thumpTick(level);
            }
        }else{
            entity.hasThumpTicked = false;
        }
    }

    private void thumpTick(Level level) {
        if(level.isClientSide){
            level.addAlwaysVisibleParticle(ACParticleRegistry.GOBTHUMPER.get(), true, this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 0.15F, this.getBlockPos().getZ() + 0.5F, particleColor, 0, 0);
            particleColor = (particleColor + 1) % 3;
        }else{
            if(this.summonedWormId != -1 && level.getEntity(this.summonedWormId) instanceof GumWormEntity gumWorm && gumWorm.isAlive()){
                gumWorm.setGobthumperPos(this.getBlockPos());
            }else{
                GumWormEntity closestWorm = null;
                Vec3 vec3 = this.getBlockPos().getCenter();
                for (GumWormEntity worm : level.getEntitiesOfClass(GumWormEntity.class, new AABB(this.getBlockPos()).inflate(200, 200, 200))) {
                    if (closestWorm == null || worm.distanceToSqr(vec3) < closestWorm.distanceToSqr(vec3)) {
                        closestWorm = worm;
                    }
                }
                if (closestWorm != null) {
                    this.summonedWormId = closestWorm.getId();
                }else{
                    boolean flag = false;
                    BlockPos summonPos = null;
                    for(int i = 0; i < 15; i++){
                        summonPos = this.getBlockPos().offset(level.getRandom().nextInt(200) - 100, -30, level.getRandom().nextInt(200) - 100);
                        if(summonPos.getY() < level.getMinBuildHeight() + 2){
                            summonPos = summonPos.atY(level.getMinBuildHeight() + 2);
                        }
                        if(level.isLoaded(summonPos) && level.getBlockState(summonPos).isSolid() && !level.getBlockState(summonPos).is(ACTagRegistry.GUM_WORM_BLOCKS_DIGGING)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag){
                        GumWormEntity summonedWorm = ACEntityRegistry.GUM_WORM.get().create(level);
                        summonedWorm.setPos(Vec3.atCenterOf(summonPos));
                        summonedWorm.setTempSummon(true);
                        summonedWorm.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(summonPos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                        level.addFreshEntity(summonedWorm);
                        this.summonedWormId = summonedWorm.getId();
                    }
                }
            }
        }
    }

    public float getThumpTime(float partialTicks){
        return thumpTime + partialTicks;
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.summonedWormId = tag.getInt("SummonedWormID");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("SummonedWormID", this.summonedWormId);
    }

}

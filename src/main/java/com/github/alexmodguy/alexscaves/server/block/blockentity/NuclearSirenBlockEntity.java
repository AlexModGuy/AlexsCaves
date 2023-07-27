package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.NuclearSirenBlock;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class NuclearSirenBlockEntity extends BlockEntity {

    private float volumeProgress;
    private float prevVolumeProgress;

    public int age;
    private Entity nearestNuclearBomb = null;
    private int bombId = -1;
    public NuclearSirenBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.NUCLEAR_SIREN.get(), pos, state);
        if (state.getValue(NuclearSirenBlock.POWERED)) {
            prevVolumeProgress = volumeProgress = 10.0F;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, NuclearSirenBlockEntity entity) {
        entity.prevVolumeProgress = entity.volumeProgress;
        boolean powered = entity.isActivated(state);
        entity.age++;
        if (powered && entity.volumeProgress < 10.0F) {
            entity.volumeProgress += 0.5F;
        } else if (!powered && entity.volumeProgress > 0.0F) {
            entity.volumeProgress -= 0.5F;
        }
        if(powered){
            AlexsCaves.PROXY.playWorldSound(entity, (byte)0);
            int j = entity.age % 18;
            if(level.isClientSide && j >= 9 && j  % 3 == 0){
                Vec3 particlesFrom = blockPos.getCenter().add(0, 0.2, 0);
                for(Direction direction : ACMath.HORIZONTAL_DIRECTIONS){
                    Vec3 vec3 = particlesFrom.add(direction.getStepX() * 0.5F, 0, direction.getStepZ() * 0.5F);
                    float yRot = direction.toYRot();
                    level.addAlwaysVisibleParticle(ACParticleRegistry.NUCLEAR_SIREN_SONAR.get(), true, vec3.x, vec3.y, vec3.z, 0, yRot, 0);
                }
            }
        }
        if(!level.isClientSide){
            if(entity.nearestNuclearBomb == null || entity.nearestNuclearBomb.isRemoved()){
                entity.nearestNuclearBomb = null;
                int prevBombId = entity.bombId;
                entity.bombId = -1;
                if(prevBombId != entity.bombId){
                    level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 2);
                }
            }else{
                int prevBombId = entity.bombId;
                entity.bombId = entity.nearestNuclearBomb != null ? entity.nearestNuclearBomb.getId() : -1;
                if(prevBombId != entity.bombId){
                    level.sendBlockUpdated(entity.getBlockPos(), entity.getBlockState(), entity.getBlockState(), 2);
                }
            }
        }
    }

    public void setNearestNuclearBomb(Entity bomb){
        Vec3 center = getBlockPos().getCenter();
        if(nearestNuclearBomb == null || nearestNuclearBomb.distanceToSqr(center) > bomb.distanceToSqr(center)){
            nearestNuclearBomb = bomb;
        }
    }

    public boolean isActivated(BlockState state){
        return state.is(ACBlockRegistry.NUCLEAR_SIREN.get()) && state.getValue(NuclearSirenBlock.POWERED) || this.bombId != -1;
    }
    public float getVolume(float partialTicks) {
        return (prevVolumeProgress + (volumeProgress - prevVolumeProgress) * partialTicks) * 0.1F;
    }


    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.bombId = tag.getInt("BombID");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BombID", this.bombId);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            this.bombId = packet.getTag().getInt("BombID");
        }
    }
}

package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.CaramelCubeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class MeltedCaramelEntity extends Entity {

    private int despawnsIn = 40;
    private int prevDespawnsIn;
    private float yRenderOffset = random.nextFloat() * 0.05F;

    public MeltedCaramelEntity(EntityType entityType, Level level) {
        super(entityType, level);
    }

    public MeltedCaramelEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.MELTED_CARAMEL.get(), level);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) NetworkHooks.getEntitySpawningPacket(this);
    }

    public void tick() {
        super.tick();
        prevDespawnsIn = despawnsIn;
        if(despawnsIn > 0){
            despawnsIn--;
        }else if(!level().isClientSide){
          this.discard();
        }
        BlockPos below = this.blockPosition().below();
        if(!level().isClientSide && !this.level().getBlockState(below).isFaceSturdy(this.level(), below, Direction.UP, SupportType.CENTER)){
           this.discard();
        }
        slowEntities();
        Vec3 vec3 = this.getDeltaMovement();
        this.move(MoverType.SELF, vec3);
        this.setDeltaMovement(vec3.multiply((double) 0.2F, (double) 0.2F, (double) 0.2F));
    }

    public void setDespawnsIn(int i){
        this.despawnsIn = i;
    }

    public float getDespawnTime(float partialTicks){
        return prevDespawnsIn + (despawnsIn - prevDespawnsIn) * partialTicks;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if(compoundTag.contains("DespawnsIn")){
            this.despawnsIn = compoundTag.getInt("DespawnsIn");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("DespawnsIn", this.despawnsIn);
    }

    private void slowEntities() {
        AABB bashBox = this.getBoundingBox();
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
            if (!isAlliedTo(entity) && !(entity instanceof CaramelCubeEntity)) {
                entity.makeStuckInBlock(Blocks.DIRT.defaultBlockState(), new Vec3(0.25D, (double)0.05F, 0.25D));
            }
        }
    }

    public float getYRenderOffset() {
        return yRenderOffset;
    }
}

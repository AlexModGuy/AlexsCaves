package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.MultipartEntityMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public class HullbreakerPartEntity extends PartEntity<HullbreakerEntity> {

    private final Entity connectedTo;
    private EntityDimensions size;
    public float scale = 1;

    public HullbreakerPartEntity(HullbreakerEntity parent, Entity connectedTo, float sizeXZ, float sizeY) {
        super(parent);
        this.blocksBuilding = true;
        this.connectedTo = connectedTo;
        this.size = EntityDimensions.fixed(sizeXZ, sizeY);
        this.refreshDimensions();
    }

    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        HullbreakerEntity parent = this.getParent();
        if (parent == null) {
            return InteractionResult.PASS;
        } else {
            this.playSound(SoundEvents.ITEM_BREAK);
            if (player.level().isClientSide) {
                AlexsCaves.sendMSGToServer(new MultipartEntityMessage(parent.getId(), player.getId(), 0, 0));
            }
            return parent.interact(player, hand);
        }
    }

    @Override
    public boolean save(CompoundTag tag) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        HullbreakerEntity parent = this.getParent();
        return parent != null && parent.canBeCollidedWith();
    }


    @Override
    public boolean isPickable() {
        HullbreakerEntity parent = this.getParent();
        return parent != null && parent.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        HullbreakerEntity parent = this.getParent();
        if(source.is(DamageTypeTags.IS_PROJECTILE)){
            amount *= 0.35F;
        }
        if (!this.isInvulnerableTo(source) && parent != null) {
            Entity player = source.getEntity();
            if (player != null && player.level().isClientSide) {
                AlexsCaves.sendMSGToServer(new MultipartEntityMessage(parent.getId(), player.getId(), 1, amount));
            }
        }
        return false;
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2.0D, 0.5D, 2.0D);
    }

    public float calculateAnimationAngle(float partialTicks, boolean pitch) {
        HullbreakerEntity parent = this.getParent();
        float parentRot = parent == null ? 0 : (parent.yBodyRotO + (parent.yBodyRot - parent.yBodyRotO) * partialTicks);
        Vec3 connection = connectedTo.getPosition(partialTicks).add(0, connectedTo.getBbHeight() * 0.5F, 0);
        Vec3 center = centeredPosition(partialTicks);
        Vec3 offset = connection.subtract(center).normalize();
        Vec3 back = center.add(offset.scale(-1 * this.getBbWidth()));
        double d0 = connection.x - back.x;
        double d1 = connection.y - back.y;
        double d2 = connection.z - back.z;
        if (pitch) {
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            return Mth.wrapDegrees((float) (-(Mth.atan2(d1, d3) * 180.0F / (float) Math.PI))) * 0.35F;
        } else {
            return (float) (Mth.atan2(d2, d0) * 57.2957763671875D) - 90.0F - parentRot;
        }
    }

    public boolean shouldBeSaved() {
        return false;
    }

    public void setPosCenteredY(Vec3 pos) {
        this.setPos(pos.x, pos.y - this.getBbHeight() * 0.5F, pos.z);
    }

    public Vec3 centeredPosition() {
        return this.position().add(0, this.getBbHeight() * 0.5F, 0);
    }

    public Vec3 centeredPosition(float partialTicks) {
        return this.getPosition(partialTicks).add(0, this.getBbHeight() * 0.5F, 0);
    }
}

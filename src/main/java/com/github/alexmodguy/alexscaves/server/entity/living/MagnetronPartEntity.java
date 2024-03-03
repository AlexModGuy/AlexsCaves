package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetronJoint;
import com.github.alexmodguy.alexscaves.server.message.MultipartEntityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.stringtemplate.v4.ST;

public class MagnetronPartEntity extends PartEntity<MagnetronEntity> {

    private final MagnetronJoint joint;
    private BlockPos startPosition;
    private BlockState blockState;
    private EntityDimensions size;
    public boolean left;
    public float scale = 1;
    private final BlockState STONE = Blocks.STONE.defaultBlockState();

    public MagnetronPartEntity(MagnetronEntity parent, MagnetronJoint joint, boolean left) {
        super(parent);
        this.blocksBuilding = true;
        this.size = EntityDimensions.fixed(0.9F, 0.9F);
        this.joint = joint;
        this.left = left;
        this.refreshDimensions();
    }


    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public MagnetronJoint getJoint() {
        return joint;
    }

    public boolean isLeft() {
        return left;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        MagnetronEntity parent = this.getParent();
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
        MagnetronEntity parent = this.getParent();
        return parent != null && parent.canBeCollidedWith();
    }


    @Override
    public boolean isPickable() {
        MagnetronEntity parent = this.getParent();
        return parent != null && parent.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        MagnetronEntity parent = this.getParent();
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


    public boolean shouldBeSaved() {
        return false;
    }

    public void positionMultipart(MagnetronEntity entity) {
        Vec3 targetPos = entity.position().add(this.joint.getTargetPosition(entity, left));
        Vec3 start = startPosition == null ? entity.position() : Vec3.atCenterOf(startPosition);
        Vec3 addToStart = targetPos.subtract(start);
        this.setPos(start.add(addToStart.scale(entity.getFormProgress(1.0F))));
    }

    public void setStartsAt(BlockPos pos) {
        startPosition = pos;
    }

    public BlockPos getStartPosition() {
        return startPosition;
    }

    public BlockState getBlockState() {
        return blockState;
    }
    public BlockState getVisualBlockState() {
        MagnetronEntity parent = this.getParent();
        return blockState == null && parent != null && parent.isAlive() ? STONE : blockState;
    }

    public void setBlockState(BlockState state) {
        blockState = state;
    }

    public double getLowPoint() {
        return this.getBoundingBox().minY;
    }
}

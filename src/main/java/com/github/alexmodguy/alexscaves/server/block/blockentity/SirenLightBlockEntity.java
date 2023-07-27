package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.server.block.SirenLightBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SirenLightBlockEntity extends BlockEntity {
    private float onProgress;
    private float prevOnProgress;

    private float sirenRotation;
    private float prevSirenRotation;

    private int color = -1;

    public SirenLightBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.SIREN_LIGHT.get(), pos, state);
        if (state.getValue(SirenLightBlock.POWERED)) {
            prevOnProgress = onProgress = 10.0F;
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, SirenLightBlockEntity entity) {
        entity.prevOnProgress = entity.onProgress;
        entity.prevSirenRotation = entity.sirenRotation;
        boolean powered = state.getValue(SirenLightBlock.POWERED);

        if (powered && entity.onProgress < 10.0F) {
            entity.onProgress += 1F;
        } else if (!powered && entity.onProgress > 0.0F) {
            entity.onProgress -= 1F;
        }
        if (powered) {
            entity.sirenRotation += entity.onProgress * 2F + 0.25F;
        }
    }

    public float getOnProgress(float partialTicks) {
        return (prevOnProgress + (onProgress - prevOnProgress) * partialTicks) * 0.1F;
    }

    public float getSirenRotation(float partialTicks) {
        return (prevSirenRotation + (sirenRotation - prevSirenRotation) * partialTicks);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.color = tag.getInt("Color");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Color", this.color);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            this.color = packet.getTag().getInt("Color");
        }
    }


    public void setColor(int setTo) {
        this.color = setTo;
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
    }

    public int getColor() {
        return color < 0 ? 0X00FF00 : color;
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        return new AABB(pos.offset(-3, -3, -3), pos.offset(4, 4, 4));
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

}

package com.github.alexmodguy.alexscaves.server.entity.living;

import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CorrodentTailEntity extends ACMultipartEntity<CorrodentEntity> {

    private EntityDimensions size;
    public float scale = 1;

    public CorrodentTailEntity(CorrodentEntity parent) {
        super(parent);
        this.size = EntityDimensions.fixed(0.9F, 0.9F);
        this.refreshDimensions();
    }

    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0D, 1.0D, 1.0D);
    }

    public void setToTransformation(Vec3 offset, float xRot, float yRot) {
        Vec3 transformed = offset.xRot((float) (-xRot * (Math.PI / 180F))).yRot((float) (-yRot * (Math.PI / 180F)));
        Vec3 offseted = transformed.add(getParent().position().add(0, getParent().getBbHeight() * 0.5F, 0));
        this.setPos(offseted.x, offseted.y - this.getBbHeight() * 0.5F, offseted.z);
    }
}


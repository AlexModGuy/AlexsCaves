package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.util.ACMultipartEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;

public class QuarrySmasherHeadEntity extends ACMultipartEntity<QuarrySmasherEntity> {

    private EntityDimensions size;

    public QuarrySmasherHeadEntity(QuarrySmasherEntity parent) {
        super(parent);
        this.size = EntityDimensions.fixed(0.9F, 0.6F);
        this.refreshDimensions();
    }

    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0D, 1.0D, 1.0D);
    }
}


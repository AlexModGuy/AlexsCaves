package com.github.alexmodguy.alexscaves.server.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface MagneticEntityAccessor {

    float getMagneticDeltaX();

    float getMagneticDeltaY();

    float getMagneticDeltaZ();

    Direction getMagneticAttachmentFace();

    Direction getPrevMagneticAttachmentFace();

    float getAttachmentProgress(float partialTicks);

    void setMagneticDeltaX(float f);

    void setMagneticDeltaY(float f);

    void setMagneticDeltaZ(float f);

    void setMagneticAttachmentFace(Direction dir);

    void postMagnetJump();

    boolean canChangeDirection();

    void stepOnMagnetBlock(BlockPos pos);
}

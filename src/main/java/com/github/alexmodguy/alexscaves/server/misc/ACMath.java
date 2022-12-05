package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexthe666.citadel.animation.Animation;

public class ACMath {

    public static float smin(float a, float b, float k) {
        float h = Math.max(k - Math.abs(a - b), 0.0F) / k;
        return Math.min(a, b) - h * h * k * (1.0F / 4.0F);
    }

    public static float cullAnimationTick(int tick, float amplitude, Animation animation, float partialTick, int startOffset) {
        float i = Math.max(tick + partialTick - startOffset, 0);
        float f = (float) Math.sin(((i ) / (float) (animation.getDuration() - startOffset)) * Math.PI) * amplitude;
        return ACMath.smin(f, 1.0F, 0.1F);
    }

}

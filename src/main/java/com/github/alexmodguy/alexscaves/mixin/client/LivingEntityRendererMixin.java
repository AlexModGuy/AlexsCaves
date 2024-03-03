package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.render.entity.LivingEntityRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer implements LivingEntityRendererAccessor {

    @Shadow protected abstract void scale(LivingEntity living, PoseStack poseStack, float f);

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    public void scaleForHologram(LivingEntity entity, PoseStack poseStack, float partialTicks) {
        this.scale(entity, poseStack, partialTicks);
    }
}

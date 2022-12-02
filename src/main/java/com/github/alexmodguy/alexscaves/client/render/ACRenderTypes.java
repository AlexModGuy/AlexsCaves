package com.github.alexmodguy.alexscaves.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ACRenderTypes extends RenderType {

    protected static final RenderStateShard.TransparencyStateShard EYES_ALPHA_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("eyes_alpha_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public ACRenderTypes(String s, VertexFormat format, VertexFormat.Mode mode, int i, boolean b1, boolean b2, Runnable runnable1, Runnable runnable2) {
        super(s, format, mode, i, b1, b2, runnable1, runnable2);
    }

    public static RenderType getParticleTrail(ResourceLocation resourceLocation) {
        return create("particle_trail", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, true, true)).setLightmapState(LIGHTMAP).setCullState(RenderStateShard.NO_CULL).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(true));
    }

    public static RenderType getEyesAlphaEnabled(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setTransparencyState(EYES_ALPHA_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setDepthTestState(EQUAL_DEPTH_TEST).createCompositeState(true);
        return create("eye_alpha", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    }

    public static RenderType getAmbersolShine() {
        return create("ambersol_shine", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setShaderState(RenderType.RENDERTYPE_LIGHTNING_SHADER)
                .setTransparencyState(EYES_ALPHA_TRANSPARENCY)
                .setCullState(CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                .setOutputState(RenderStateShard.PARTICLES_TARGET)
                .createCompositeState(true));
    }

}

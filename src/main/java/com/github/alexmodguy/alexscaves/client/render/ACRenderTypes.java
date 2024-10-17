package com.github.alexmodguy.alexscaves.client.render;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;


public class ACRenderTypes extends RenderType {
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_FEROUSSLIME_GEL_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeFerrouslimeGelShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_HOLOGRAM_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeHologramShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_IRRADIATED_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeIrradiatedShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_BLUE_IRRADIATED_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeBlueIrradiatedShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_BUBBLED_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeBubbledShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_SEPIA_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeSepiaShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_RED_GHOST_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypeRedGhostShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_PURPLE_WITCH_SHADER = new RenderStateShard.ShaderStateShard(ACInternalShaders::getRenderTypePurpleWitchShader);

    protected static final RenderStateShard.OutputStateShard IRRADIATED_OUTPUT = new RenderStateShard.OutputStateShard("irradiated_target", () -> {
        RenderTarget target = PostEffectRegistry.getRenderTargetFor(ClientProxy.IRRADIATED_SHADER);
        if (target != null) {
            target.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            target.bindWrite(false);
        }
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    });
    protected static final RenderStateShard.OutputStateShard HOLOGRAM_OUTPUT = new RenderStateShard.OutputStateShard("hologram_target", () -> {
        RenderTarget target = PostEffectRegistry.getRenderTargetFor(ClientProxy.HOLOGRAM_SHADER);
        if (target != null) {
            target.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            target.bindWrite(false);
        }
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    });

    protected static final RenderStateShard.OutputStateShard PURPLE_WITCH_OUTPUT = new RenderStateShard.OutputStateShard("purple_witch_target", () -> {
        RenderTarget target = PostEffectRegistry.getRenderTargetFor(ClientProxy.PURPLE_WITCH_SHADER);
        if (target != null) {
            target.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            target.bindWrite(false);
        }
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    });

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

    public static RenderType getVoidBeingCloud(ResourceLocation resourceLocation) {
        return create("void_being", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, true)).setLightmapState(LIGHTMAP).setCullState(RenderStateShard.NO_CULL).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setOverlayState(OVERLAY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(true));
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

    public static RenderType getNucleeperLights() {
        return create("nucleeper_lights", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setShaderState(RenderType.RENDERTYPE_LIGHTNING_SHADER)
                .setTransparencyState(EYES_ALPHA_TRANSPARENCY)
                .setCullState(CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .createCompositeState(true));
    }

    public static RenderType getHologramLights() {
        return create("hologram_lights", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_HOLOGRAM_SHADER)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLightmapState(NO_LIGHTMAP)
                .setOutputState(HOLOGRAM_OUTPUT)
                .createCompositeState(false));
    }

    public static RenderType getCrucibleItemBeam() {
        return create("crucible_item_beam", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
                .setCullState(CULL)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(NO_LIGHTMAP)
                .createCompositeState(true));
    }

    public static RenderType getSubmarineLights() {
        return create("submarine_lights", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setLightmapState(NO_LIGHTMAP)
                .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                .createCompositeState(false));
    }


    public static RenderType getGel(ResourceLocation locationIn) {
        return create("ferrouslime_gel", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setCullState(NO_CULL)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setShaderState(RENDERTYPE_FEROUSSLIME_GEL_SHADER)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(true));
    }

    public static RenderType getRadiationGlow(ResourceLocation locationIn) {
        return create("radiation_glow", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_IRRADIATED_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setOutputState(IRRADIATED_OUTPUT)
                .createCompositeState(false));
    }

    public static RenderType getBlueRadiationGlow(ResourceLocation locationIn) {
        return create("blue_radiation_glow", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_BLUE_IRRADIATED_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setOutputState(IRRADIATED_OUTPUT)
                .createCompositeState(false));
    }
    public static RenderType getGelTriangles(ResourceLocation locationIn) {
        return create("ferrouslime_gel_triangles", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, true, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setCullState(NO_CULL)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setShaderState(RENDERTYPE_FEROUSSLIME_GEL_SHADER)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false));
    }


    public static RenderType getSubmarineMask() {
        return create("submarine_mask", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_WATER_MASK_SHADER).setTextureState(NO_TEXTURE).setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST).setWriteMaskState(DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));
    }

    public static RenderType getGhostly(ResourceLocation texture) {
        CompositeState renderState = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .createCompositeState(true);
        return create("ghostly", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, renderState);
    }


    public static RenderType getTeslaBulb(ResourceLocation resourceLocation) {
        return create("tesla_bulb", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, true)).setLightmapState(LIGHTMAP).setCullState(RenderStateShard.NO_CULL).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(true));
    }

    public static RenderType getHologram(ResourceLocation locationIn) {
        return create("hologram", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setOutputState(HOLOGRAM_OUTPUT)
                .createCompositeState(false));
    }


    public static RenderType getRedGhost(ResourceLocation locationIn) {
        return create("red_ghost", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_RED_GHOST_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(EYES_ALPHA_TRANSPARENCY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setOverlayState(OVERLAY)
                .createCompositeState(true));
    }

    public static RenderType getCaveMapBackground(ResourceLocation locationIn, boolean showBackground) {
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_TEXT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setCullState(showBackground ? NO_CULL : CULL)
                .createCompositeState(false);
        return create("cave_map_background", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, rendertype$state);
    }

    public static RenderType getBookWidget(ResourceLocation locationIn, boolean sepia) {
        if(sepia){
            return create("book_widget", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_SEPIA_SHADER)
                    .setCullState(NO_CULL)
                    .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));
        }else{
            return ForgeRenderTypes.getUnlitTranslucent(locationIn);
        }

    }

    public static RenderType getBubbledCull(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_BUBBLED_SHADER).setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOutputState(RenderStateShard.ITEM_ENTITY_TARGET).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);
        return create("bubbled_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
    }

    public static RenderType getBubbledNoCull(ResourceLocation locationIn) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_BUBBLED_SHADER).setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOutputState(RenderStateShard.ITEM_ENTITY_TARGET).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);
        return create("bubbled_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, rendertype$compositestate);
    }

    public static RenderType getRaygunRay(ResourceLocation locationIn, boolean irradiated) {
        return create("raygun_ray", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 256, true, true, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setShaderState(RenderType.RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOutputState(irradiated ? IRRADIATED_OUTPUT : ITEM_ENTITY_TARGET)
                .createCompositeState(false));
    }

    public static RenderType getTremorzillaBeam(ResourceLocation locationIn, boolean irradiated) {
        return create("tremorzilla_beam", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setShaderState(RenderType.RENDERTYPE_ENERGY_SWIRL_SHADER)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOutputState(irradiated ? IRRADIATED_OUTPUT : ITEM_ENTITY_TARGET)
                .createCompositeState(false));
    }

    public static RenderType getPurpleWitch(ResourceLocation locationIn) {
        return create("purple_witch", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_PURPLE_WITCH_SHADER)
                .setCullState(NO_CULL)
                .setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
                .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setOutputState(PURPLE_WITCH_OUTPUT)
                .createCompositeState(false));
    }

}

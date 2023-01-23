package com.github.alexmodguy.alexscaves.client.render.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.HashMap;

public class DefaultMapBackgrounds {
    private static final ResourceLocation MAGNETIC_CAVES_BG = new ResourceLocation(AlexsCaves.MODID, "textures/misc/map/magnetic_caves_background.png");
    private static final ResourceLocation PRIMORDIAL_CAVES_BG = new ResourceLocation(AlexsCaves.MODID, "textures/misc/map/primordial_caves_background.png");
    private static final ResourceLocation TOXIC_CAVES_BG = new ResourceLocation(AlexsCaves.MODID, "textures/misc/map/toxic_caves_background.png");
    private static final HashMap<Integer, MapBackgroundTexture> TEXTURE_HASH_MAP = new HashMap<>();

    public static MapBackgroundTexture getBackgroundTexture(int id){
        if(TEXTURE_HASH_MAP.containsKey(id)){
            return TEXTURE_HASH_MAP.get(id);
        }else{
            ResourceLocation resourceLocation = getResourceLocation(id);
            MapBackgroundTexture simpleTexture = new MapBackgroundTexture(resourceLocation);
            Minecraft.getInstance().getTextureManager().register(resourceLocation, simpleTexture);
            TEXTURE_HASH_MAP.put(id,simpleTexture);
            return simpleTexture;
        }
    }

    private static ResourceLocation getResourceLocation(int id) {
        switch (id){
            case 1:
                return PRIMORDIAL_CAVES_BG;
            case 2:
                return TOXIC_CAVES_BG;
            default:
            case 0:
                return MAGNETIC_CAVES_BG;
        }
    }

    public static int getMapColor(int i, int u, int v) {
        MapBackgroundTexture texture = getBackgroundTexture(i);
        return texture.getNativeImage() == null ? 0 : clampNativeImg(texture.getNativeImage(), u, v);
    }

    private static int clampNativeImg(NativeImage nativeImage, int u, int v) {
        return nativeImage.getPixelRGBA(u % nativeImage.getWidth(), v % nativeImage.getHeight());
    }

    public static class MapBackgroundTexture extends SimpleTexture{

        private NativeImage nativeImage;

        public MapBackgroundTexture(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        public NativeImage getNativeImage(){
            return nativeImage;
        }

        public void load(ResourceManager resourceManager) throws IOException {
            super.load(resourceManager);
            nativeImage = this.getTextureImage(resourceManager).getImage();
        }
    }

}

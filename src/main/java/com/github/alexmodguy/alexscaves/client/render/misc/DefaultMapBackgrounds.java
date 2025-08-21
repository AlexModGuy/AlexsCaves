package com.github.alexmodguy.alexscaves.client.render.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public enum DefaultMapBackgrounds {

    DEFAULT,
    BORDER,
    WATER,
    FROZEN_OCEAN,
    PLAINS,
    DESERT,
    FOREST,
    JUNGLE,
    TAIGA,
    SNOWY,
    SNOWY_TAIGA,
    BADLANDS,
    MOUNTAIN,
    SNOWY_MOUNTAIN,
    ROOFED_FOREST,
    MUSHROOM,
    SWAMP,
    SAVANNA,
    ICE_SPIKES,
    BEACH,
    STONY_SHORE,
    DRIPSTONE_CAVES,
    LUSH_CAVES,
    DEEP_DARK,
    MAGNETIC_CAVES,
    PRIMORDIAL_CAVES,
    TOXIC_CAVES,
    ABYSSAL_CHASM,
    FORLORN_HOLLOWS,
    CANDY_CAVITY;

    private ResourceLocation texture;

    private static final HashMap<Integer, MapBackgroundTexture> TEXTURE_HASH_MAP = new HashMap<>();

    private static MapBackgroundTexture getBackgroundTexture(int id, ResourceLocation resourceLocation) {
        if (TEXTURE_HASH_MAP.containsKey(id)) {
            return TEXTURE_HASH_MAP.get(id);
        } else {
            MapBackgroundTexture simpleTexture = new MapBackgroundTexture(resourceLocation);
            Minecraft.getInstance().getTextureManager().register(resourceLocation, simpleTexture);
            TEXTURE_HASH_MAP.put(id, simpleTexture);
            return simpleTexture;
        }
    }

    public int getMapColor(int u, int v) {
        if(texture == null){
            texture = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/map/" + this.name().toLowerCase(Locale.ROOT) + "_background.png");
        }
        MapBackgroundTexture backgroundTexture = getBackgroundTexture(this.ordinal(), texture);
        return backgroundTexture.getNativeImage() == null ? 0 : clampNativeImg(backgroundTexture.getNativeImage(), u, v);
    }

    private static int clampNativeImg(NativeImage nativeImage, int u, int v) {
        return nativeImage.getPixelRGBA(u % nativeImage.getWidth(), v % nativeImage.getHeight());
    }

    public static class MapBackgroundTexture extends SimpleTexture {

        private NativeImage nativeImage;

        public MapBackgroundTexture(ResourceLocation resourceLocation) {
            super(resourceLocation);
        }

        public NativeImage getNativeImage() {
            return nativeImage;
        }

        public void load(ResourceManager resourceManager) throws IOException {
            super.load(resourceManager);
            nativeImage = this.getTextureImage(resourceManager).getImage();
        }
    }

}

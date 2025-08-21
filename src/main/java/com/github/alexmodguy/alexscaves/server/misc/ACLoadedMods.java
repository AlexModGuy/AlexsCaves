package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraftforge.fml.ModList;

public class ACLoadedMods {

    private static boolean distantHorizonsLoaded;
    private static boolean entityCullingLoaded;

    public static void afterAllModsLoaded(){
        distantHorizonsLoaded = ModList.get().isLoaded("distanthorizons");
        entityCullingLoaded = ModList.get().isLoaded("entityculling");
    }

    public static boolean isDistantHorizonsLoaded() {
        return distantHorizonsLoaded;
    }

    public static boolean isEntityCullingLoaded() {
        return entityCullingLoaded;
    }
}

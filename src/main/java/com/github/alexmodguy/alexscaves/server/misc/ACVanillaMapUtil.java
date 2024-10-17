package com.github.alexmodguy.alexscaves.server.misc;

import net.minecraft.world.level.saveddata.maps.MapDecoration;

public class ACVanillaMapUtil {
    public static final MapDecoration.Type UNDERGROUND_CABIN_MAP_DECORATION = MapDecoration.Type.valueOf("AC_UNDERGROUND_CABIN");

    public static byte getMapIconRenderOrdinal(MapDecoration.Type type) {
        return (byte) (type == UNDERGROUND_CABIN_MAP_DECORATION ? 0 : -1);
    }
}

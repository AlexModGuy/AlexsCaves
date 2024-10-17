package com.github.alexmodguy.alexscaves.mixin;

import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(MapDecoration.Type.class)
@Unique
public class MapDecorationTypeMixin {

    @Shadow
    @Final
    @Mutable
    private static MapDecoration.Type[] $VALUES;

    private static final MapDecoration.Type AC_UNDERGROUND_CABIN = ac_addType("AC_UNDERGROUND_CABIN", true, 0X6B6B6B, false);

    @Invoker("<init>")
    public static MapDecoration.Type ac_invokeInit(String internalName, int internalId, boolean renderOnFrame, int mapColor, boolean trackCount) {
        throw new AssertionError();
    }

    private static MapDecoration.Type ac_addType(String internalName, boolean renderOnFrame, int mapColor, boolean trackCount) {
        ArrayList<MapDecoration.Type> variants = new ArrayList<MapDecoration.Type>(Arrays.asList($VALUES));
        MapDecoration.Type instrument = ac_invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, renderOnFrame, mapColor, trackCount);
        variants.add(instrument);
        MapDecorationTypeMixin.$VALUES = variants.toArray(new MapDecoration.Type[0]);
        return instrument;
    }
}

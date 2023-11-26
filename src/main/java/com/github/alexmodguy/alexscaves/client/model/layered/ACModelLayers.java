package com.github.alexmodguy.alexscaves.client.model.layered;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ACModelLayers {

    public static final ModelLayerLocation PRIMORDIAL_ARMOR = createLocation("primordial_armor", "main");
    public static final ModelLayerLocation HAZMAT_ARMOR = createLocation("hazmat_armor", "main");
    public static final ModelLayerLocation DIVING_ARMOR = createLocation("diving_armor", "main");
    public static final ModelLayerLocation DARKNESS_ARMOR = createLocation("darkness_armor", "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PRIMORDIAL_ARMOR, () -> PrimordialArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(HAZMAT_ARMOR, () -> HazmatArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(DIVING_ARMOR, () -> DivingArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(DARKNESS_ARMOR, () -> DarknessArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
    }

    private static ModelLayerLocation createLocation(String model, String layer) {
        return new ModelLayerLocation(new ResourceLocation(AlexsCaves.MODID, model), layer);
    }


}

package com.github.alexmodguy.alexscaves.client.model.layered;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ACModelLayers {

    public static final ModelLayerLocation DIVING_ARMOR = createLocation("diving_armor", "main");

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(DIVING_ARMOR, () -> DivingArmorModel.createArmorLayer(new CubeDeformation(0.5F)));
    }

    private static ModelLayerLocation createLocation(String model, String layer) {
        return new ModelLayerLocation(new ResourceLocation(AlexsCaves.MODID, model), layer);
    }


}

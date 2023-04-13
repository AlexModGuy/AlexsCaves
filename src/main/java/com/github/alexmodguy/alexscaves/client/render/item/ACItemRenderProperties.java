package com.github.alexmodguy.alexscaves.client.render.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ACItemRenderProperties implements IClientItemExtensions {

    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return new ACItemstackRenderer();
    }
}

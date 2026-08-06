package com.github.alexmodguy.alexscaves.client.model.misc;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;

public class HideableModelBoxWithChildren extends AdvancedModelBox {


    public HideableModelBoxWithChildren(AdvancedEntityModel model, String name) {
        super(model, name);
    }

    public HideableModelBoxWithChildren(AdvancedEntityModel model) {
        super(model);
    }

    public HideableModelBoxWithChildren(AdvancedEntityModel model, int textureOffsetX, int textureOffsetY) {
        super(model, textureOffsetX, textureOffsetY);
    }


    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLightIn, int overlay, int color) {
        if (this.showModel) {
            super.render(poseStack, vertexConsumer, packedLightIn, overlay, color);
        } else if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
            poseStack.pushPose();
            this.translateAndRotate(poseStack);
            ObjectListIterator var9 = this.childModels.iterator();
            if (!this.scaleChildren) {
                poseStack.scale(1.0F / Math.max(this.scaleX, 1.0E-4F), 1.0F / Math.max(this.scaleY, 1.0E-4F), 1.0F / Math.max(this.scaleZ, 1.0E-4F));
            }
            while (var9.hasNext()) {
                BasicModelPart lvt_10_1_ = (BasicModelPart) var9.next();
                lvt_10_1_.render(poseStack, vertexConsumer, packedLightIn, overlay, color);
            }
            poseStack.popPose();
        }
    }
}

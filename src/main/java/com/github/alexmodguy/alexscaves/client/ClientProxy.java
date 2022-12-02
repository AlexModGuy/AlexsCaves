package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.baked.BakedModelFinalLayerFullbright;
import com.github.alexmodguy.alexscaves.client.particle.*;
import com.github.alexmodguy.alexscaves.client.render.blockentity.AmbersolBlockRenderer;
import com.github.alexmodguy.alexscaves.client.render.blockentity.MagnetBlockRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.*;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.HeadRotationEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    private static final List<String> FULLBRIGHTS = ImmutableList.of("alexscaves:ambersol#");
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::setupParticles);
    }

    public void clientInit() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::bakeModels);
        BlockEntityRenderers.register(ACBlockEntityRegistry.MAGNET.get(), MagnetBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBERSOL.get(), AmbersolBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MOVING_METAL_BLOCK.get(), MovingMetalBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TELETOR.get(), TeletorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETIC_WEAPON.get(), MagneticWeaponRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETRON.get(), MagnetronRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SUBTERRANODON.get(), SubterranodonRenderer::new);
        EntityRenderers.register(ACEntityRegistry.VALLUMRAPTOR.get(), VallumraptorRenderer::new);
        Sheets.addWoodType(ACBlockRegistry.PEWEN_WOOD_TYPE);
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsCaves.LOGGER.debug("Registered particle factories");
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.AzureFactory());
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_FLOW.get(), new MagneticFlowParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_FLOW.get(), new MagneticFlowParticle.AzureFactory());
        registry.register(ACParticleRegistry.GALENA_DEBRIS.get(), GalenaDebrisParticle.Factory::new);
        registry.register(ACParticleRegistry.FLY.get(), FlyParticle.Factory::new);
    }

    @SubscribeEvent
    public void setupEntityRotations(EventLivingRenderer.SetupRotations event) {
        if (event.getEntity() instanceof MagneticEntityAccessor magnetic) {
            float width = event.getEntity().getBbHeight();
            float height = event.getEntity().getBbHeight();
            float progress = magnetic.getAttachmentProgress(event.getPartialTicks());
            float prevProg = 1F - progress;
            float bodyRot = 180.0F - event.getBodyYRot();
            if (magnetic.getMagneticAttachmentFace().getAxis() != Direction.Axis.Y) {
                event.getPoseStack().mulPose(Vector3f.YN.rotationDegrees(1F * bodyRot));
            }
            rotateForAngle(event.getEntity(), event.getPoseStack(), magnetic.getPrevMagneticAttachmentFace(), prevProg, width, height);
            rotateForAngle(event.getEntity(), event.getPoseStack(), magnetic.getMagneticAttachmentFace(), progress, width, height);
        }
    }

    @SubscribeEvent
    public void preRenderLiving(RenderLivingEvent.Pre event) {
        if (event.getEntity() instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.setMagnetHeadRotation();
        }
    }

    @SubscribeEvent
    public void postRenderLiving(RenderLivingEvent.Post event) {
        if (event.getEntity() instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.resetMagnetHeadRotation();
        }
    }

    @SubscribeEvent
    public void postRenderStage(RenderLevelStageEvent event) {
        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS){
            RenderSystem.runAsFancy(() -> AmbersolBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void fogRenderColor(ViewportEvent.ComputeFogColor event){
    }

    private void rotateForAngle(LivingEntity entity, PoseStack matrixStackIn, Direction rotate, float f, float width, float height) {
        boolean down = entity.zza < 0.0F;
        switch (rotate) {
            case DOWN:
                break;
            case UP:
                matrixStackIn.translate(0.0D, height * f, 0.0D);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-180.0F * f));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-180.0F * f));
                break;
            case NORTH:
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F * f));
                }
                break;
            case SOUTH:
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180 * f));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F * f));
                }
                break;
            case WEST:
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90 * f));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F * f));
                }
                break;
            case EAST:
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-90 * f));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F * f));
                }
                break;
        }
    }

    private void bakeModels(final ModelEvent.BakingCompleted e) {
        for (ResourceLocation id : e.getModels().keySet()) {
            if (FULLBRIGHTS.contains(id.toString())) {
                e.getModels().put(id, new BakedModelFinalLayerFullbright(e.getModels().get(id)));
            }
        }
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

}

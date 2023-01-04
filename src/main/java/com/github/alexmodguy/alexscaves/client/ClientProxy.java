package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.model.baked.BakedModelFinalLayerFullbright;
import com.github.alexmodguy.alexscaves.client.particle.*;
import com.github.alexmodguy.alexscaves.client.render.ACInternalShaders;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.blockentity.AmbersolBlockRenderer;
import com.github.alexmodguy.alexscaves.client.render.blockentity.MagnetBlockRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.*;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.ACBlockEntityRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorsaurusEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.HeadRotationEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.client.event.EventLivingRenderer;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {

    private static final List<String> FULLBRIGHTS = ImmutableList.of("alexscaves:ambersol#", "alexscaves:radrock_uranium_ore#");
    public static final ResourceLocation POTION_EFFECT_HUD_OVERLAYS = new ResourceLocation(AlexsCaves.MODID, "textures/misc/potion_effect_hud_overlays.png");
    public static final ResourceLocation BOMB_FLASH = new ResourceLocation(AlexsCaves.MODID, "textures/misc/bomb_flash.png");
    protected final RandomSource random = RandomSource.create();
    private int lastTremorTick = -1;
    private int updateCounter = 0;
    private int playerHealth = 0;
    private long lastSystemTime;
    private float[] randomTremorOffsets = new float[3];
    private List<UUID> blockedEntityRenders = new ArrayList<>();
    public int renderNukeFlashFor = 0;

    private float prevNukeFlashAmount = 0;
    private float nukeFlashAmount = 0;
    public boolean renderNukeSkyDark = false;

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientProxy::setupParticles);
    }

    public void clientInit() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::bakeModels);
        bus.addListener(this::registerShaders);
        BlockEntityRenderers.register(ACBlockEntityRegistry.MAGNET.get(), MagnetBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBERSOL.get(), AmbersolBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MOVING_METAL_BLOCK.get(), MovingMetalBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TELETOR.get(), TeletorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETIC_WEAPON.get(), MagneticWeaponRenderer::new);
        EntityRenderers.register(ACEntityRegistry.MAGNETRON.get(), MagnetronRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BOUNDROID.get(), BoundroidRenderer::new);
        EntityRenderers.register(ACEntityRegistry.BOUNDROID_WINCH.get(), BoundroidWinchRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FERROUSLIME.get(), FerrouslimeRenderer::new);
        EntityRenderers.register(ACEntityRegistry.SUBTERRANODON.get(), SubterranodonRenderer::new);
        EntityRenderers.register(ACEntityRegistry.VALLUMRAPTOR.get(), VallumraptorRenderer::new);
        EntityRenderers.register(ACEntityRegistry.GROTTOCERATOPS.get(), GrottoceratopsRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TRILOCARIS.get(), TrilocarisRenderer::new);
        EntityRenderers.register(ACEntityRegistry.TREMORSAURUS.get(), TremorsaurusRenderer::new);
        EntityRenderers.register(ACEntityRegistry.RELICHEIRUS.get(), RelicheirusRenderer::new);
        EntityRenderers.register(ACEntityRegistry.FALLING_TREE_BLOCK.get(), FallingTreeBlockRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEAR_EXPLOSION.get(), NuclearExplosionRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEAR_BOMB.get(), NuclearBombRenderer::new);
        EntityRenderers.register(ACEntityRegistry.NUCLEEPER.get(), NucleeperRenderer::new);
        EntityRenderers.register(ACEntityRegistry.RADGILL.get(), RadgillRenderer::new);
        Sheets.addWoodType(ACBlockRegistry.PEWEN_WOOD_TYPE);
    }

    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsCaves.LOGGER.debug("Registered particle factories");
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get(), new MagneticOrbitParticle.AzureFactory());
        registry.register(ACParticleRegistry.SCARLET_MAGNETIC_FLOW.get(), new MagneticFlowParticle.ScarletFactory());
        registry.register(ACParticleRegistry.AZURE_MAGNETIC_FLOW.get(), new MagneticFlowParticle.AzureFactory());
        registry.register(ACParticleRegistry.GALENA_DEBRIS.get(), GalenaDebrisParticle.Factory::new);
        registry.register(ACParticleRegistry.FERROUSLIME.get(), FerrouslimeParticle.Factory::new);
        registry.register(ACParticleRegistry.FLY.get(), FlyParticle.Factory::new);
        registry.register(ACParticleRegistry.WATER_TREMOR.get(), WaterTremorParticle.Factory::new);
        registry.register(ACParticleRegistry.ACID_BUBBLE.get(), AcidBubbleParticle.Factory::new);
        registry.register(ACParticleRegistry.BLACK_VENT_SMOKE.get(), VentSmokeParticle.BlackFactory::new);
        registry.register(ACParticleRegistry.WHITE_VENT_SMOKE.get(), VentSmokeParticle.WhiteFactory::new);
        registry.register(ACParticleRegistry.GREEN_VENT_SMOKE.get(), VentSmokeParticle.GreenFactory::new);
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD.get(), new MushroomCloudParticle.Factory());
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD_SMOKE.get(), MushroomCloudEffectParticle.Factory::new);
        registry.register(ACParticleRegistry.MUSHROOM_CLOUD_EXPLOSION.get(), MushroomCloudEffectParticle.Factory::new);
        registry.register(ACParticleRegistry.NUCLEAR_BOMB.get(), NuclearBombParticle.Factory::new);
        registry.register(ACParticleRegistry.RADGILL_SPLASH.get(), RadgillSplashParticle.Factory::new);
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
                event.getPoseStack().mulPose(Axis.YN.rotationDegrees(1F * bodyRot));
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
        if (blockedEntityRenders.contains(event.getEntity().getUUID())) {
            event.setCanceled(true);
            blockedEntityRenders.remove(event.getEntity().getUUID());
            MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(event.getEntity(), event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
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
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && AlexsCaves.CLIENT_CONFIG.ambersolShines.get()) {
            RenderSystem.runAsFancy(() -> AmbersolBlockRenderer.renderEntireBatch(event.getLevelRenderer(), event.getPoseStack(), event.getRenderTick(), event.getCamera(), event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && AlexsCaves.CLIENT_CONFIG.screenShaking.get()) {
            float tremorAmount = renderNukeSkyDark ? 1.5F : 0;
            double shakeDistanceScale = 20D;
            double distance = Double.MAX_VALUE;
            float partialTick = Minecraft.getInstance().getPartialTick();
            if (player.isOnGround() && tremorAmount == 0) {
                AABB aabb = player.getBoundingBox().inflate(shakeDistanceScale);
                for (TremorsaurusEntity tremorsaurus : Minecraft.getInstance().level.getEntitiesOfClass(TremorsaurusEntity.class, aabb)) {
                    tremorAmount = (1F - (float) (distance / shakeDistanceScale)) * Math.max(tremorsaurus.getScreenShakeAmount(partialTick), 0);
                    if (tremorsaurus.distanceTo(player) < distance) {
                        distance = tremorsaurus.distanceTo(player);
                    }
                }
            }
            if (tremorAmount > 0) {
                if (lastTremorTick != player.tickCount) {
                    RandomSource rng = player.getLevel().random;
                    randomTremorOffsets[0] = rng.nextFloat();
                    randomTremorOffsets[1] = rng.nextFloat();
                    randomTremorOffsets[2] = rng.nextFloat();
                    lastTremorTick = player.tickCount;
                }
                double intensity = tremorAmount * Minecraft.getInstance().options.screenEffectScale().get();
                event.getCamera().move(randomTremorOffsets[0] * 0.2F * intensity, randomTremorOffsets[1] * 0.2F * intensity, randomTremorOffsets[2] * 0.5F * intensity);
            }
        }
    }

    @SubscribeEvent
    public void onPostRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = getClientSidePlayer();
        if (event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id()) && Minecraft.getInstance().gameMode.canHurtPlayer() && Minecraft.getInstance().getCameraEntity() instanceof Player && player.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
            int leftHeight = 39;
            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();
            int health = Mth.ceil(player.getHealth());
            if (health < this.playerHealth && player.invulnerableTime > 0) {
                this.lastSystemTime = System.currentTimeMillis();
            } else if (health > this.playerHealth && player.invulnerableTime > 0) {
                this.lastSystemTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - this.lastSystemTime > 1000L) {
                this.playerHealth = health;
                this.lastSystemTime = System.currentTimeMillis();
            }

            this.playerHealth = health;
            AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            float healthMax = (float) attrMaxHealth.getValue();
            float absorb = Mth.ceil(player.getAbsorptionAmount());

            int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
            int rowHeight = Math.max(10 - (healthRows - 2), 3);

            this.random.setSeed((long) (updateCounter * 312871));

            int left = width / 2 - 91;
            int top = height - leftHeight;
            int regen = -1;
            if (player.hasEffect(MobEffects.REGENERATION)) {
                regen = updateCounter % 25;
            }
            final int heartV = player.level.getLevelData().isHardcore() ? 9 : 0;
            int heartU = 0;
            float absorbRemaining = absorb;
            event.getPoseStack().pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, POTION_EFFECT_HUD_OVERLAYS);
            for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
                int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += random.nextInt(2);
                }
                if (i == regen) {
                    y -= 2;
                }
                GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU, heartV + 18, 9, 9, 32, 32);
                if (absorbRemaining > 0.0F) {
                    if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 1.0F;
                    } else {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                        absorbRemaining -= 2.0F;
                    }
                } else {
                    if (i * 2 + 1 < health) {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU, heartV, 9, 9, 32, 32);
                    } else if (i * 2 + 1 == health) {
                        GuiComponent.blit(event.getPoseStack(), x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                    }
                }
            }
            event.getPoseStack().popPose();
        }
    }

    @SubscribeEvent
    public void fogRender(ViewportEvent.RenderFog event) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        FluidState fluidstate = player.level.getFluidState(event.getCamera().getBlockPosition());
        if (!fluidstate.isEmpty() && fluidstate.getType().getFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get())) {
            event.setCanceled(true);
            event.setFarPlaneDistance(10.0F);
            event.setNearPlaneDistance(0.0F);
        } else if (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float nearness;
            if (i == 0) {
                nearness = ACBiomeRegistry.getBiomeFogNearness(player.level.getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeFogNearness(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                nearness = (float) vec31.x;
            }
            if (nearness != 1.0F) {
                event.setCanceled(true);
                event.setNearPlaneDistance(event.getNearPlaneDistance() * nearness);
            }
        }
    }

    @SubscribeEvent
    public void fogRender(ViewportEvent.ComputeFogColor event) {
        Entity player = Minecraft.getInstance().player;
        if (player.getEyeInFluidType() != null && player.getEyeInFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get())) {
            event.setRed((float) (0));
            event.setGreen((float) (1));
            event.setBlue((float) (0));
        } else if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            int i = Minecraft.getInstance().options.biomeBlendRadius().get();
            float override;
            if (i == 0) {
                override = ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiome(player.blockPosition()));
            } else {
                Vec3 vec31 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                    return new Vec3(ACBiomeRegistry.getBiomeSkyOverride(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z)), 0, 0);
                });
                override = (float) vec31.x;
            }
            if (override != 0.0F) {

                Vec3 vec3;
                if (i == 0) {
                    vec3 = ((ClientLevel) player.level).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getFogColor()), override);
                } else {
                    vec3 = CubicSampler.gaussianSampleVec3(player.position(), (x, y, z) -> {
                        return ((ClientLevel) player.level).effects().getBrightnessDependentFogColor(Vec3.fromRGB24(player.level.getBiomeManager().getNoiseBiomeAtPosition(x, y, z).value().getFogColor()), override);
                    });
                }
                event.setRed((float) (vec3.x));
                event.setGreen((float) (vec3.y));
                event.setBlue((float) (vec3.z));
            }
        }
    }

    private void rotateForAngle(LivingEntity entity, PoseStack matrixStackIn, Direction rotate, float f, float width, float height) {
        boolean down = entity.zza < 0.0F;
        switch (rotate) {
            case DOWN:
                break;
            case UP:
                matrixStackIn.translate(0.0D, height * f, 0.0D);
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180.0F * f));
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-180.0F * f));
                break;
            case NORTH:
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case SOUTH:
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(180 * f));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case WEST:
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(90 * f));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case EAST:
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-90 * f));
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                matrixStackIn.translate(0.0D, -height * 0.2f * f, 0.0D);
                if (down) {
                    matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
        }
    }

    private void bakeModels(final ModelEvent.ModifyBakingResult e) {
        for (ResourceLocation id : e.getModels().keySet()) {
            if (FULLBRIGHTS.contains(id.toString())) {
                e.getModels().put(id, new BakedModelFinalLayerFullbright(e.getModels().get(id)));
            }
        }
    }

    private void registerShaders(final RegisterShadersEvent e) {
        try {
            e.registerShader(new ShaderInstance(e.getResourceProvider(), new ResourceLocation(AlexsCaves.MODID, "rendertype_ferrouslime_gel"), DefaultVertexFormat.NEW_ENTITY), ACInternalShaders::setRenderTypeFerrouslimeGelShader);
            AlexsCaves.LOGGER.info("registered internal shaders");
        } catch (IOException exception) {
            AlexsCaves.LOGGER.error("could not register internal shaders");
            exception.printStackTrace();
        }
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }


    public void blockRenderingEntity(UUID id) {
        blockedEntityRenders.add(id);
    }

    public void releaseRenderingEntity(UUID id) {
        blockedEntityRenders.remove(id);
    }

    public void setVisualFlag(int flag) {
    }

    public float getNukeFlashAmount(float partialTicks){
        return prevNukeFlashAmount + (nukeFlashAmount - prevNukeFlashAmount) * partialTicks;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            prevNukeFlashAmount = nukeFlashAmount;
            if (renderNukeFlashFor > 0) {
                if (nukeFlashAmount < 1F) {
                    nukeFlashAmount = Math.min(nukeFlashAmount + 0.4F, 1F);
                }
                renderNukeFlashFor--;
            } else if (nukeFlashAmount > 0F) {
                nukeFlashAmount = Math.max(nukeFlashAmount - 0.05F, 0F);
            }
        }
    }
}


package com.github.alexmodguy.alexscaves.client.render.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaveMapRenderer {

    private static final Map<ItemStack, CaveMapRenderer> CAVE_MAPS = new HashMap<>();
    public static final RenderType MAP_BACKGROUND = RenderType.text(new ResourceLocation("textures/map/map_background.png"));
    public static final RenderType CAVE_MAP_PLAYER_TEXTURE = RenderType.text(new ResourceLocation(AlexsCaves.MODID, "textures/misc/map/cave_map_player.png"));
    public static final RenderType CAVE_MAP_PLAYER_DIRECTION_TEXTURE = RenderType.text(new ResourceLocation(AlexsCaves.MODID, "textures/misc/map/cave_map_player_direction.png"));
    private final RenderType renderType;

    public BlockPos target;
    private final DynamicTexture texture;
    public List<BiomeLabel> labels = new ArrayList<>();
    public final int[] mapBiomes;

    public CaveMapRenderer(BlockPos target, int[] mapBiomes, long seed) {
        this.target = target;
        this.mapBiomes = mapBiomes;
        this.texture = new DynamicTexture(128, 128, true);
        ResourceLocation resourcelocation = Minecraft.getInstance().textureManager.register("cave_map/" + CAVE_MAPS.size(), this.texture);
        this.renderType = RenderType.text(resourcelocation);
        updateTexture();
        updateLabels(new LegacyRandomSource(seed));
    }

    public static CaveMapRenderer getMapFor(ItemStack item) {
        if (CAVE_MAPS.containsKey(item)) {
            return CAVE_MAPS.get(item);
        } else {
            CaveMapRenderer mapRenderer = new CaveMapRenderer(CaveMapItem.getBiomeBlockPos(item), CaveMapItem.getBiomes(item), CaveMapItem.getSeed(item));
            CAVE_MAPS.put(item, mapRenderer);
            return mapRenderer;
        }
    }

    private void updateTexture() {
        Registry<Biome> registry = Minecraft.getInstance().level.registryAccess().registry(Registries.BIOME).orElse(null);
        if (registry != null && mapBiomes.length >= 128 * 128) {
            for (int i = 0; i < 128; ++i) {
                for (int j = 0; j < 128; ++j) {
                    int k = j + i * 128;
                    int biomeId = mapBiomes[k];
                    int biomeColor = getBiomeColor(registry.asHolderIdMap().byId(biomeId), j, i);
                    int r = FastColor.ABGR32.red(biomeColor);
                    int g = FastColor.ABGR32.green(biomeColor);
                    int b = FastColor.ABGR32.blue(biomeColor);
                    double edge = Math.sqrt((i - 64) * (i - 64) + (j - 64) * (j - 64)) / 128;
                    int alpha = Math.max(255 - (int) (255F * edge), 10);
                    this.texture.getPixels().setPixelRGBA(j, i, FastColor.ABGR32.color(alpha, b, g, r));
                }
            }
        }
        this.texture.upload();
    }

    private void updateLabels(LegacyRandomSource random) {
        labels.clear();
        int extraBiomes = random.nextInt(3) + 3;
        Registry<Biome> registry = Minecraft.getInstance().level.registryAccess().registry(Registries.BIOME).orElse(null);
        if (registry != null && mapBiomes.length >= 128 * 128) {
            Pair<Integer, Integer> targetBiomeLoc = centerBiomeCoordinates(64, 64);

            BiomeLabel centerLabel = buildLabelFrom(targetBiomeLoc, registry, (targetBiomeLoc.getFirst() - 64) / 2);
            labels.add(centerLabel);
            for (int i = 0; i < extraBiomes; i++) {
                Vec3 randomOffsetFromCenterVec = new Vec3(0, 0, random.nextInt(20) + 40).yRot((float) Math.toRadians((360 / (float) extraBiomes) * i + random.nextFloat() * 40));
                int offsetX = Mth.clamp(centerLabel.x() + (int) randomOffsetFromCenterVec.x, 10, 118);
                int offsetY = Mth.clamp(centerLabel.y() + (int) randomOffsetFromCenterVec.z, 10, 118);
                Pair<Integer, Integer> extraBiomeLoc = centerBiomeCoordinates(offsetX, offsetY);
                BiomeLabel builtLabel = buildLabelFrom(extraBiomeLoc, registry, (extraBiomeLoc.getFirst() - 64) / 2);
                if (!builtLabel.biome.location().equals(centerLabel.biome.location())) {
                    labels.add(builtLabel);
                }
            }
        }
    }

    private BiomeLabel buildLabelFrom(Pair<Integer, Integer> targetBiomeLoc, Registry<Biome> registry, int rotation) {
        int k = targetBiomeLoc.getFirst() + targetBiomeLoc.getSecond() * 128;
        int biomeId = mapBiomes[k];
        ResourceKey<Biome> biomeResourceKey = registry.asHolderIdMap().byId(biomeId).unwrapKey().get();
        return new BiomeLabel(biomeResourceKey, targetBiomeLoc.getFirst(), targetBiomeLoc.getSecond(), rotation);
    }

    private Pair<Integer, Integer> centerBiomeCoordinates(int xIn, int yIn) {
        int colorFor = mapBiomes[xIn + yIn * 128];
        int farLeftX = xIn;
        int farRightX = xIn;
        int farUpY = yIn;
        int farDownY = yIn;
        while (farLeftX > 0) {
            if (mapBiomes[farLeftX + yIn * 128] != colorFor) {
                break;
            }
            farLeftX--;
        }
        while (farRightX < 128) {
            if (mapBiomes[farRightX + yIn * 128] != colorFor) {
                break;
            }
            farRightX++;
        }
        while (farDownY > 0) {
            if (mapBiomes[xIn + farDownY * 128] != colorFor) {
                break;
            }
            farDownY--;
        }
        while (farUpY < 128) {
            if (mapBiomes[xIn + farUpY * 128] != colorFor) {
                break;
            }
            farUpY++;
        }
        return new Pair<>((farLeftX + farRightX) / 2, (farUpY + farDownY) / 2);
    }

    private int getBiomeColor(Holder<Biome> biome, int u, int v) {
        if(biome.is(Tags.Biomes.IS_WATER)){
            return 0XFF0000 * biome.get().getWaterColor();
        }
        if(biome.is(Tags.Biomes.IS_SNOWY)){
            return 0XEEEEEE;
        }
        if (biome.is(ACBiomeRegistry.MAGNETIC_CAVES)) {
            return DefaultMapBackgrounds.getMapColor(0, u, v);
        }
        if (biome.is(ACBiomeRegistry.PRIMORDIAL_CAVES)) {
            return DefaultMapBackgrounds.getMapColor(1, u, v);
        }
        if (biome.is(ACBiomeRegistry.TOXIC_CAVES)) {
            return DefaultMapBackgrounds.getMapColor(2, u, v);
        }
        if (biome.is(ACBiomeRegistry.ABYSSAL_CHASM)) {
            return DefaultMapBackgrounds.getMapColor(3, u, v);
        }
        int foliage = biome.get().getFoliageColor();
        return foliage;
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, ItemStack map, boolean fullFrame, int light) {
        Matrix4f matrix4f = poseStack.last().pose();
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(this.renderType);
        vertexconsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
        vertexconsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();

        for (BiomeLabel label : labels) {
            poseStack.pushPose();
            Font font = Minecraft.getInstance().font;
            Component component = Component.translatable(getBiomeString(label.biome.location().toString()));
            float f6 = (float) font.width(component);
            float distFromCenter = (float) Math.sqrt((label.x() - 64) * (label.x() - 64) + (label.y() - 64) * (label.y() - 64));
            float clampedSize = (float) Mth.clamp((128 - distFromCenter) / 128, 0.5F, 1F);
            float f7 = 1F * clampedSize;
            poseStack.translate(0.0F + (float) label.x() - (f6 * f7) / 2.0F, (float) label.y(), -0.025F);
            poseStack.scale(f7, f7, -1.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(label.rotation()));
            font.drawInBatch(component, 0.0F, 0.0F, 0XFFFFFF, true, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, 0, light);
            poseStack.popPose();

        }
        poseStack.pushPose();
        float playerX = (float) (Minecraft.getInstance().player.getX() - (double) this.target.getX()) / (float) CaveMapItem.MAP_SCALE;
        float playerZ = (float) (Minecraft.getInstance().player.getZ() - (double) this.target.getZ()) / (float) CaveMapItem.MAP_SCALE;
        float renderPlayerX = Mth.clamp(playerX + 64.0F, 0, 128);
        float renderPlayerZ = Mth.clamp(playerZ + 64.0F, 0, 128);
        poseStack.translate(renderPlayerX, renderPlayerZ, -0.02F);
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) Minecraft.getInstance().player.getYRot() + 180));
        renderDetail(multiBufferSource.getBuffer(CAVE_MAP_PLAYER_DIRECTION_TEXTURE), poseStack, 3, light, 6);
        poseStack.popPose();
        renderDetail(multiBufferSource.getBuffer(CAVE_MAP_PLAYER_TEXTURE), poseStack, 4, light, 4);
        poseStack.popPose();
    }

    private String getBiomeString(String id) {
        return "biome." + id.replace(":", ".");
    }

    private void renderDetail(VertexConsumer vertexconsumer1, PoseStack poseStack, int yOffset, int light, float scale) {
        Matrix4f matrix4f1 = poseStack.last().pose();
        vertexconsumer1.vertex(matrix4f1, -1.0F * scale, 1.0F * scale, (float) yOffset * -0.001F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer1.vertex(matrix4f1, 1.0F * scale, 1.0F * scale, (float) yOffset * -0.001F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        vertexconsumer1.vertex(matrix4f1, 1.0F * scale, -1.0F * scale, (float) yOffset * -0.001F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
        vertexconsumer1.vertex(matrix4f1, -1.0F * scale, -1.0F * scale, (float) yOffset * -0.001F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();

    }

    private record BiomeLabel(ResourceKey<Biome> biome, int x, int y, int rotation) {
    }

    ;
}

package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Set;

public class GingerbreadHousePiece extends UndergroundTemplateStructurePiece {

    public static final ResourceLocation TOWN_CENTER_TEMPLATE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_town_center");
    public static final ResourceLocation[] HOUSE_TEMPLATES = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_0"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_1"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_2"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_3"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_4"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_5"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_6"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_7"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_8"),
            ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "gingerbread_house_9")
    };

    public GingerbreadHousePiece(StructureTemplateManager manager, ResourceLocation resourceLocation, BlockPos pos, Rotation rotation) {
        super(ACStructurePieceRegistry.GINGERBREAD_HOUSE.get(), 0, manager, resourceLocation, resourceLocation.toString(), makeSettings(rotation), pos);
    }

    public GingerbreadHousePiece(StructureTemplateManager manager, CompoundTag tag) {
        super(ACStructurePieceRegistry.GINGERBREAD_HOUSE.get(), tag, manager, (x) -> {
            return makeSettings(Rotation.valueOf(tag.getString("Rotation")));
        });
    }

    public GingerbreadHousePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(context.structureTemplateManager(), tag);
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return (new StructurePlaceSettings()).setRotation(rotation).setIgnoreEntities(true).setKeepLiquids(false);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rotation", this.placeSettings.getRotation().name());
    }

    @Override
    public int minimumDepthBeneathSurface() {
        return 35;
    }

    @Override
    protected void handleDataMarker(String string, BlockPos pos, ServerLevelAccessor accessor, RandomSource random, BoundingBox box) {
        if (string.equals("loot_chest")) {
            RandomizableContainerBlockEntity.setLootTable(accessor, random, pos.below(), ACLootTableRegistry.GINGERBREAD_TOWN_CHEST);
        }
        accessor.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 10);
        for(Direction neighborDir : ACMath.HORIZONTAL_DIRECTIONS){
            BlockPos offset = pos.relative(neighborDir);
            if(accessor.getBlockState(offset).is(BlockTags.WALLS)){
                accessor.getChunk(offset).markPosForPostprocessing(offset);
            }
        }
        accessor.blockUpdated(pos, accessor.getBlockState(pos).getBlock());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos structurePos) {
        super.postProcess(level, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, structurePos);
        BlockPos center = this.boundingBox.getCenter().atY(this.boundingBox.minY());
        BoundingBox box = new BoundingBox(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.minY() + 1, this.boundingBox.maxZ());
        Set<BlockPos> supportsNeededBelow = Sets.newHashSet();
        BlockPos.betweenClosedStream(box).forEach((pos) -> {
            if (!level.getBlockState(pos).isAir()) {
                if (!level.getFluidState(pos.below()).isEmpty() || !level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below())) {
                    supportsNeededBelow.add(pos.immutable());
                }
            }
        });
        Direction roadDirection = this.getRotation().rotate(Direction.NORTH);
        BlockPos endOfRoad = center.relative(roadDirection, Math.max(this.getBoundingBox().getXSpan(), this.getBoundingBox().getZSpan()) / 2);
        BlockPos.betweenClosedStream(center, endOfRoad).forEach((pos) -> {
            BlockPos roadPos = getCaveHeight(pos, level, randomSource).below();
            if(boundingBox.isInside(roadPos.atY(boundingBox.minY())) && level.getBlockState(roadPos).is(ACTagRegistry.GINGERBREAD_TOWN_GEN_REPLACEABLES)){
                level.setBlock(roadPos, ACBlockRegistry.GINGERBREAD_BRICKS.get().defaultBlockState(), 3);
            }
        });
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos grounded = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos groundedInBox = new BlockPos.MutableBlockPos();
        for (BlockPos pos : supportsNeededBelow) {
            grounded.set(pos.getX(), pos.getY() - 1, pos.getZ());
            above.set(pos.getX(), pos.getY(), pos.getZ());
            groundedInBox.set(pos.getX(), boundingBox.minY() + 1, pos.getZ());
            if(boundingBox.isInside(groundedInBox)){
                int yDown = 0;
                int maxYDown = 3 + randomSource.nextInt(3);
                while ((!level.getBlockState(grounded).getFluidState().isEmpty() || !level.getBlockState(grounded).isCollisionShapeFullBlock(level, grounded)) && grounded.getY() > level.getMinBuildHeight() && yDown < maxYDown) {
                    if(yDown == 0 && !level.getBlockState(above).isCollisionShapeFullBlock(level, above)){
                        level.setBlock(grounded, ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get().defaultBlockState(), 3);
                        level.setBlock(grounded.below(), ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState(), 3);
                    }else{
                        level.setBlock(grounded, yDown < maxYDown * 0.4F ? ACBlockRegistry.BLOCK_OF_CHOCOLATE.get().defaultBlockState() : ACBlockRegistry.CAKE_LAYER.get().defaultBlockState(), 3);
                    }
                    grounded.move(0, -1, 0);
                    yDown++;
                }
            }
        }
    }

    public void checkedSetBlock(WorldGenLevel level, BlockPos position, BlockState state) {
        if (this.getBoundingBox().isInside(position)) {
            level.setBlock(position, state, 128);
        }
    }

    @Override
    public boolean placeInHighestCave() {
        return true;
    }

    @Override
    public boolean discardIfNotOnGround() {
        return true;
    }

    @Override
    public boolean sinkByEdges() {
        return false;
    }

    @Override
    protected BlockPos getCaveHeight(BlockPos currentStructureCenter, WorldGenLevel level, RandomSource randomSource) {
        return currentStructureCenter.atY((int) CakeCaveStructurePiece.calculatePlateauHeight(currentStructureCenter.getX(), currentStructureCenter.getZ(), 7, true));
    }

    public int moveDownBy() {
        return 1;
    }
}

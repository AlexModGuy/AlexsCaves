package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.MusselBlock;
import com.github.alexmodguy.alexscaves.server.level.feature.config.WhalefallFeatureConfiguration;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.WhalefallProcessor;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class WhalefallFeature extends Feature<WhalefallFeatureConfiguration> {

    public WhalefallFeature(Codec<WhalefallFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WhalefallFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
        BlockPos.MutableBlockPos trenchBottom = new BlockPos.MutableBlockPos();
        trenchBottom.set(context.origin());
        while (!level.getBlockState(trenchBottom).getFluidState().isEmpty() && trenchBottom.getY() > level.getMinBuildHeight()) {
            trenchBottom.move(0, -1, 0);
        }
        if (level.getBlockState(trenchBottom.below()).is(ACBlockRegistry.MUCK.get())) {
            Rotation rotation = Rotation.getRandom(randomsource);
            ResourceLocation head = context.config().headStructures.get(randomsource.nextInt(context.config().headStructures.size()));
            ResourceLocation body = context.config().bodyStructures.get(randomsource.nextInt(context.config().bodyStructures.size()));
            ResourceLocation tail = context.config().tailStructures.get(randomsource.nextInt(context.config().tailStructures.size()));
            Direction direction = rotation.rotate(Direction.SOUTH);
            Direction bendTo = randomsource.nextBoolean() ? direction.getClockWise() : direction.getCounterClockWise();
            StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();
            BlockPos startAt = trenchBottom.immutable().relative(direction.getOpposite(), 5);
            int i = generateStructurePiece(level, structuretemplatemanager, rotation, randomsource, head, startAt, false);
            int bendToDist = randomsource.nextInt(2);
            int j = generateStructurePiece(level, structuretemplatemanager, rotation, randomsource, body, startAt.relative(direction, i + randomsource.nextInt(1)).relative(bendTo, bendToDist), true) + 1;
            generateStructurePiece(level, structuretemplatemanager, rotation, randomsource, tail, startAt.relative(direction, i + j + randomsource.nextInt(1)).relative(bendTo, bendToDist + randomsource.nextInt(2)), true);
            return true;
        }
        return false;
    }

    private int generateStructurePiece(WorldGenLevel level, StructureTemplateManager structuretemplatemanager, Rotation rotation, RandomSource randomsource, ResourceLocation head, BlockPos blockpos, boolean gravity) {
        StructureTemplate structuretemplate = structuretemplatemanager.getOrCreate(head);
        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(rotation).setRandom(randomsource).addProcessor(gravity ? WhalefallProcessor.INSTANCE_GRAVITY : WhalefallProcessor.INSTANCE_NO_GRAVITY);
        Vec3i defaultSize = structuretemplate.getSize(Rotation.NONE);
        Vec3i rotatedSize = structuretemplate.getSize(rotation);
        BlockPos blockpos1 = blockpos.offset(-rotatedSize.getX() / 2, 0, -rotatedSize.getZ() / 2);
        BlockPos blockpos2 = structuretemplate.getZeroPositionWithTransform(blockpos1, Mirror.NONE, rotation);
        structuretemplate.placeInWorld(level, blockpos2, blockpos2, structureplacesettings, randomsource, 18);
        BoundingBox decorateBox = structuretemplate.getBoundingBox(structureplacesettings, blockpos2).inflatedBy(1);
        BlockPos.betweenClosedStream(decorateBox).forEach(pos -> decorateWhalefallPos(pos, level, randomsource));
        return Math.abs(defaultSize.getZ());
    }

    private void decorateWhalefallPos(BlockPos pos, WorldGenLevel worldgenlevel, RandomSource randomsource) {
        Direction dir = Direction.getRandom(randomsource);
        BlockPos blockpos2 = pos.relative(dir);
        if (worldgenlevel.getBlockState(blockpos2).is(ACTagRegistry.WHALEFALL_IGNORES) || randomsource.nextInt(5) == 0) {
            BlockState replaceAt = worldgenlevel.getBlockState(pos);
            if ((replaceAt.is(Blocks.WATER) || replaceAt.isAir()) && worldgenlevel.getBlockState(blockpos2).isFaceSturdy(worldgenlevel, blockpos2, dir.getOpposite())) {
                boolean waterlog = worldgenlevel.getFluidState(pos).is(FluidTags.WATER);
                if (randomsource.nextBoolean()) {
                    worldgenlevel.setBlock(pos, ACBlockRegistry.BONE_WORMS.get().defaultBlockState().setValue(MusselBlock.FACING, dir.getOpposite()).setValue(MusselBlock.WATERLOGGED, waterlog), 3);
                } else {
                    worldgenlevel.setBlock(pos, ACBlockRegistry.MUSSEL.get().defaultBlockState().setValue(MusselBlock.FACING, dir.getOpposite()).setValue(MusselBlock.WATERLOGGED, waterlog).setValue(MusselBlock.MUSSELS, 1 + randomsource.nextInt(4)), 3);
                }
            }
        }
    }
}

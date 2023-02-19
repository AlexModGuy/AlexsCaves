package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.level.feature.config.WhalefallFeatureConfiguration;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.ACStructureProcessorRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.WhalefallProcessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
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
        while(!level.getBlockState(trenchBottom).getFluidState().isEmpty() && trenchBottom.getY() > level.getMinBuildHeight()){
            trenchBottom.move(0, -1, 0);
        }
        if(!level.getBlockState(trenchBottom.below()).is(Blocks.TUFF)){
            Rotation rotation = Rotation.getRandom(randomsource);
            ResourceLocation head = context.config().headStructures.get(randomsource.nextInt(context.config().headStructures.size()));
            ResourceLocation body = context.config().bodyStructures.get(randomsource.nextInt(context.config().bodyStructures.size()));
            ResourceLocation tail = context.config().tailStructures.get(randomsource.nextInt(context.config().tailStructures.size()));
            Direction direction = rotation.rotate(Direction.SOUTH);
            Direction bendTo = randomsource.nextBoolean() ? direction.getClockWise() : direction.getCounterClockWise();
            StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();
            BlockPos startAt = trenchBottom.immutable().relative(direction.getOpposite(), 5);
            int i = generateStructurePiece(level, structuretemplatemanager, direction, rotation, randomsource, head, startAt);
            int bendToDist = randomsource.nextInt(2);
            int j = generateStructurePiece(level, structuretemplatemanager, direction, rotation, randomsource, body, startAt.relative(direction, i + randomsource.nextInt(1)).relative(bendTo, bendToDist)) + 1;
            generateStructurePiece(level, structuretemplatemanager, direction, rotation, randomsource, tail, startAt.relative(direction, i + j + randomsource.nextInt(1)).relative(bendTo, bendToDist + randomsource.nextInt(2)));
            return true;
        }
        return false;
    }

    private int generateStructurePiece(WorldGenLevel level, StructureTemplateManager structuretemplatemanager, Direction direction, Rotation rotation, RandomSource randomsource, ResourceLocation head, BlockPos blockpos) {
        StructureTemplate structuretemplate = structuretemplatemanager.getOrCreate(head);
        StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(rotation).setRandom(randomsource).addProcessor(WhalefallProcessor.INSTANCE);
        Vec3i defaultSize = structuretemplate.getSize(Rotation.NONE);
        Vec3i rotatedSize = structuretemplate.getSize(rotation);
        BlockPos blockpos1 = blockpos.offset(-rotatedSize.getX() / 2, 0, -rotatedSize.getZ() / 2);
        BlockPos blockpos2 = structuretemplate.getZeroPositionWithTransform(blockpos1, Mirror.NONE, rotation);
        structuretemplate.placeInWorld(level, blockpos2, blockpos2, structureplacesettings, randomsource, 18);
        return Math.abs(defaultSize.getZ());
    }
}

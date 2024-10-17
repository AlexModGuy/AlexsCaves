package com.github.alexmodguy.alexscaves.server.level.feature;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.CandyCanePoleBlock;
import com.github.alexmodguy.alexscaves.server.block.SmallCandyCaneBlock;
import com.github.alexmodguy.alexscaves.server.level.feature.config.LollipopFeatureConfiguration;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.LollipopProcessor;
import com.github.alexmodguy.alexscaves.server.level.structure.processor.WhalefallProcessor;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class LollipopFeature extends Feature<LollipopFeatureConfiguration> {

    public LollipopFeature(Codec<LollipopFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<LollipopFeatureConfiguration> context) {
        RandomSource randomsource = context.random();
        WorldGenLevel level = context.level();
         BlockPos genAt = context.origin();
        if (!level.getBlockState(genAt).is(ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get())) {
            return false;
        }
        boolean big = randomsource.nextFloat() < context.config().bigChance;
        genAt = genAt.above();
        int poleHeight = 3 + randomsource.nextInt(3);
        if(big){
            poleHeight += 4 + randomsource.nextInt(2);
        }
        if(hasClearance(level, genAt, poleHeight + (big ? 10 : 6))){
            for(int i = 0; i < poleHeight; i++){
                level.setBlock(genAt.above(i), ACBlockRegistry.STRIPPED_CANDY_CANE_BLOCK.get().defaultBlockState(), 4);
            }
            BlockPos structurePos = genAt.above(poleHeight);
            Rotation rotation = Rotation.getRandom(randomsource);
            ResourceLocation structureLocation = big ? context.config().bigLollipopTopStructures.get(randomsource.nextInt(context.config().bigLollipopTopStructures.size())) : context.config().smallLollipopTopStructures.get(randomsource.nextInt(context.config().smallLollipopTopStructures.size()));
            StructureTemplateManager structuretemplatemanager = level.getLevel().getServer().getStructureManager();

            StructureTemplate structuretemplate = structuretemplatemanager.getOrCreate(structureLocation);
            StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setRotation(rotation).setRandom(randomsource).addProcessor(new LollipopProcessor(randomsource));
            Vec3i rotatedSize = structuretemplate.getSize(rotation);
            BlockPos blockpos1 = structurePos.offset((int) -Math.round(rotatedSize.getX() / 2F - 1), 0, (int) -Math.ceil(rotatedSize.getZ() / 2F - 1));
            BlockPos blockpos2 = structuretemplate.getZeroPositionWithTransform(blockpos1, Mirror.NONE, rotation);
            structuretemplate.placeInWorld(level, blockpos2, blockpos2, structureplacesettings, randomsource, 4);
            return true;
        }
        return false;
    }

    private boolean hasClearance(WorldGenLevel level, BlockPos pos, int height){
        int i = 1;
        while(i <= height){
            if(!level.getBlockState(pos.above(i)).canBeReplaced()){
                return false;
            }
            i++;
        }
        return true;
    }
}

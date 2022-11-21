package com.github.alexmodguy.alexscaves.server.level.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Function;

public class WaterBubbleCarver  extends CaveWorldCarver {

    public WaterBubbleCarver(Codec<CaveCarverConfiguration> p_64873_) {
        super(p_64873_);
    }

    protected int getCaveBound() {
        return 15;
    }

    protected float getThickness(RandomSource randomSource) {
        return (randomSource.nextFloat() * 4.0F) + 2.0F;
    }

    protected double getYScale() {
        return 1.0D;
    }

    public boolean carve(CarvingContext p_224885_, CaveCarverConfiguration p_224886_, ChunkAccess p_224887_, Function<BlockPos, Holder<Biome>> p_224888_, RandomSource p_224889_, Aquifer p_224890_, ChunkPos p_224891_, CarvingMask p_224892_) {
        int i = SectionPos.sectionToBlockCoord(this.getRange() * 2 - 1);
        int j = p_224889_.nextInt(p_224889_.nextInt(p_224889_.nextInt(this.getCaveBound()) + 1) + 1);

        for(int k = 0; k < j; ++k) {
            double d0 = (double)p_224891_.getBlockX(p_224889_.nextInt(16));
            double d1 = (double)p_224886_.y.sample(p_224889_, p_224885_);
            double d2 = (double)p_224891_.getBlockZ(p_224889_.nextInt(16));
            double d3 = (double)p_224886_.horizontalRadiusMultiplier.sample(p_224889_);
            double d4 = (double)p_224886_.verticalRadiusMultiplier.sample(p_224889_);
            double d5 = -0.4F - p_224889_.nextFloat() * 0.6F;
            WorldCarver.CarveSkipChecker worldcarver$carveskipchecker = (p_159202_, p_159203_, p_159204_, p_159205_, p_159206_) -> {
                return shouldSkip(p_159203_, p_159204_, p_159205_, d5);
            };
            double d6 = (double)p_224886_.yScale.sample(p_224889_);
            float f1 = 3;
            this.createRoom(p_224885_, p_224886_, p_224887_, p_224888_, p_224890_, d0, d1, d2, f1, d6, p_224892_, worldcarver$carveskipchecker);

        }

        return true;
    }

    private static boolean shouldSkip(double p_159196_, double p_159197_, double p_159198_, double p_159199_) {
        if (p_159197_ <= p_159199_) {
            return true;
        } else {
            return p_159196_ * p_159196_ + p_159197_ * p_159197_ + p_159198_ * p_159198_ >= 1.0D;
        }
    }

    protected boolean carveBlock(CarvingContext context, CaveCarverConfiguration carverConfiguration, ChunkAccess access, Function<BlockPos, Holder<Biome>> biomeGetter, CarvingMask mask, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos blockPos, Aquifer aquifer, MutableBoolean mutableBoolean) {
        if (this.canReplaceBlock(carverConfiguration, access.getBlockState(mutableBlockPos))) {
            BlockState blockstate = WATER.createLegacyBlock();
            access.setBlockState(mutableBlockPos, blockstate, false);
            return true;
        } else {
            return false;
        }
    }
}
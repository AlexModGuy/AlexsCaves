package com.github.alexmodguy.alexscaves.server.block.grower;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class AncientTreeGrower extends AbstractTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "ancient_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_ANCIENT_TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "giant_ancient_tree"));

    public boolean growTree(ServerLevel serverLevel, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState state, RandomSource randomSource) {
        for (int i = 0; i >= -2; --i) {
            for (int j = 0; j >= -2; --j) {
                if (isThreeByThreeSapling(state, serverLevel, blockPos, i, j)) {
                    return this.placeMega(serverLevel, chunkGenerator, blockPos, state, randomSource, i, j);
                }
            }
        }

        return super.growTree(serverLevel, chunkGenerator, blockPos, state, randomSource);
    }

    public boolean placeMega(ServerLevel serverLevel, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState blockState, RandomSource randomSource, int x, int z) {
        ResourceKey<ConfiguredFeature<?, ?>> resourcekey = this.getConfiguredMegaFeature(randomSource);
        if (resourcekey == null) {
            return false;
        } else {
            Holder<ConfiguredFeature<?, ?>> holder = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(resourcekey).orElse((Holder.Reference<ConfiguredFeature<?, ?>>) null);
            var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(serverLevel, randomSource, blockPos, holder);
            holder = event.getFeature();
            if (event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
            if (holder == null) {
                return false;
            } else {
                ConfiguredFeature<?, ?> configuredfeature = holder.value();
                BlockState blockstate = Blocks.AIR.defaultBlockState();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        serverLevel.setBlock(blockPos.offset(x + i, 0, z + j), blockstate, 4);
                    }
                }
                if (configuredfeature.place(serverLevel, chunkGenerator, randomSource, blockPos.offset(x + 1, 0, z + 1))) {
                    return true;
                } else {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            serverLevel.setBlock(blockPos.offset(x + i, 0, z + j), blockstate, 4);
                        }
                    }
                    return false;
                }
            }
        }
    }

    public static boolean isThreeByThreeSapling(BlockState state, BlockGetter level, BlockPos pos, int x, int z) {
        Block block = state.getBlock();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mutableBlockPos.set(pos.getX() + x + i, pos.getY(), pos.getZ() + z + j);
                if (!level.getBlockState(mutableBlockPos).is(block)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean b) {
        return ANCIENT_TREE;
    }

    private ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource randomSource) {
        return GIANT_ANCIENT_TREE;
    }
}

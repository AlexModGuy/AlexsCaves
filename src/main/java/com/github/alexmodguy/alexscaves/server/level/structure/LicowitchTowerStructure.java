package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.LicowitchTowerStructurePiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.SodaBottleStructurePiece;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;
import java.util.function.Consumer;

public class LicowitchTowerStructure extends Structure {

    public static final Codec<LicowitchTowerStructure> CODEC = simpleCodec((settings) -> new LicowitchTowerStructure(settings));

    private static final ResourceLocation TOWER_NBT = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "licowitch_tower");
    private static final int Y = 0;

    public LicowitchTowerStructure(StructureSettings settings) {
        super(settings);
    }

    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int i = context.chunkPos().getBlockX(9);
        int j = context.chunkPos().getBlockZ(9);
        for (Holder<Biome> holder : ACMath.getBiomesWithinAtY(context.biomeSource(), i, -30, j, 20, context.randomState().sampler())) {
            if (!holder.is(ACBiomeRegistry.CANDY_CAVITY)) {
                return Optional.empty();
            }
        }
        Rotation rotation = Rotation.getRandom(context.random());
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), Y, context.chunkPos().getMinBlockZ());
        return atYCaveBiomePoint(context, piecesBuilder -> piecesBuilder.addPiece(new LicowitchTowerStructurePiece(context.structureTemplateManager(), TOWER_NBT, blockpos, rotation)));
    }

    protected Optional<GenerationStub> atYCaveBiomePoint(GenerationContext context, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        return Optional.of(new GenerationStub(new BlockPos(i, Y, j), builderConsumer));
    }


    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.LICOWITCH_TOWER.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }
}

package com.github.alexmodguy.alexscaves.server.level.structure;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.GingerbreadHousePiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.GingerbreadRoadPiece;
import com.github.alexmodguy.alexscaves.server.level.structure.piece.LicowitchTowerStructurePiece;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GingerbreadTownStructure extends Structure {

    public static final Codec<GingerbreadTownStructure> CODEC = simpleCodec((settings) -> new GingerbreadTownStructure(settings));

    private static final int Y = 0;

    public GingerbreadTownStructure(StructureSettings settings) {
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
        BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), Y, context.chunkPos().getMinBlockZ());
        return atYCaveBiomePoint(context, piecesBuilder -> buildTown(context, blockpos, piecesBuilder));
    }

    private void buildTown(GenerationContext context, BlockPos blockpos, StructurePiecesBuilder piecesBuilder) {
        Rotation rotation = Rotation.getRandom(context.random());
        StructureTemplate centerTemplate = context.structureTemplateManager().getOrCreate(GingerbreadHousePiece.TOWN_CENTER_TEMPLATE);
        Vec3i centerSize = centerTemplate.getSize(rotation);
        BlockPos townCenterPos1 = blockpos.offset(-centerSize.getX() / 2, 0, -centerSize.getZ() / 2);
        BlockPos townCenterPos2 = centerTemplate.getZeroPositionWithTransform(townCenterPos1, Mirror.NONE, rotation);
        GingerbreadRoadPiece roadPiece = new GingerbreadRoadPiece(context.structureTemplateManager(), blockpos, 30, 5, rotation.rotate(Direction.NORTH));
        piecesBuilder.addPiece(roadPiece);
        GingerbreadHousePiece townCenterPiece = new GingerbreadHousePiece(context.structureTemplateManager(), GingerbreadHousePiece.TOWN_CENTER_TEMPLATE, townCenterPos2, rotation);
        //add to pending children just so that the bounding boxes pick up on the town center
        roadPiece.pendingChildren.add(townCenterPiece);
        piecesBuilder.addPiece(townCenterPiece);
        roadPiece.addChildren(roadPiece, piecesBuilder, context.random());
        List<StructurePiece> list = roadPiece.pendingChildren;
        while(!list.isEmpty()) {
            int i = context.random().nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.addChildren(roadPiece, piecesBuilder, context.random());
        }
    }

    protected Optional<GenerationStub> atYCaveBiomePoint(GenerationContext context, Consumer<StructurePiecesBuilder> builderConsumer) {
        ChunkPos chunkpos = context.chunkPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        return Optional.of(new GenerationStub(new BlockPos(i, Y, j), builderConsumer));
    }


    @Override
    public StructureType<?> type() {
        return ACStructureRegistry.GINGERBREAD_TOWN.get();
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }


}

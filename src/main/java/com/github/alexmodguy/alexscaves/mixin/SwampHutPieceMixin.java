package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACLootTableRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SwampHutPiece.class)
public abstract class SwampHutPieceMixin extends ScatteredFeaturePiece {

    private boolean placedMainChest;

    protected SwampHutPieceMixin(StructurePieceType type, int i1, int i2, int i3, int i4, int i5, int i6, Direction direction) {
        super(type, i1, i2, i3, i4, i5, i6, direction);
    }

    protected SwampHutPieceMixin(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    @Inject(
            method = {"Lnet/minecraft/world/level/levelgen/structure/structures/SwampHutPiece;postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_calculateEntityAnimation(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos, CallbackInfo ci) {
        if(AlexsCaves.COMMON_CONFIG.lootChestInWitchHuts.get()) {
            if (this.updateAverageGroundHeight(level, boundingBox, 0)) {
                if (!this.placedMainChest) {
                    this.placedMainChest = this.createChest(level, boundingBox, randomSource, 2, 2, 6, ACLootTableRegistry.WITCH_HUT_CHEST);
                }
            }
        }
    }
}
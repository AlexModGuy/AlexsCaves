package com.github.alexmodguy.alexscaves.server.level.structure.piece;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.ForsakenIdolBlock;
import com.github.alexmodguy.alexscaves.server.block.ThornwoodBranchBlock;
import com.github.alexmodguy.alexscaves.server.level.structure.ForlornBridgeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.HashSet;
import java.util.Set;

public class ForlornBridgeStructurePiece extends StructurePiece {
    protected final BlockPos bridgePos;
    protected final int sectionId;
    protected final int maxSections;
    protected final Direction direction;

    public ForlornBridgeStructurePiece(BlockPos bridgePos, int sectionId, int maxSections, Direction direction) {
        super(ACStructurePieceRegistry.FORLORN_BRIDGE.get(), 0, createBoundingBox(bridgePos, direction));
        this.bridgePos = bridgePos;
        this.sectionId = sectionId;
        this.maxSections = maxSections;
        this.direction = direction;
    }

    public ForlornBridgeStructurePiece(CompoundTag tag) {
        super(ACStructurePieceRegistry.FORLORN_BRIDGE.get(), tag);
        this.bridgePos = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
        this.sectionId = tag.getInt("Section");
        this.maxSections = tag.getInt("MaxSections");
        this.direction = Direction.from2DDataValue(tag.getInt("Direction"));
    }

    public ForlornBridgeStructurePiece(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag tag) {
        this(tag);
    }

    private static BoundingBox createBoundingBox(BlockPos origin, Direction direction) {
        int i = ForlornBridgeStructure.BRIDGE_SECTION_WIDTH / 2;
        int j = ForlornBridgeStructure.BRIDGE_SECTION_LENGTH - i;
        int dirX = i + j * Math.abs(direction.getStepX());
        int dirZ = i + j * Math.abs(direction.getStepZ());
        return new BoundingBox(origin.getX() - dirX, -64, origin.getZ() - dirZ, origin.getX() + dirX, 256, origin.getZ() + dirZ);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("TPX", this.bridgePos.getX());
        tag.putInt("TPY", this.bridgePos.getY());
        tag.putInt("TPZ", this.bridgePos.getZ());
        tag.putInt("Section", this.sectionId);
        tag.putInt("MaxSections", this.maxSections);
        tag.putInt("Direction", this.direction.get2DDataValue());
    }

    public void postProcess(WorldGenLevel level, StructureManager featureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Set<BlockPos> supports = new HashSet<>();
        Set<BlockPos> specialSupports = new HashSet<>();

        int j = ForlornBridgeStructure.BRIDGE_SECTION_WIDTH / 2;
        for (int width = -j; width < j; width++) {
            pos.set(bridgePos);
            pos.move(direction.getClockWise(), width);
            int startLength = 0;
            int endLength = ForlornBridgeStructure.BRIDGE_SECTION_LENGTH;
            if (sectionId == 0) {
                startLength = random.nextInt(ForlornBridgeStructure.BRIDGE_SECTION_LENGTH - 1);
            }
            if (sectionId == maxSections) {
                endLength = random.nextInt(ForlornBridgeStructure.BRIDGE_SECTION_LENGTH);
            }
            for (int length = startLength; length < endLength; length++) {
                pos.move(direction);
                BlockState prior = checkedGetBlock(level, pos);
                if (!prior.isAir() && prior.getFluidState().isEmpty()) {
                    continue;
                }
                BlockState state = ACBlockRegistry.THORNWOOD_PLANKS_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);
                if (width == -j || width == j - 1) {
                    if (length % 3 == startLength) {
                        state = Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState();
                        supports.add(pos.immutable());
                        if (sectionId == 0 && length == startLength || sectionId == maxSections && length != startLength) {
                            specialSupports.add(pos.immutable());
                        }
                    } else {
                        boolean left = width == j - 1;
                        state = ACBlockRegistry.THORNWOOD_PLANKS_STAIRS.get().defaultBlockState().setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.FACING, left ? direction.getCounterClockWise() : direction.getClockWise());
                    }
                }
                checkedSetBlock(level, pos, state);
            }
        }
        for (BlockPos support : supports) {
            checkedSetBlock(level, support.above(), Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState());
            checkedSetBlock(level, support.above().relative(direction), ACBlockRegistry.THORNWOOD_BRANCH.get().defaultBlockState().setValue(ThornwoodBranchBlock.FACING, direction));
            checkedSetBlock(level, support.above().relative(direction.getOpposite()), ACBlockRegistry.THORNWOOD_BRANCH.get().defaultBlockState().setValue(ThornwoodBranchBlock.FACING, direction.getOpposite()));
            if (specialSupports.contains(support)) {
                boolean end = this.maxSections == this.sectionId;
                checkedSetBlock(level, support.above(2), ACBlockRegistry.FORSAKEN_IDOL.get().defaultBlockState().setValue(ForsakenIdolBlock.FACING, end ? direction : direction.getOpposite()));
            } else {
                checkedSetBlock(level, support.above(2), Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState());
                checkedSetBlock(level, support.above(3), ACBlockRegistry.THORNWOOD_PLANKS_FENCE.get().defaultBlockState());
                if (random.nextBoolean()) {
                    buildChain(level, support.above(4));
                }
            }
            if (random.nextBoolean() || sectionId == 0 || sectionId == maxSections) {
                buildBeam(level, support.below());
            }
        }
    }

    private void buildBeam(WorldGenLevel level, BlockPos below) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(below);
        while (!checkedGetBlock(level, pos).isSolid() && pos.getY() > level.getMinBuildHeight()) {
            checkedSetBlock(level, pos, Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState());
            pos.move(0, -1, 0);
        }
    }

    private void buildChain(WorldGenLevel level, BlockPos above) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pos.set(above);
        if (level.canSeeSky(pos) || level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, above.getX(), above.getZ()) >= level.getMaxBuildHeight()) {
            return;
        }
        while (!checkedGetBlock(level, pos).isSolid() && pos.getY() < level.getMaxBuildHeight() && !level.canSeeSky(pos)) {
            checkedSetBlock(level, pos, Blocks.CHAIN.defaultBlockState());
            pos.move(0, 1, 0);
        }
    }

    public void checkedSetBlock(WorldGenLevel level, BlockPos position, BlockState state) {
        if (this.getBoundingBox().isInside(position)) {
            level.setBlock(position, state, 128);
        }
    }

    public BlockState checkedGetBlock(WorldGenLevel level, BlockPos position) {
        if (this.getBoundingBox().isInside(position)) {
            return level.getBlockState(position);
        } else {
            return Blocks.VOID_AIR.defaultBlockState();
        }
    }

}

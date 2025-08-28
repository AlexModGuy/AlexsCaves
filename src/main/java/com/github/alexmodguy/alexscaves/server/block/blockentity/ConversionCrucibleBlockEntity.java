package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.ConversionCrucibleBlock;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.BiomeTreatItem;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDummyBiomeSource;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConversionCrucibleBlockEntity extends BlockEntity {

    private static final Map<Optional<Holder.Reference<Biome>>, Integer> BIOME_COLORS = new HashMap<>();
    public static final int MAX_FILL_AMOUNT = 10;
    public static final int MAX_CONVERSION_TIME = 100;
    private static final int PLAINS_FOG_COLOR = 12638463;

    private final List<RecursiveBlockPlacement> recursiveBlockPlacements = new ArrayList<>();

    public int tickCount;
    private float prevConversionProgress;
    private float conversionProgress;
    private float prevSplashProgress;
    private float splashProgress;
    private float prevItemDisplayProgress;
    private float itemDisplayProgress;
    private int filledLevel;
    private int biomeColor = -1;
    private int splashTimer;
    private int conversionTime = 0;
    private ResourceKey<Biome> convertingToBiome;
    private ItemStack displayStack = ItemStack.EMPTY;
    private ItemStack wantStack = ItemStack.EMPTY;

    private BlockState topBlockForBiome = Blocks.GRASS_BLOCK.defaultBlockState();
    private BlockState middleBlockForBiome = Blocks.DIRT.defaultBlockState();
    private BlockState bottomBlockForBiome = Blocks.STONE.defaultBlockState();
    private int witchModeDuration = 0;

    private int witchRainbowColor;
    private int witchFillTextIndex;

    public ConversionCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ACBlockEntityRegistry.CONVERSION_CRUCIBLE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ConversionCrucibleBlockEntity entity) {
        entity.prevConversionProgress = entity.conversionProgress;
        entity.prevSplashProgress = entity.splashProgress;
        entity.prevItemDisplayProgress = entity.itemDisplayProgress;

        if (entity.biomeColor == -1 && entity.convertingToBiome != null) {
            Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
            Optional<Holder.Reference<Biome>> biomeHolder = registry.getHolder(entity.convertingToBiome);
            if (biomeHolder.isPresent()) {
                entity.biomeColor = calculateBiomeColor(biomeHolder);
            } else {
                entity.biomeColor = 0;
            }
        }
        if (entity.wantStack.is(entity.displayStack.getItem()) && !entity.wantStack.isEmpty()) {
            if (entity.itemDisplayProgress < 5.0F) {
                entity.itemDisplayProgress++;
            }
        } else {
            if (entity.itemDisplayProgress > 0.0F) {
                entity.itemDisplayProgress--;
            }
        }
        if (entity.splashTimer > 0) {
            entity.splashTimer--;
            if (entity.splashProgress < 5) {
                entity.splashProgress++;
            }
        } else if (entity.splashProgress > 0) {
            entity.splashProgress--;
        }
        if (entity.getFilledLevel() >= MAX_FILL_AMOUNT && entity.convertingToBiome != null && !entity.isWitchMode()) {
            entity.displayStack = ItemStack.EMPTY;
            entity.wantStack = ItemStack.EMPTY;
            if (entity.conversionTime < MAX_CONVERSION_TIME) {
                if (entity.conversionTime % (MAX_CONVERSION_TIME / 10) == 0 && entity.conversionTime >= 20 && !level.isClientSide) {
                    entity.updateTopAndBottomBlocks();
                    entity.recursivelySpreadBiomeBlocks(new ArrayList<>(), entity.getBlockPos().below(), 10, 10);
                }
                if (entity.conversionTime == 0) {
                    entity.level.playSound(null, entity.getBlockPos(), ACSoundRegistry.CONVERSION_CRUCIBLE_CONVERT.get(), SoundSource.BLOCKS);
                }
                entity.conversionTime++;
            } else {
                entity.convertBiome();
                entity.markUpdated();
                entity.conversionTime = 0;
                entity.setFilledLevel(0);
            }
            entity.conversionProgress = (entity.conversionTime / (float) MAX_CONVERSION_TIME) * 20.0F;
        } else if (entity.conversionProgress > 0.0F) {
            entity.conversionProgress--;
        }
        if (entity.itemDisplayProgress == 0.0F) {
            entity.displayStack = entity.wantStack;
        }
        if (entity.isWitchMode() && entity.splashProgress == 0) {
            entity.witchFillTextIndex = entity.filledLevel;
        }
        entity.tickCount++;
        if (level.isClientSide) {
            entity.spawnConversionParticles(entity.conversionTime > MAX_CONVERSION_TIME - 2 || entity.getFilledLevel() == 0);
            if (entity.getFilledLevel() > 0 && level.random.nextFloat() < 0.33F) {
                int intcolor = entity.getConvertingToColor();
                float r = (float) ((intcolor & 16711680) >> 16) / 255.0F;
                float g = (float) ((intcolor & '\uff00') >> 8) / 255.0F;
                float b = (float) ((intcolor & 255) >> 0) / 255.0F;
                level.addAlwaysVisibleParticle(ACParticleRegistry.SMALL_COLORED_DUST.get(), true, entity.getBlockPos().getX() + 0.25F + level.random.nextFloat() * 0.5F, entity.getBlockPos().getY() + 0.2F + entity.getFilledLevel() * 0.1F, entity.getBlockPos().getZ() + 0.25F + level.random.nextFloat() * 0.5F, r, g, b);
            }
        } else {
            if (entity.tickCount % 5 == 0) {
                boolean flag = false;
                for (ItemEntity item : entity.getItemsAtAndAbove(level, pos)) {
                    if (entity.getConvertingToBiome() == null && item.getItem().is(ACItemRegistry.BIOME_TREAT.get()) && BiomeTreatItem.getCaveBiome(item.getItem()) != null) {
                        entity.setConvertingToBiome(BiomeTreatItem.getCaveBiome(item.getItem()));
                        entity.setFilledLevel(1);
                        entity.rerollWantedItem();
                        item.getItem().shrink(1);
                        flag = true;
                    } else if (item.getItem().is(entity.wantStack.getItem())) {
                        flag = true;
                        entity.consumeItem(item.getItem());
                        break;
                    }
                }
                if (flag) {
                    entity.markUpdated();
                }
            }
            if (!entity.recursiveBlockPlacements.isEmpty()) {
                Iterator<RecursiveBlockPlacement> iterator = entity.recursiveBlockPlacements.iterator();
                while (iterator.hasNext()) {
                    RecursiveBlockPlacement recursiveBlockPlacement = iterator.next();
                    recursiveBlockPlacement.setPlaceIn(recursiveBlockPlacement.getPlaceIn() - 1);
                    if (recursiveBlockPlacement.getPlaceIn() <= 0) {
                        if(!level.getBlockState(recursiveBlockPlacement.getPos()).is(ACTagRegistry.UNMOVEABLE)){
                            level.setBlockAndUpdate(recursiveBlockPlacement.getPos(), recursiveBlockPlacement.getToPlace());
                        }
                        iterator.remove();
                    }

                }
            }
            if (entity.witchModeDuration > 0) {
                entity.witchModeDuration--;
                if (entity.witchModeDuration == 0) {
                    entity.convertingToBiome = null;
                    entity.biomeColor = -1;
                    entity.rerollWantedItem();
                    entity.displayStack = ItemStack.EMPTY;
                    entity.wantStack = ItemStack.EMPTY;
                    entity.filledLevel = 0;
                    entity.markUpdated();
                }
            }
        }
        entity.witchRainbowColor = Color.HSBtoRGB((entity.tickCount % 100) / 100F, 1F, 1F);
    }

    private List<ItemEntity> getItemsAtAndAbove(Level level, BlockPos pos) {
        return ConversionCrucibleBlock.getSuckShape().toAabbs().stream().flatMap((aabb) -> {
            return level.getEntitiesOfClass(ItemEntity.class, aabb.move(pos.getX(), pos.getY(), pos.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    public void spawnConversionParticles(boolean explosion) {
        float progressLerp = this.getConversionProgress(1.0F);
        if (progressLerp > 0) {
            float diameter = this.getConversionAreaWidth() * progressLerp;
            int intcolor = this.getConvertingToColor();
            float r = (float) ((intcolor & 16711680) >> 16) / 255.0F;
            float g = (float) ((intcolor & '\uff00') >> 8) / 255.0F;
            float b = (float) ((intcolor & 255) >> 0) / 255.0F;
            for (int i = 0; i < (explosion ? 35 : 3); i++) {
                float x = (level.random.nextFloat() - 0.5F) * diameter;
                float z = (level.random.nextFloat() - 0.5F) * diameter;
                if (Math.sqrt(x * x + z * z) < diameter * 0.5F) {
                    level.addAlwaysVisibleParticle(explosion ? ACParticleRegistry.CONVERSION_CRUCIBLE_EXPLOSION.get() : ACParticleRegistry.COLORED_DUST.get(), true, this.getBlockPos().getX() + 0.5F + x, this.getBlockPos().getY() + level.random.nextFloat() * 0.5F, this.getBlockPos().getZ() + 0.5F + z, r, g, b);
                }
            }
        }
    }

    private void updateTopAndBottomBlocks() {
        topBlockForBiome = Blocks.GRASS_BLOCK.defaultBlockState();
        middleBlockForBiome = Blocks.DIRT.defaultBlockState();
        bottomBlockForBiome = Blocks.STONE.defaultBlockState();
        if (level instanceof ServerLevel serverLevel && convertingToBiome != null) {
            try {
                Registry<Biome> registry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME);
                Optional<Holder.Reference<Biome>> biomeHolder = registry.getHolder(convertingToBiome);
                ChunkAccess chunkaccess = serverLevel.getChunk(this.getBlockPos());
                WorldGenRegion worldGenRegion = new WorldGenRegion(serverLevel, List.of(chunkaccess), ChunkStatus.SURFACE, 0);
                ResourceKey<NoiseGeneratorSettings> dimensionType = NoiseGeneratorSettings.OVERWORLD;
                if (biomeHolder.isPresent()) {
                    if (biomeHolder.get().is(BiomeTags.IS_NETHER)) {
                        dimensionType = NoiseGeneratorSettings.NETHER;
                    } else if (biomeHolder.get().is(BiomeTags.IS_END)) {
                        dimensionType = NoiseGeneratorSettings.END;
                    }
                    topBlockForBiome = getFallbackTopBlock(biomeHolder.get());
                    middleBlockForBiome = getFallbackMiddleBlock(biomeHolder.get());
                    bottomBlockForBiome = getFallbackBottomBlock(biomeHolder.get());
                }
                Holder<NoiseGeneratorSettings> settings = serverLevel.registryAccess().registryOrThrow(Registries.NOISE_SETTINGS).getHolderOrThrow(dimensionType);
                //for compat with other world types, like flat worlds, we cannot assume the chunk generator of the world is noise-based so we must create a new one
                NoiseBasedChunkGenerator noiseBasedChunkGenerator = new NoiseBasedChunkGenerator(new ACDummyBiomeSource(), settings);
                //get or create a dummy noise chunk
                NoiseChunk noisechunk = chunkaccess.getOrCreateNoiseChunk((chunkAccess) -> {
                    return noiseBasedChunkGenerator.createNoiseChunk(chunkAccess, serverLevel.structureManager(), Blender.of(worldGenRegion), serverLevel.getChunkSource().randomState());
                });
                //should ideally be merged when we get it, for some reason isn't. Idk why
                SurfaceRules.RuleSource ruleSource = SurfaceRulesManager.mergeOverworldRules(noiseBasedChunkGenerator.generatorSettings().value().surfaceRule());
                WorldGenerationContext worldGenerationContext = new WorldGenerationContext(noiseBasedChunkGenerator, serverLevel);
                Function<BlockPos, Holder<Biome>> biomeRef = (blockPos -> biomeHolder.get());
                SurfaceRules.Context surfacerulesContext = new SurfaceRules.Context(serverLevel.getChunkSource().randomState().surfaceSystem(), serverLevel.getChunkSource().randomState(), chunkaccess, noisechunk, biomeRef, registry, worldGenerationContext);
                SurfaceRules.SurfaceRule rule = ruleSource.apply(surfacerulesContext);
                int x = this.getBlockPos().getX();
                int z = this.getBlockPos().getZ();
                //one over the top (grass condition)
                int topHeight = serverLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) + 1;
                surfacerulesContext.updateXZ(x, z);
                surfacerulesContext.updateY(1, 1, topHeight, x, topHeight, z);
                BlockState grass = rule.tryApply(x, topHeight, z);
                if (grass != null && !grass.is(Blocks.BEDROCK)) {
                    topBlockForBiome = grass;
                }
                //tell it that there is a block about the top position
                surfacerulesContext.updateY(1, 1, topHeight + 1, x, topHeight, z);
                BlockState dirt = rule.tryApply(x, topHeight, z);
                if (dirt != null && !dirt.is(Blocks.BEDROCK)) {
                    middleBlockForBiome = dirt;
                }
                //tell it that there is many blocks about the top position
                surfacerulesContext.updateY(1, 1, topHeight + 20, x, topHeight, z);
                BlockState stone = rule.tryApply(x, topHeight, z);
                if (stone != null && !stone.is(Blocks.BEDROCK)) {
                    bottomBlockForBiome = stone;
                }
            } catch (Exception e) {
                AlexsCaves.LOGGER.warn("Encountered error finding the surface blocks of a biome");
            }
        }
    }

    private boolean isBiomeBlock(BlockState blockState) {
        return blockState.is(topBlockForBiome.getBlock()) || blockState.is(bottomBlockForBiome.getBlock()) || blockState.is(middleBlockForBiome.getBlock());
    }

    public void recursivelySpreadBiomeBlocks(List<BlockPos> crossed, BlockPos to, int maxDistance, int distanceIn) {
        if (distanceIn > 0) {
            if (!isBiomeBlock(level.getBlockState(to))) {
                BlockState blockState = level.getBlockState(to.above()).canBeReplaced() ? topBlockForBiome : level.getBlockState(to.above(2)).canBeReplaced() ? middleBlockForBiome : bottomBlockForBiome;
                recursiveBlockPlacements.add(new RecursiveBlockPlacement(2 * (maxDistance - distanceIn), to, blockState));
            }
            crossed.add(to);
            distanceIn--;
            List<BlockPos> possibles = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                BlockPos offset = to.relative(direction);
                BlockState state = level.getBlockState(offset);
                if (!state.canBeReplaced() && !crossed.contains(offset) && !state.is(ACTagRegistry.UNMOVEABLE) && level.getBlockEntity(offset) == null && (direction != Direction.DOWN || level.random.nextInt(3) == 0)) {
                    possibles.add(offset);
                }
            }
            if (possibles.size() > 0) {
                recursivelySpreadBiomeBlocks(crossed, Util.getRandom(possibles, level.random), maxDistance, distanceIn);
            }
        }
    }

    public void convertBiome() {
        Optional<Holder.Reference<Biome>> biomeHolder = level.registryAccess().registryOrThrow(Registries.BIOME).getHolder(convertingToBiome);
        if(biomeHolder.isEmpty()){
            return;
        }
        AABB aabb = new AABB(this.getBlockPos().offset(-32, -32, -32), this.getBlockPos().offset(32, 32, 32));
        for (Player player : level.getEntitiesOfClass(Player.class, aabb, EntitySelector.NO_SPECTATORS)) {
            ACAdvancementTriggerRegistry.CONVERT_BIOME.triggerForEntity(player);
            if (biomeHolder.get().is(BiomeTags.IS_NETHER) && this.level.dimensionType().bedWorks()) {
                ACAdvancementTriggerRegistry.CONVERT_NETHER_BIOME.triggerForEntity(player);
            }
        }
        int width = (int) Math.ceil(getConversionAreaWidth() * 0.5F) + 1;

        List<ChunkAccess> list = new ArrayList<>();
        BoundingBox biomeConversionBox = new BoundingBox(this.getBlockPos().getX() - width, this.getBlockPos().getY() - width, this.getBlockPos().getZ() - width, this.getBlockPos().getX() + width, this.getBlockPos().getY() + width, this.getBlockPos().getZ() + width);
        if(level instanceof ServerLevel serverLevel){
            for(int k = SectionPos.blockToSectionCoord(biomeConversionBox.minZ()); k <= SectionPos.blockToSectionCoord(biomeConversionBox.maxZ()); ++k) {
                for(int l = SectionPos.blockToSectionCoord(biomeConversionBox.minX()); l <= SectionPos.blockToSectionCoord(biomeConversionBox.maxX()); ++l) {
                    ChunkAccess chunkaccess = serverLevel.getChunk(l, k, ChunkStatus.FULL, false);
                    if (chunkaccess != null) {
                        list.add(chunkaccess);
                    }
                }
            }
            MutableInt mutableint = new MutableInt(0);
            for(ChunkAccess chunkaccess1 : list) {
                chunkaccess1.fillBiomesFromNoise(makeResolver(mutableint, chunkaccess1, biomeConversionBox, width, biomeHolder.get()), serverLevel.getChunkSource().randomState().sampler());
                chunkaccess1.setUnsaved(true);
            }
            serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(list);
        }
    }

    private static BiomeResolver makeResolver(MutableInt biomeCounter, ChunkAccess chunkAccess, BoundingBox boundingBox, int width, Holder<Biome> biomeHolder) {
        return (quartX, quartY, quartZ, sampler) -> {
            int i = QuartPos.toBlock(quartX);
            int j = QuartPos.toBlock(quartY);
            int k = QuartPos.toBlock(quartZ);
            Holder<Biome> holder = chunkAccess.getNoiseBiome(quartX, quartY, quartZ);
            if (boundingBox.isInside(i, j, k)) {
                biomeCounter.increment();
                return biomeHolder;
            } else {
                return holder;
            }
        };
    }

    public void setConvertingToBiome(ResourceKey<Biome> resourceKey) {
        this.convertingToBiome = resourceKey;
        this.biomeColor = -1;
    }

    public ResourceKey<Biome> getConvertingToBiome() {
        return this.convertingToBiome;
    }

    public float getConversionProgress(float partialTicks) {
        return (prevConversionProgress + (conversionProgress - prevConversionProgress) * partialTicks) * 0.05F;
    }

    public float getSplashProgress(float partialTicks) {
        return (prevSplashProgress + (splashProgress - prevSplashProgress) * partialTicks) * 0.2F;
    }

    public float getItemDisplayProgress(float partialTicks) {
        return (prevItemDisplayProgress + (itemDisplayProgress - prevItemDisplayProgress) * partialTicks) * 0.2F;
    }

    public int getConvertingToColor() {
        return isWitchMode() ? witchRainbowColor : biomeColor;
    }

    public float getConversionAreaWidth() {
        return 10F;
    }

    public int getFilledLevel() {
        return filledLevel;
    }

    public void setFilledLevel(int filledLevel) {
        this.filledLevel = filledLevel;
    }

    public ItemStack getDisplayItem() {
        return displayStack;
    }

    public ItemStack getWantItem() {
        return wantStack;
    }

    public boolean isWitchMode() {
        return witchModeDuration > 0;
    }

    public void setWitchModeDuration(int duration) {
        this.witchModeDuration = duration;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        loadAdditional(compound);
    }

    public void loadAdditional(CompoundTag compound) {
        if (compound.contains("WantStack")) {
            this.wantStack = ItemStack.of(compound.getCompound("WantStack"));
        }
        if (compound.contains("DisplayStack")) {
            this.displayStack = ItemStack.of(compound.getCompound("DisplayStack"));
        }
        if (compound.contains("ConvertingToBiome")) {
            convertingToBiome = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(compound.getString("ConvertingToBiome")));
        }
        filledLevel = compound.getInt("FilledLevel");
        biomeColor = compound.getInt("BiomeColor");
        splashTimer = compound.getInt("SplashTimer");
        conversionTime = compound.getInt("ConversionTime");
        witchModeDuration = compound.getInt("WitchModeDuration");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.wantStack != null && !this.wantStack.isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.wantStack.save(stackTag);
            compound.put("WantStack", stackTag);
        }
        if (this.displayStack != null && !this.displayStack.isEmpty()) {
            CompoundTag stackTag = new CompoundTag();
            this.displayStack.save(stackTag);
            compound.put("DisplayStack", stackTag);
        }
        if (convertingToBiome != null) {
            compound.putString("ConvertingToBiome", convertingToBiome.location().toString());
        }
        compound.putInt("FilledLevel", filledLevel);
        compound.putInt("BiomeColor", biomeColor);
        compound.putInt("SplashTimer", splashTimer);
        compound.putInt("ConversionTime", conversionTime);
        compound.putInt("WitchModeDuration", witchModeDuration);

    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public AABB getRenderBoundingBox() {
        int f = 16;
        return new AABB(worldPosition.offset(-f, 0, -f), worldPosition.offset(f, 3, f));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (packet != null && packet.getTag() != null) {
            CompoundTag compound = packet.getTag();
            displayStack = ItemStack.EMPTY;
            wantStack = ItemStack.EMPTY;
            loadAdditional(compound);
        }
    }

    public void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void consumeItem(ItemStack copy) {
        copy.shrink(1);
        this.setFilledLevel(Math.min(this.getFilledLevel() + 1, this.isWitchMode() ? MAX_FILL_AMOUNT - 4 : MAX_FILL_AMOUNT));
        this.rerollWantedItem();
        this.splashTimer = 10;
    }

    @Nullable
    public Component getDisplayText() {
        if (this.getDisplayItem().isEmpty() || this.isWitchMode()) {
            return null;
        } else {
            return this.getDisplayItem().getHoverName();
        }
    }

    public static BlockState getFallbackTopBlock(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_NETHER)) {
            return Blocks.NETHERRACK.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_END)) {
            return Blocks.END_STONE.defaultBlockState();
        } else {
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
    }

    public static BlockState getFallbackMiddleBlock(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_NETHER)) {
            return Blocks.NETHERRACK.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_END)) {
            return Blocks.END_STONE.defaultBlockState();
        } else {
            return Blocks.DIRT.defaultBlockState();
        }
    }

    public static BlockState getFallbackBottomBlock(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_NETHER)) {
            return Blocks.NETHERRACK.defaultBlockState();
        } else if (biome.is(BiomeTags.IS_END)) {
            return Blocks.END_STONE.defaultBlockState();
        } else {
            return Blocks.STONE.defaultBlockState();
        }
    }

    public static ItemStack getFinalSacrificeForBiome(Holder<Biome> biome) {
        if (biome.is(ACBiomeRegistry.MAGNETIC_CAVES)) {
            return new ItemStack(ACBlockRegistry.HEART_OF_IRON.get());
        } else if (biome.is(ACBiomeRegistry.PRIMORDIAL_CAVES)) {
            return new ItemStack(ACItemRegistry.TECTONIC_SHARD.get());
        } else if (biome.is(ACBiomeRegistry.TOXIC_CAVES)) {
            return new ItemStack(ACBlockRegistry.NUCLEAR_BOMB.get());
        } else if (biome.is(ACBiomeRegistry.ABYSSAL_CHASM)) {
            return new ItemStack(ACBlockRegistry.ENIGMATIC_ENGINE.get());
        } else if (biome.is(ACBiomeRegistry.FORLORN_HOLLOWS)) {
            return new ItemStack(ACItemRegistry.PURE_DARKNESS.get());
        } else if (biome.is(ACBiomeRegistry.CANDY_CAVITY)) {
            return new ItemStack(ACItemRegistry.SWEET_TOOTH.get());
        } else if (biome.is(BiomeTags.IS_NETHER)) {
            return new ItemStack(Items.NETHERITE_INGOT);
        } else if (biome.is(BiomeTags.IS_END)) {
            return new ItemStack(Items.DRAGON_BREATH);
        } else {
            return new ItemStack(Items.DIAMOND);
        }
    }

    public void rerollWantedItem() {
        this.updateTopAndBottomBlocks();
        this.level.playSound(null, this.getBlockPos(), getFilledLevel() >= MAX_FILL_AMOUNT ? ACSoundRegistry.CONVERSION_CRUCIBLE_ACTIVATE.get() : ACSoundRegistry.CONVERSION_CRUCIBLE_ADD.get(), SoundSource.BLOCKS);
        if (this.isWitchMode()) {
            this.wantStack = new ItemStack(ACItemRegistry.LICOWITCH_RADIANT_ESSENCE.get());
        } else if (getFilledLevel() > MAX_FILL_AMOUNT - 2) {
            Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
            Optional<Holder.Reference<Biome>> biomeHolder = registry.getHolder(this.convertingToBiome);
            this.wantStack = biomeHolder.isPresent() ? getFinalSacrificeForBiome(biomeHolder.get()) : new ItemStack(Items.DIAMOND);
        } else {
            int choice = level.random.nextInt(2);
            switch (choice) {
                case 0:
                    this.wantStack = new ItemStack(topBlockForBiome.getBlock().asItem());
                    break;
                case 1:
                    this.wantStack = new ItemStack(middleBlockForBiome.getBlock().asItem());
                    break;
                case 2:
                    this.wantStack = new ItemStack(bottomBlockForBiome.getBlock().asItem());
                    break;
            }
        }
    }

    private class RecursiveBlockPlacement {
        private int placeIn;
        private final BlockPos pos;
        private final BlockState toPlace;

        public RecursiveBlockPlacement(int placeIn, BlockPos pos, BlockState toPlace) {
            this.placeIn = placeIn;
            this.pos = pos;
            this.toPlace = toPlace;
        }

        public int getPlaceIn() {
            return placeIn;
        }

        public void setPlaceIn(int placeIn) {
            this.placeIn = placeIn;
        }

        public BlockPos getPos() {
            return pos;
        }

        public BlockState getToPlace() {
            return toPlace;
        }
    }

    public static int calculateBiomeColor(Optional<Holder.Reference<Biome>> holder) {
        if (BIOME_COLORS.containsKey(holder)) {
            return BIOME_COLORS.get(holder);
        } else {
            int fogColor = holder.get().get().getFogColor();
            int color;
            if (ACBiomeRegistry.getBiomeTabletColor(holder.get().key()) != -1) {
                color = ACBiomeRegistry.getBiomeTabletColor(holder.get().key());
            } else if (fogColor == PLAINS_FOG_COLOR) {
                color = holder.get().get().getGrassColor(0.0D, 0.0D);
            } else {
                fogColor = 0xff000000 | fogColor;
                float[] hsb = Color.RGBtoHSB(fogColor >> 16 & 0xFF, fogColor >> 8 & 0xFF, fogColor & 0xFF, null);
                float saturationModifier = 1.0F;
                float brightnessModifier = 3.0F;
                color = Color.HSBtoRGB(hsb[0], Mth.clamp(hsb[1] * saturationModifier, 0, 1), Mth.clamp(hsb[2] * brightnessModifier, 0, 1));
            }
            BIOME_COLORS.put(holder, color);
            return color;
        }
    }
}

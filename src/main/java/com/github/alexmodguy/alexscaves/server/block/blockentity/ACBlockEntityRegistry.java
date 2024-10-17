package com.github.alexmodguy.alexscaves.server.block.blockentity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ACBlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AlexsCaves.MODID);

    public static final RegistryObject<BlockEntityType<VolcanicCoreBlockEntity>> VOLCANIC_CORE = DEF_REG.register("volcanic_core", () -> BlockEntityType.Builder.of(VolcanicCoreBlockEntity::new, ACBlockRegistry.VOLCANIC_CORE.get()).build(null));
    public static final RegistryObject<BlockEntityType<MagnetBlockEntity>> MAGNET = DEF_REG.register("magnet", () -> BlockEntityType.Builder.of(MagnetBlockEntity::new, ACBlockRegistry.SCARLET_MAGNET.get(), ACBlockRegistry.AZURE_MAGNET.get()).build(null));
    public static final RegistryObject<BlockEntityType<TeslaBulbBlockEntity>> TESLA_BULB = DEF_REG.register("tesla_bulb", () -> BlockEntityType.Builder.of(TeslaBulbBlockEntity::new, ACBlockRegistry.TESLA_BULB.get()).build(null));
    public static final RegistryObject<BlockEntityType<HologramProjectorBlockEntity>> HOLOGRAM_PROJECTOR = DEF_REG.register("hologram_projector", () -> BlockEntityType.Builder.of(HologramProjectorBlockEntity::new, ACBlockRegistry.HOLOGRAM_PROJECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<QuarryBlockEntity>> QUARRY = DEF_REG.register("quarry", () -> BlockEntityType.Builder.of(QuarryBlockEntity::new, ACBlockRegistry.QUARRY.get()).build(null));
    public static final RegistryObject<BlockEntityType<AmbersolBlockEntity>> AMBERSOL = DEF_REG.register("ambersol", () -> BlockEntityType.Builder.of(AmbersolBlockEntity::new, ACBlockRegistry.AMBERSOL.get()).build(null));
    public static final RegistryObject<BlockEntityType<AmberMonolithBlockEntity>> AMBER_MONOLITH = DEF_REG.register("amber_monolith", () -> BlockEntityType.Builder.of(AmberMonolithBlockEntity::new, ACBlockRegistry.AMBER_MONOLITH.get()).build(null));
    public static final RegistryObject<BlockEntityType<GeothermalVentBlockEntity>> GEOTHERMAL_VENT = DEF_REG.register("geothermal_vent", () -> BlockEntityType.Builder.of(GeothermalVentBlockEntity::new, ACBlockRegistry.GEOTHERMAL_VENT.get(), ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get(), ACBlockRegistry.GEOTHERMAL_VENT_THIN.get()).build(null));
    public static final RegistryObject<BlockEntityType<NuclearFurnaceBlockEntity>> NUCLEAR_FURNACE = DEF_REG.register("nuclear_furnace", () -> BlockEntityType.Builder.of(NuclearFurnaceBlockEntity::new, ACBlockRegistry.NUCLEAR_FURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<SirenLightBlockEntity>> SIREN_LIGHT = DEF_REG.register("siren_light", () -> BlockEntityType.Builder.of(SirenLightBlockEntity::new, ACBlockRegistry.SIREN_LIGHT.get()).build(null));
    public static final RegistryObject<BlockEntityType<NuclearSirenBlockEntity>> NUCLEAR_SIREN = DEF_REG.register("nuclear_siren", () -> BlockEntityType.Builder.of(NuclearSirenBlockEntity::new, ACBlockRegistry.NUCLEAR_SIREN.get()).build(null));
    public static final RegistryObject<BlockEntityType<MetalBarrelBlockEntity>> METAL_BARREL = DEF_REG.register("metal_barrel", () -> BlockEntityType.Builder.of(MetalBarrelBlockEntity::new, ACBlockRegistry.METAL_BARREL.get(), ACBlockRegistry.RUSTY_BARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssalAltarBlockEntity>> ABYSSAL_ALTAR = DEF_REG.register("abyssal_altar", () -> BlockEntityType.Builder.of(AbyssalAltarBlockEntity::new, ACBlockRegistry.ABYSSAL_ALTAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<CopperValveBlockEntity>> COPPER_VALVE = DEF_REG.register("copper_valve", () -> BlockEntityType.Builder.of(CopperValveBlockEntity::new, ACBlockRegistry.COPPER_VALVE.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnigmaticEngineBlockEntity>> ENIGMATIC_ENGINE = DEF_REG.register("enigmatic_engine", () -> BlockEntityType.Builder.of(EnigmaticEngineBlockEntity::new, ACBlockRegistry.ENIGMATIC_ENGINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BeholderBlockEntity>> BEHOLDER = DEF_REG.register("beholder", () -> BlockEntityType.Builder.of(BeholderBlockEntity::new, ACBlockRegistry.BEHOLDER.get()).build(null));
    public static final RegistryObject<BlockEntityType<GobthumperBlockEntity>> GOBTHUMPER = DEF_REG.register("gobthumper", () -> BlockEntityType.Builder.of(GobthumperBlockEntity::new, ACBlockRegistry.GOBTHUMPER.get()).build(null));
    public static final RegistryObject<BlockEntityType<ConversionCrucibleBlockEntity>> CONVERSION_CRUCIBLE = DEF_REG.register("conversion_crucible", () -> BlockEntityType.Builder.of(ConversionCrucibleBlockEntity::new, ACBlockRegistry.CONVERSION_CRUCIBLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<GingerbarrelBlockEntity>> GINGERBARREL = DEF_REG.register("gingerbarrel", () -> BlockEntityType.Builder.of(GingerbarrelBlockEntity::new, ACBlockRegistry.GINGERBARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<ConfectionOvenBlockEntity>> CONFECTION_OVEN = DEF_REG.register("confection_oven", () -> BlockEntityType.Builder.of(ConfectionOvenBlockEntity::new, ACBlockRegistry.CONFECTION_OVEN.get()).build(null));

    public static void expandVanillaDefinitions() {
        ImmutableSet.Builder<Block> validSignBlocks = new ImmutableSet.Builder<>();
        validSignBlocks.addAll(BlockEntityType.SIGN.validBlocks);
        validSignBlocks.add(ACBlockRegistry.PEWEN_SIGN.get());
        validSignBlocks.add(ACBlockRegistry.PEWEN_WALL_SIGN.get());
        validSignBlocks.add(ACBlockRegistry.THORNWOOD_SIGN.get());
        validSignBlocks.add(ACBlockRegistry.THORNWOOD_WALL_SIGN.get());
        BlockEntityType.SIGN.validBlocks = validSignBlocks.build();
        ImmutableSet.Builder<Block> validHangingSignBlocks = new ImmutableSet.Builder<>();
        validHangingSignBlocks.addAll(BlockEntityType.HANGING_SIGN.validBlocks);
        validHangingSignBlocks.add(ACBlockRegistry.PEWEN_HANGING_SIGN.get());
        validHangingSignBlocks.add(ACBlockRegistry.PEWEN_WALL_HANGING_SIGN.get());
        validHangingSignBlocks.add(ACBlockRegistry.THORNWOOD_HANGING_SIGN.get());
        validHangingSignBlocks.add(ACBlockRegistry.THORNWOOD_WALL_HANGING_SIGN.get());
        BlockEntityType.HANGING_SIGN.validBlocks = validHangingSignBlocks.build();
    }
}

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

    public static final RegistryObject<BlockEntityType<MagnetBlockEntity>> MAGNET = DEF_REG.register("magnet", () -> BlockEntityType.Builder.of(MagnetBlockEntity::new, ACBlockRegistry.SCARLET_MAGNET.get(), ACBlockRegistry.AZURE_MAGNET.get()).build(null));
    public static final RegistryObject<BlockEntityType<TeslaBulbBlockEntity>> TESLA_BULB = DEF_REG.register("tesla_bulb", () -> BlockEntityType.Builder.of(TeslaBulbBlockEntity::new, ACBlockRegistry.TESLA_BULB.get()).build(null));
    public static final RegistryObject<BlockEntityType<AmbersolBlockEntity>> AMBERSOL = DEF_REG.register("ambersol", () -> BlockEntityType.Builder.of(AmbersolBlockEntity::new, ACBlockRegistry.AMBERSOL.get()).build(null));
    public static final RegistryObject<BlockEntityType<GeothermalVentBlockEntity>> GEOTHERMAL_VENT = DEF_REG.register("geothermal_vent", () -> BlockEntityType.Builder.of(GeothermalVentBlockEntity::new, ACBlockRegistry.GEOTHERMAL_VENT.get(), ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get(), ACBlockRegistry.GEOTHERMAL_VENT_THIN.get()).build(null));
    public static final RegistryObject<BlockEntityType<MetalBarrelBlockEntity>> METAL_BARREL = DEF_REG.register("metal_barrel", () -> BlockEntityType.Builder.of(MetalBarrelBlockEntity::new, ACBlockRegistry.METAL_BARREL.get(), ACBlockRegistry.RUSTY_BARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<AbyssalAltarBlockEntity>> ABYSSAL_ALTAR = DEF_REG.register("abyssal_altar", () -> BlockEntityType.Builder.of(AbyssalAltarBlockEntity::new, ACBlockRegistry.ABYSSAL_ALTAR.get()).build(null));

    public static void expandVanillaDefinitions(){
        ImmutableSet.Builder<Block> list = new ImmutableSet.Builder<>();
        list.addAll(BlockEntityType.SIGN.validBlocks);
        list.add(ACBlockRegistry.PEWEN_SIGN.get());
        list.add(ACBlockRegistry.PEWEN_WALL_SIGN.get());
        BlockEntityType.SIGN.validBlocks = list.build();
    }
}

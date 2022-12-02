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
    public static final RegistryObject<BlockEntityType<AmbersolBlockEntity>> AMBERSOL = DEF_REG.register("ambersol", () -> BlockEntityType.Builder.of(AmbersolBlockEntity::new, ACBlockRegistry.AMBERSOL.get()).build(null));

    public static void expandVanillaDefinitions(){
        ImmutableSet.Builder<Block> list = new ImmutableSet.Builder<>();
        list.addAll(BlockEntityType.SIGN.validBlocks);
        list.add(ACBlockRegistry.PEWEN_SIGN.get());
        list.add(ACBlockRegistry.PEWEN_WALL_SIGN.get());
        BlockEntityType.SIGN.validBlocks = list.build();
    }
}

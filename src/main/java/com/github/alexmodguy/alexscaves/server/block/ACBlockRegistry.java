package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.grower.PewenGrower;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.BlockItemWithSupplierLore;
import com.github.alexthe666.citadel.item.BlockItemWithSupplier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ACBlockRegistry {

    public static final BlockBehaviour.Properties GALENA_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE, MaterialColor.METAL).requiresCorrectToolForDrops().strength(3.5F, 10.0F).sound(SoundType.DEEPSLATE);
    public static final BlockBehaviour.Properties LIMESTONE_PROPERTIES = BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_YELLOW).requiresCorrectToolForDrops().strength(1.2F, 4.5F).sound(SoundType.DRIPSTONE_BLOCK);
    public static final BlockBehaviour.Properties PEWEN_LOG_PROPERTIES = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sound(SoundType.WOOD);
    public static final BlockBehaviour.Properties PEWEN_PLANKS_PROPERTIES = BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW).strength(2.0F, 3.0F).sound(SoundType.WOOD);
    public static final WoodType PEWEN_WOOD_TYPE = WoodType.register(WoodType.create("alexscaves:pewen"));

    public static final DeferredRegister<Block> DEF_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, AlexsCaves.MODID);
    public static final RegistryObject<Block> GALENA = registerBlockAndItem("galena", () -> new Block(GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_STAIRS = registerBlockAndItem("galena_stairs", () -> new StairBlock(GALENA.get().defaultBlockState(), GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_SLAB = registerBlockAndItem("galena_slab", () -> new SlabBlock(GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_WALL = registerBlockAndItem("galena_wall", () -> new WallBlock(GALENA_PROPERTIES));
    public static final RegistryObject<Block> PACKED_GALENA = registerBlockAndItem("packed_galena", () -> new Block(GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_BRICKS = registerBlockAndItem("galena_bricks", () -> new Block(GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_BRICK_STAIRS = registerBlockAndItem("galena_brick_stairs", () -> new StairBlock(GALENA_BRICKS.get().defaultBlockState(), GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_BRICK_SLAB = registerBlockAndItem("galena_brick_slab", () -> new SlabBlock(GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_BRICK_WALL = registerBlockAndItem("galena_brick_wall", () -> new WallBlock(GALENA_PROPERTIES));
    public static final RegistryObject<Block> GALENA_IRON_ORE = registerBlockAndItem("galena_iron_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> SCRAP_METAL = registerBlockAndItem("scrap_metal", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5F, 15.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> METAL_SWARF = registerBlockAndItem("metal_swarf", () -> new MetalSwarfBlock(BlockBehaviour.Properties.of(Material.SAND, MaterialColor.STONE).strength(0.6F).sound(SoundType.SAND)));
    public static final RegistryObject<Block> SCARLET_NEODYMIUM_NODE = registerBlockAndItem("scarlet_neodymium_node", () -> new NeodymiumNodeBlock(false));
    public static final RegistryObject<Block> AZURE_NEODYMIUM_NODE = registerBlockAndItem("azure_neodymium_node", () -> new NeodymiumNodeBlock(true));
    public static final RegistryObject<Block> SCARLET_NEODYMIUM_PILLAR = registerBlockAndItem("scarlet_neodymium_pillar", () -> new NeodymiumPillarBlock(false));
    public static final RegistryObject<Block> AZURE_NEODYMIUM_PILLAR = registerBlockAndItem("azure_neodymium_pillar", () -> new NeodymiumPillarBlock(true));
    public static final RegistryObject<Block> BLOCK_OF_SCARLET_NEODYMIUM = registerBlockAndItem("block_of_scarlet_neodymium", () -> new NeodymiumOreBlock(false));
    public static final RegistryObject<Block> BLOCK_OF_AZURE_NEODYMIUM = registerBlockAndItem("block_of_azure_neodymium", () -> new NeodymiumOreBlock(true));
    public static final RegistryObject<Block> SCARLET_MAGNET = registerBlockAndItem("scarlet_magnet", () -> new MagnetBlock(false));
    public static final RegistryObject<Block> AZURE_MAGNET = registerBlockAndItem("azure_magnet", () -> new MagnetBlock(true));

    public static final RegistryObject<Block> LIMESTONE = registerBlockAndItem("limestone", () -> new Block(LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> LIMESTONE_STAIRS = registerBlockAndItem("limestone_stairs", () -> new StairBlock(LIMESTONE.get().defaultBlockState(), LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> LIMESTONE_SLAB = registerBlockAndItem("limestone_slab", () -> new SlabBlock(LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> LIMESTONE_WALL = registerBlockAndItem("limestone_wall", () -> new WallBlock(LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> SMOOTH_LIMESTONE = registerBlockAndItem("smooth_limestone", () -> new SmoothLimestoneBlock(LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> SMOOTH_LIMESTONE_STAIRS = registerBlockAndItem("smooth_limestone_stairs", () -> new StairBlock(SMOOTH_LIMESTONE.get().defaultBlockState(), LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> SMOOTH_LIMESTONE_SLAB = registerBlockAndItem("smooth_limestone_slab", () -> new SlabBlock(LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> SMOOTH_LIMESTONE_WALL = registerBlockAndItem("smooth_limestone_wall", () -> new WallBlock(LIMESTONE_PROPERTIES));
    public static final RegistryObject<Block> CAVE_PAINTING_AMBERSOL = registerBlockAndItem("cave_painting_ambersol", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_DARK = registerBlockAndItem("cave_painting_dark", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_FOOTPRINT = registerBlockAndItem("cave_painting_footprint", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_FOOTPRINTS = registerBlockAndItem("cave_painting_footprints", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_TREE_STARS = registerBlockAndItem("cave_painting_tree_stars", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_PEWEN = registerBlockAndItem("cave_painting_pewen", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_TRILOCARIS = registerBlockAndItem("cave_painting_trilocaris", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_GROTTOCERATOPS = registerBlockAndItem("cave_painting_grottoceratops", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_GROTTOCERATOPS_FRIEND = registerBlockAndItem("cave_painting_grottoceratops_friend", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_DINO_NUGGETS = registerBlockAndItem("cave_painting_dino_nuggets", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_VALLUMRAPTOR_CHEST = registerBlockAndItem("cave_painting_vallumraptor_chest", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_VALLUMRAPTOR_FRIEND = registerBlockAndItem("cave_painting_vallumraptor_friend", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_RELICHEIRUS = registerBlockAndItem("cave_painting_relicheirus", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_RELICHEIRUS_SLASH = registerBlockAndItem("cave_painting_relicheirus_slash", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_ENDERMAN = registerBlockAndItem("cave_painting_enderman", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_PORTAL = registerBlockAndItem("cave_painting_portal", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_SUBTERRANODON = registerBlockAndItem("cave_painting_subterranodon", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_SUBTERRANODON_RIDE = registerBlockAndItem("cave_painting_subterranodon_ride", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_TREMORSAURUS = registerBlockAndItem("cave_painting_tremorsaurus", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> CAVE_PAINTING_TREMORSAURUS_FRIEND = registerBlockAndItem("cave_painting_tremorsaurus_friend", () -> new CavePaintingBlock(), true);
    public static final RegistryObject<Block> AMBER = registerBlockAndItem("amber", () -> new GlassBlock(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.COLOR_ORANGE).noOcclusion().requiresCorrectToolForDrops().strength(0.3F, 2.0F).sound(SoundType.GLASS)));
    public static final RegistryObject<Block> AMBERSOL = registerBlockAndItem("ambersol", () -> new AmbersolBlock());
    public static final RegistryObject<Block> AMBERSOL_LIGHT = DEF_REG.register("ambersol_light", () -> new AmbersolLightBlock(BlockBehaviour.Properties.of(Material.AIR).noOcclusion().strength(-1.0F, 3600000.8F).noLootTable().noOcclusion().lightLevel(((state -> 15)))));
    public static final RegistryObject<Block> PEWEN_LOG = registerBlockAndItem("pewen_log", () -> new RotatedPillarBlock(PEWEN_LOG_PROPERTIES));
    public static final RegistryObject<Block> PEWEN_WOOD = registerBlockAndItem("pewen_wood", () -> new RotatedPillarBlock(PEWEN_LOG_PROPERTIES));
    public static final RegistryObject<Block> STRIPPED_PEWEN_LOG = registerBlockAndItem("stripped_pewen_log", () -> new RotatedPillarBlock(PEWEN_LOG_PROPERTIES));
    public static final RegistryObject<Block> STRIPPED_PEWEN_WOOD = registerBlockAndItem("stripped_pewen_wood", () -> new RotatedPillarBlock(PEWEN_LOG_PROPERTIES));
    public static final RegistryObject<Block> PEWEN_PLANKS = registerBlockAndItem("pewen_planks", () -> new Block(PEWEN_PLANKS_PROPERTIES));
    public static final RegistryObject<Block> PEWEN_PLANKS_STAIRS = registerBlockAndItem("pewen_stairs", () -> new StairBlock(PEWEN_PLANKS.get().defaultBlockState(), PEWEN_PLANKS_PROPERTIES));
    public static final RegistryObject<Block> PEWEN_PLANKS_SLAB = registerBlockAndItem("pewen_slab", () -> new SlabBlock(PEWEN_PLANKS_PROPERTIES));
    public static final RegistryObject<Block> PEWEN_PLANKS_FENCE = registerBlockAndItem("pewen_fence", () -> new FenceBlock(PEWEN_PLANKS_PROPERTIES));
    public static final RegistryObject<Block> PEWEN_SIGN = DEF_REG.register("pewen_sign", () -> new StandingSignBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).noCollission().strength(1.0F).sound(SoundType.WOOD), PEWEN_WOOD_TYPE));
    public static final RegistryObject<Block> PEWEN_WALL_SIGN = DEF_REG.register("pewen_wall_sign", () -> new WallSignBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).noCollission().strength(1.0F).sound(SoundType.WOOD), PEWEN_WOOD_TYPE));
    public static final RegistryObject<Block> PEWEN_PRESSURE_PLATE = registerBlockAndItem("pewen_pressure_plate",  () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.WOOD, PEWEN_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD), SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON));
    public static final RegistryObject<Block> PEWEN_TRAPDOOR = registerBlockAndItem("pewen_trapdoor",  () -> new TrapDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN));
    public static final RegistryObject<Block> PEWEN_BUTTON = registerBlockAndItem("pewen_button", () -> new ButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD), 30, true, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON));
    public static final RegistryObject<Block> PEWEN_FENCE_GATE = registerBlockAndItem("pewen_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of(Material.WOOD, PEWEN_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD), SoundEvents.FENCE_GATE_CLOSE, SoundEvents.FENCE_GATE_OPEN));
    public static final RegistryObject<Block> PEWEN_DOOR = DEF_REG.register("pewen_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.WOOD, PEWEN_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion(), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN));
    public static final RegistryObject<Block> PEWEN_BRANCH = registerBlockAndItem("pewen_branch", () -> new PewenBranchBlock());
    public static final RegistryObject<Block> PEWEN_PINES = registerBlockAndItem("pewen_pines", () -> new PewenPinesBlock());
    public static final RegistryObject<Block> PEWEN_SAPLING = registerBlockAndItem("pewen_sapling", () -> new SaplingBlock(new PewenGrower(), BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> CURLY_FERN = registerBlockAndItem("curly_fern", () -> new DoublePlantWithRotationBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)));
    public static final RegistryObject<Block> FLYTRAP = registerBlockAndItem("flytrap", () -> new FlytrapBlock());
    public static final RegistryObject<Block> CYCAD = registerBlockAndItem("cycad", () -> new CycadBlock());
    public static final RegistryObject<Block> ARCHAIC_VINE = registerBlockAndItem("archaic_vine", () -> new ArchaicVineBlock());
    public static final RegistryObject<Block> ARCHAIC_VINE_PLANT = DEF_REG.register("archaic_vine_plant", () -> new ArchaicVinePlantBlock());
    public static final RegistryObject<Block> ANCIENT_LEAVES = registerBlockAndItem("ancient_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion()));
    public static final RegistryObject<Block> TREE_STAR = registerBlockAndItem("tree_star", () -> new TreeStarBlock());


    public static RegistryObject<Block> registerBlockAndItem(String name, Supplier<Block> block){
        return registerBlockAndItem(name, block, false);
    }

    public static RegistryObject<Block> registerBlockAndItem(String name, Supplier<Block> block, boolean itemLore){
        RegistryObject<Block> blockObj = DEF_REG.register(name, block);
        //TODO
        ACItemRegistry.DEF_REG.register(name, () -> itemLore ?  new BlockItemWithSupplierLore(blockObj, new Item.Properties()) : new BlockItemWithSupplier(blockObj, new Item.Properties()));
        return blockObj;
    }
}

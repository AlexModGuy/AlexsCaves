package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.*;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ACEntityRegistry {


    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AlexsCaves.MODID);
    public static  final MobCategory CAVE_CREATURE = MobCategory.create("cave_creature", "alexscaves:cave_creature", 10, true, true, 128);
    public static final RegistryObject<EntityType<MovingMetalBlockEntity>> MOVING_METAL_BLOCK = DEF_REG.register("moving_metal_block", () -> (EntityType)EntityType.Builder.of(MovingMetalBlockEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).setCustomClientFactory(MovingMetalBlockEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("moving_metal_block"));
    public static final RegistryObject<EntityType<TeletorEntity>> TELETOR = DEF_REG.register("teletor", () -> (EntityType)EntityType.Builder.of(TeletorEntity::new, MobCategory.MONSTER).sized(0.99F, 1.99F).build("teletor"));
    public static final RegistryObject<EntityType<MagneticWeaponEntity>> MAGNETIC_WEAPON = DEF_REG.register("magnetic_weapon", () -> (EntityType)EntityType.Builder.of(MagneticWeaponEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).setCustomClientFactory(MagneticWeaponEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).clientTrackingRange(20).build("magnetic_weapon"));
    public static final RegistryObject<EntityType<MagnetronEntity>> MAGNETRON = DEF_REG.register("magnetron", () -> (EntityType)EntityType.Builder.of(MagnetronEntity::new, MobCategory.MONSTER).sized(0.99F, 2.3F).build("magnetron"));
    public static final RegistryObject<EntityType<BoundroidEntity>> BOUNDROID = DEF_REG.register("boundroid", () -> (EntityType)EntityType.Builder.of(BoundroidEntity::new, MobCategory.MONSTER).sized(1.4F, 0.75F).build("boundroid"));
    public static final RegistryObject<EntityType<FerrouslimeEntity>> FERROUSLIME = DEF_REG.register("ferrouslime", () -> (EntityType)EntityType.Builder.of(FerrouslimeEntity::new, MobCategory.MONSTER).sized(0.85F, 0.85F).build("ferrouslime"));
    public static final RegistryObject<EntityType<BoundroidWinchEntity>> BOUNDROID_WINCH = DEF_REG.register("boundroid_winch", () -> (EntityType)EntityType.Builder.of(BoundroidWinchEntity::new, MobCategory.MONSTER).sized(0.8F, 1.4F).build("boundroid_winch"));
    public static final RegistryObject<EntityType<SubterranodonEntity>> SUBTERRANODON = DEF_REG.register("subterranodon", () -> (EntityType)EntityType.Builder.of(SubterranodonEntity::new, CAVE_CREATURE).sized(1.75F, 1.2F).setTrackingRange(12).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build("subterranodon"));
    public static final RegistryObject<EntityType<VallumraptorEntity>> VALLUMRAPTOR = DEF_REG.register("vallumraptor", () -> (EntityType)EntityType.Builder.of(VallumraptorEntity::new, CAVE_CREATURE).sized(0.8F, 1.5F).setTrackingRange(8).build("vallumraptor"));
    public static final RegistryObject<EntityType<GrottoceratopsEntity>> GROTTOCERATOPS = DEF_REG.register("grottoceratops", () -> (EntityType)EntityType.Builder.of(GrottoceratopsEntity::new, CAVE_CREATURE).sized(2.3F, 2.5F).setTrackingRange(8).build("grottoceratops"));
    public static final RegistryObject<EntityType<TrilocarisEntity>> TRILOCARIS = DEF_REG.register("trilocaris", () -> (EntityType)EntityType.Builder.of(TrilocarisEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.4F).build("trilocaris"));
    public static final RegistryObject<EntityType<TremorsaurusEntity>> TREMORSAURUS = DEF_REG.register("tremorsaurus", () -> (EntityType)EntityType.Builder.of(TremorsaurusEntity::new, CAVE_CREATURE).sized(2.5F, 3.85F).setTrackingRange(8).build("tremorsaurus"));
    public static final RegistryObject<EntityType<RelicheirusEntity>> RELICHEIRUS = DEF_REG.register("relicheirus", () -> (EntityType)EntityType.Builder.of(RelicheirusEntity::new, CAVE_CREATURE).sized(2.65F, 5.9F).setTrackingRange(9).build("relicheirus"));
    public static final RegistryObject<EntityType<FallingTreeBlockEntity>> FALLING_TREE_BLOCK = DEF_REG.register("falling_tree_block", () -> (EntityType)EntityType.Builder.of(FallingTreeBlockEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).setCustomClientFactory(FallingTreeBlockEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("falling_tree_block"));
    public static final RegistryObject<EntityType<NuclearExplosionEntity>> NUCLEAR_EXPLOSION = DEF_REG.register("nuclear_explosion", () -> (EntityType)EntityType.Builder.of(NuclearExplosionEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).setCustomClientFactory(NuclearExplosionEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("nuclear_explosion"));
    public static final RegistryObject<EntityType<NuclearBombEntity>> NUCLEAR_BOMB = DEF_REG.register("nuclear_bomb", () -> (EntityType)EntityType.Builder.of(NuclearBombEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).setCustomClientFactory(NuclearBombEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("nuclear_bomb"));
    public static final RegistryObject<EntityType<NucleeperEntity>> NUCLEEPER = DEF_REG.register("nucleeper", () -> (EntityType)EntityType.Builder.of(NucleeperEntity::new, MobCategory.MONSTER).sized(0.98F, 3.95F).build("nucleeper"));
    public static final RegistryObject<EntityType<RadgillEntity>> RADGILL = DEF_REG.register("radgill", () -> (EntityType)EntityType.Builder.of(RadgillEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F).build("radgill"));
    public static final RegistryObject<EntityType<BrainiacEntity>> BRAINIAC = DEF_REG.register("brainiac", () -> (EntityType)EntityType.Builder.of(BrainiacEntity::new, MobCategory.MONSTER).sized(1.3F, 2.5F).build("brainiac"));
    public static final RegistryObject<EntityType<ThrownWasteDrumEntity>> THROWN_WASTE_DRUM = DEF_REG.register("thrown_waste_drum", () -> (EntityType)EntityType.Builder.of(ThrownWasteDrumEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).setCustomClientFactory(ThrownWasteDrumEntity::new).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true).updateInterval(10).clientTrackingRange(20).build("thrown_waste_drum"));
    public static final RegistryObject<EntityType<GammaroachEntity>> GAMMAROACH = DEF_REG.register("gammaroach", () -> (EntityType)EntityType.Builder.of(GammaroachEntity::new, MobCategory.AMBIENT).sized(1.25F, 0.9F).build("gammaroach"));
    public static final RegistryObject<EntityType<RaycatEntity>> RAYCAT = DEF_REG.register("raycat", () -> (EntityType)EntityType.Builder.of(RaycatEntity::new, CAVE_CREATURE).sized(0.85F, 0.6F).build("raycat"));
    public static final RegistryObject<EntityType<LanternfishEntity>> LANTERNFISH = DEF_REG.register("lanternfish", () -> (EntityType)EntityType.Builder.of(LanternfishEntity::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).build("lanternfish"));

    public static final RegistryObject<EntityType<CinderBrickEntity>> CINDER_BRICK = DEF_REG.register("cinder_brick", () -> (EntityType)EntityType.Builder.of(CinderBrickEntity::new, MobCategory.MISC).sized(0.4F, 0.4F).setCustomClientFactory(CinderBrickEntity::new).setUpdateInterval(1).build("cinder_brick"));

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(TELETOR.get(), TeletorEntity.createAttributes().build());
        event.put(MAGNETRON.get(), MagnetronEntity.createAttributes().build());
        event.put(BOUNDROID.get(), BoundroidEntity.createAttributes().build());
        event.put(BOUNDROID_WINCH.get(), BoundroidEntity.createAttributes().build());
        event.put(FERROUSLIME.get(), FerrouslimeEntity.createAttributes().build());
        event.put(SUBTERRANODON.get(), SubterranodonEntity.createAttributes().build());
        event.put(VALLUMRAPTOR.get(), VallumraptorEntity.createAttributes().build());
        event.put(GROTTOCERATOPS.get(), GrottoceratopsEntity.createAttributes().build());
        event.put(TRILOCARIS.get(), TrilocarisEntity.createAttributes().build());
        event.put(TREMORSAURUS.get(), TremorsaurusEntity.createAttributes().build());
        event.put(RELICHEIRUS.get(), RelicheirusEntity.createAttributes().build());
        event.put(NUCLEEPER.get(), NucleeperEntity.createAttributes().build());
        event.put(RADGILL.get(), NucleeperEntity.createAttributes().build());
        event.put(BRAINIAC.get(), BrainiacEntity.createAttributes().build());
        event.put(GAMMAROACH.get(), GammaroachEntity.createAttributes().build());
        event.put(RAYCAT.get(), RaycatEntity.createAttributes().build());
        event.put(LANTERNFISH.get(), LanternfishEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnPlacements(SpawnPlacementRegisterEvent event){
        SpawnPlacements.Type inAcid = SpawnPlacements.Type.create("in_acid", (levelReader, blockPos, entityType) -> !levelReader.getFluidState(blockPos).isEmpty() && levelReader.getFluidState(blockPos).getFluidType() == ACFluidRegistry.ACID_FLUID_TYPE.get());
        event.register(TELETOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TeletorEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(MAGNETRON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MagnetronEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(BOUNDROID.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BoundroidEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(FERROUSLIME.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FerrouslimeEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(SUBTERRANODON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SubterranodonEntity::checkSubterranodonSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(VALLUMRAPTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VallumraptorEntity::checkPrehistoricSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(GROTTOCERATOPS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GrottoceratopsEntity::checkPrehistoricSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(TRILOCARIS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TrilocarisEntity::checkTrilocarisSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(TREMORSAURUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TremorsaurusEntity::checkPrehistoricSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(RELICHEIRUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RelicheirusEntity::checkPrehistoricSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(NUCLEEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NucleeperEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(RADGILL.get(), inAcid, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RadgillEntity::checkRadgillSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(BRAINIAC.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BrainiacEntity::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(GAMMAROACH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GammaroachEntity::checkGammaroachSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(RAYCAT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RaycatEntity::checkRaycatSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(LANTERNFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LanternfishEntity::checkLanternfishSpawnRules, SpawnPlacementRegisterEvent.Operation.AND);
    }
}


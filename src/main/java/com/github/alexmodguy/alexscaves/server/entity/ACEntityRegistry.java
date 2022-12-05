package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
    public static final RegistryObject<EntityType<SubterranodonEntity>> SUBTERRANODON = DEF_REG.register("subterranodon", () -> (EntityType)EntityType.Builder.of(SubterranodonEntity::new, CAVE_CREATURE).sized(1.75F, 1.2F).setTrackingRange(12).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1).build("subterranodon"));
    public static final RegistryObject<EntityType<VallumraptorEntity>> VALLUMRAPTOR = DEF_REG.register("vallumraptor", () -> (EntityType)EntityType.Builder.of(VallumraptorEntity::new, CAVE_CREATURE).sized(0.8F, 1.5F).setTrackingRange(8).build("vallumraptor"));
    public static final RegistryObject<EntityType<GrottoceratopsEntity>> GROTTOCERATOPS = DEF_REG.register("grottoceratops", () -> (EntityType)EntityType.Builder.of(GrottoceratopsEntity::new, CAVE_CREATURE).sized(2.3F, 2.5F).setTrackingRange(8).build("grottoceratops"));
    public static final RegistryObject<EntityType<TrilocarisEntity>> TRILOCARIS = DEF_REG.register("trilocaris", () -> (EntityType)EntityType.Builder.of(TrilocarisEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.4F).build("trilocaris"));

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(TELETOR.get(), TeletorEntity.createAttributes().build());
        event.put(MAGNETRON.get(), MagnetronEntity.createAttributes().build());
        event.put(SUBTERRANODON.get(), SubterranodonEntity.createAttributes().build());
        event.put(VALLUMRAPTOR.get(), VallumraptorEntity.createAttributes().build());
        event.put(GROTTOCERATOPS.get(), GrottoceratopsEntity.createAttributes().build());
        event.put(TRILOCARIS.get(), TrilocarisEntity.createAttributes().build());
        SpawnPlacements.register(TELETOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TeletorEntity::checkMonsterSpawnRules);
        SpawnPlacements.register(MAGNETRON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MagnetronEntity::checkMonsterSpawnRules);
        SpawnPlacements.register(SUBTERRANODON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SubterranodonEntity::checkSubterranodonSpawnRules);
        SpawnPlacements.register(VALLUMRAPTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VallumraptorEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(GROTTOCERATOPS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GrottoceratopsEntity::checkPrehistoricSpawnRules);
        SpawnPlacements.register(TRILOCARIS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TrilocarisEntity::checkTrilocarisSpawnRules);
    }
}

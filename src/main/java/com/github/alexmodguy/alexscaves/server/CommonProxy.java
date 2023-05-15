package com.github.alexmodguy.alexscaves.server;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public void init() {
    }

    public void clientInit() {
    }

    @SubscribeEvent
    public void resizeEntity(EntityEvent.Size event) {
        if (event.getEntity() instanceof MagneticEntityAccessor magnet && event.getEntity().getEntityData().isDirty()) {
            Direction dir = magnet.getMagneticAttachmentFace();
            float defaultHeight = event.getEntity().getDimensions(Pose.STANDING).height;
            float defaultWidth = event.getEntity().getDimensions(Pose.STANDING).width;
            float defaultEyeHeight = event.getEntity().getEyeHeight(Pose.STANDING);
            if (dir == Direction.DOWN && event.getEntity() instanceof Player && event.getEntity().getPose() == Pose.STANDING) {
                event.setNewEyeHeight(defaultEyeHeight);
            } else if (dir == Direction.UP) {
                event.setNewEyeHeight(defaultHeight - defaultEyeHeight);
            } else if (dir.getAxis() != Direction.Axis.Y) {
                event.setNewEyeHeight(0.0F);
            }
        }
    }

    @SubscribeEvent
    public void livingDie(LivingDeathEvent event) {
        if (event.getEntity().getType() == EntityType.MAGMA_CUBE && event.getSource() != null && event.getSource().getEntity() instanceof Frog frog) {
            if (frog.getVariant() == ACFrogRegistry.PRIMORDIAL.get()) {
                event.getEntity().spawnAtLocation(new ItemStack(ACBlockRegistry.AMBER.get()));
            }
        }
    }

    @SubscribeEvent
    public void livingHeal(LivingHealEvent event) {
        if (event.getEntity().hasEffect(ACEffectRegistry.IRRADIATED.get()) && !event.getEntity().getType().is(ACTagRegistry.RESISTS_RADIATION)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().hasEffect(ACEffectRegistry.BUBBLED.get()) && event.getEntity().isInFluidType()) {
            event.getEntity().removeEffect(ACEffectRegistry.BUBBLED.get());
        }
        if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.DIVING_HELMET.get())  && !event.getEntity().isEyeInFluid(FluidTags.WATER)) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 810,  0, false, false, true));
        }
        //TODO: figure out why sometimes items are 1 air on the server side for players
        //System.out.println(event.getEntity().getItemInHand(InteractionHand.MAIN_HAND));
    }

    @SubscribeEvent
    public void onEntityJoinWorld(MobSpawnEvent.FinalizeSpawn event) {
        try {
            if (event.getEntity() instanceof Creeper creeper) {
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, RaycatEntity.class, 10.0F, 1.0D, 1.2D));
            }
            if (event.getEntity() instanceof Drowned drowned && drowned.level.getBiome(drowned.blockPosition()).is(ACBiomeRegistry.ABYSSAL_CHASM)) {
                if(drowned.getItemBySlot(EquipmentSlot.FEET).isEmpty() && drowned.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && drowned.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && drowned.getItemBySlot(EquipmentSlot.HEAD).isEmpty()){
                    if(drowned.getRandom().nextFloat() < 0.2){
                        drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ACItemRegistry.DIVING_HELMET.get()));
                        drowned.setDropChance(EquipmentSlot.HEAD, 0.5F);
                    }
                    if(drowned.getRandom().nextFloat() < 0.2){
                        drowned.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ACItemRegistry.DIVING_CHESTPLATE.get()));
                        drowned.setDropChance(EquipmentSlot.CHEST, 0.5F);
                    }
                    if(drowned.getRandom().nextFloat() < 0.2){
                        drowned.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ACItemRegistry.DIVING_LEGGINGS.get()));
                        drowned.setDropChance(EquipmentSlot.LEGS, 0.5F);
                    }
                    if(drowned.getRandom().nextFloat() < 0.2){
                        drowned.setItemSlot(EquipmentSlot.FEET, new ItemStack(ACItemRegistry.DIVING_BOOTS.get()));
                        drowned.setDropChance(EquipmentSlot.FEET, 0.5F);
                    }
                }
            }
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onReplaceBiome(EventReplaceBiome event) {
        ResourceKey<Biome> biome = BiomeGenerationConfig.getBiomeForEvent(event);
        if (biome != null) {
            event.setResult(Event.Result.ALLOW);
            event.setBiomeToGenerate(event.getBiomeSource().getResourceKeyMap().get(biome));
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.isCreative()) {
            if (event.player.getItemInHand(InteractionHand.MAIN_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
                checkAndDestroyExploitItem(event.player, EquipmentSlot.MAINHAND);
            }
            if (event.player.getItemInHand(InteractionHand.OFF_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
                checkAndDestroyExploitItem(event.player, EquipmentSlot.OFFHAND);
            }
        }
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if ((event.getName().equals(BuiltInLootTables.BASTION_TREASURE) || event.getName().equals(BuiltInLootTables.BASTION_OTHER) || event.getName().equals(BuiltInLootTables.BASTION_BRIDGE)) && AlexsCaves.COMMON_CONFIG.magneticTabletLootChance.get() > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putString("CaveBiome", ACBiomeRegistry.MAGNETIC_CAVES.location().toString());
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET.get()).setWeight(1).apply(SetNbtFunction.setTag(tag));
            LootPool.Builder builder = new LootPool.Builder().name("ac_magnetic_tablet").add(item).when(LootItemRandomChanceCondition.randomChance(AlexsCaves.COMMON_CONFIG.magneticTabletLootChance.get().floatValue())).setRolls(UniformGenerator.between(0, 1)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
        if (event.getName().getPath().contains("archaeology") && AlexsCaves.COMMON_CONFIG.primordialTabletLootChance.get() > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putString("CaveBiome", ACBiomeRegistry.PRIMORDIAL_CAVES.location().toString());
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET.get()).setWeight(1).apply(SetNbtFunction.setTag(tag));
            LootPool.Builder builder = new LootPool.Builder().name("ac_primordial_tablet").add(item).when(LootItemRandomChanceCondition.randomChance(AlexsCaves.COMMON_CONFIG.primordialTabletLootChance.get().floatValue())).setRolls(UniformGenerator.between(0, 1)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
        if (event.getName().equals(BuiltInLootTables.JUNGLE_TEMPLE) && AlexsCaves.COMMON_CONFIG.toxicTabletLootChance.get() > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putString("CaveBiome", ACBiomeRegistry.TOXIC_CAVES.location().toString());
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET.get()).setWeight(1).apply(SetNbtFunction.setTag(tag));
            LootPool.Builder builder = new LootPool.Builder().name("ac_toxic_tablet").add(item).when(LootItemRandomChanceCondition.randomChance(AlexsCaves.COMMON_CONFIG.toxicTabletLootChance.get().floatValue())).setRolls(UniformGenerator.between(0, 1)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
        if ((event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_BIG) || event.getName().equals(BuiltInLootTables.UNDERWATER_RUIN_SMALL) || event.getName().equals(BuiltInLootTables.BURIED_TREASURE)) && AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance.get() > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putString("CaveBiome", ACBiomeRegistry.ABYSSAL_CHASM.location().toString());
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET.get()).setWeight(1).apply(SetNbtFunction.setTag(tag));
            LootPool.Builder builder = new LootPool.Builder().name("ac_abyssal_tablet").add(item).when(LootItemRandomChanceCondition.randomChance(AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance.get().floatValue())).setRolls(UniformGenerator.between(0, 1)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
        if (event.getName().equals(BuiltInLootTables.WOODLAND_MANSION) && AlexsCaves.COMMON_CONFIG.forlornTabletLootChance.get() > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putString("CaveBiome", ACBiomeRegistry.FORLORN_HOLLOWS.location().toString());
            LootPoolEntryContainer.Builder item = LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET.get()).setWeight(1).apply(SetNbtFunction.setTag(tag));
            LootPool.Builder builder = new LootPool.Builder().name("ac_forlorn_tablet").add(item).when(LootItemRandomChanceCondition.randomChance(AlexsCaves.COMMON_CONFIG.forlornTabletLootChance.get().floatValue())).setRolls(UniformGenerator.between(0, 1)).setBonusRolls(UniformGenerator.between(0, 1));
            event.getTable().addPool(builder.build());
        }
    }

    private static void checkAndDestroyExploitItem(Player player, EquipmentSlot slot) {
        ItemStack itemInHand = player.getItemBySlot(slot);
        if (itemInHand.is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
            boolean flag = false;
            CompoundTag tag = itemInHand.getTag();
            if (tag != null && tag.contains("BiomeKey")) {
                String biomeKey = tag.getString("BiomeKey");
                if (biomeKey.contains("alexscaves:")) {
                    flag = true;
                }
            }
            if (flag) {
                itemInHand.shrink(1);
                player.broadcastBreakEvent(slot);
                player.playSound(ACSoundRegistry.DISAPPOINTMENT.get());
                if (!player.level.isClientSide) {
                    player.displayClientMessage(Component.translatable("item.alexscaves.natures_compass_warning"), true);
                }
            }
        }
    }

    public void blockRenderingEntity(UUID id) {
    }

    public void releaseRenderingEntity(UUID id) {
    }

    public void setVisualFlag(int flag) {
    }

    public Player getClientSidePlayer() {
        return null;
    }

    public boolean isKeyDown(int keyType) {
        return false;
    }

    public boolean checkIfParticleAt(SimpleParticleType simpleParticleType, BlockPos at) {
        return false;
    }

    public float getPartialTicks() {
        return 1.0F;
    }

    public Object getISTERProperties() {
        return null;
    }

    public Object getArmorProperties() {
        return null;
    }

    public void setSpelunkeryTutorialComplete(boolean completedTutorial) {
    }

    public boolean isSpelunkeryTutorialComplete() {
        return true;
    }
}

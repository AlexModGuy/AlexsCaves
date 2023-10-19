package com.github.alexmodguy.alexscaves.server.event;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.VallumraptorEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.FlyingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.VillagerUndergroundCabinMapTrade;
import com.github.alexmodguy.alexscaves.server.entity.util.WatcherPossessionAccessor;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class CommonEvents {

    @SubscribeEvent
    public void resizeEntity(EntityEvent.Size event) {
        if (event.getEntity() instanceof MagneticEntityAccessor magnet && event.getEntity().getEntityData().isDirty()) {
            Direction dir = magnet.getMagneticAttachmentFace();
            float defaultHeight = event.getOriginalSize().height;
            float defaultWidth = event.getOriginalSize().width;
            float defaultEyeHeight = defaultHeight * 0.85F;
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
                event.getEntity().spawnAtLocation(new ItemStack(ACBlockRegistry.CARMINE_FROGLIGHT.get()));
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
    public void livingFindTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && event.getNewTarget() instanceof VallumraptorEntity vallumraptor && vallumraptor.getHideFor() > 0) {
            mob.setTarget(null);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void livingHurt(LivingDamageEvent event) {
        if (event.getEntity().isPassenger() && event.getEntity() instanceof FlyingMount && (event.getSource().is(DamageTypes.IN_WALL) || event.getSource().is(DamageTypes.FALL) || event.getSource().is(DamageTypes.FLY_INTO_WALL))) {
            event.setCanceled(true);
        }
        if(event.getEntity() instanceof WatcherPossessionAccessor possessed && possessed.isPossessedByWatcher() && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !(event.getSource().getEntity() instanceof WatcherEntity)){
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public void livingAttack(LivingAttackEvent event) {
        if (event.getSource() != null && event.getSource().getDirectEntity() instanceof LivingEntity directSource && directSource.hasEffect(ACEffectRegistry.STUNNED.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void playerAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof DinosaurEntity && event.getEntity().isPassengerOfSameVehicle(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().hasEffect(ACEffectRegistry.BUBBLED.get()) && event.getEntity().isInFluidType()) {
            event.getEntity().removeEffect(ACEffectRegistry.BUBBLED.get());
        }
        if (event.getEntity().hasEffect(ACEffectRegistry.DARKNESS_INCARNATE.get()) && event.getEntity().tickCount % 5 == 0 && DarknessIncarnateEffect.isInLight(event.getEntity(), 11)) {
            event.getEntity().removeEffect(ACEffectRegistry.DARKNESS_INCARNATE.get());
        }
        if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(ACItemRegistry.DIVING_HELMET.get()) && !event.getEntity().isEyeInFluid(FluidTags.WATER)) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 810, 0, false, false, true));
        }
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Mob mob && mob.getTarget() instanceof VallumraptorEntity vallumraptor && vallumraptor.getHideFor() > 0) {
            mob.setTarget(null);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(MobSpawnEvent.FinalizeSpawn event) {
        try {
            if (event.getEntity() instanceof Creeper creeper) {
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, RaycatEntity.class, 10.0F, 1.0D, 1.2D));
            }
            if (event.getEntity() instanceof Drowned drowned && drowned.level().getBiome(drowned.blockPosition()).is(ACBiomeRegistry.ABYSSAL_CHASM)) {
                if (drowned.getItemBySlot(EquipmentSlot.FEET).isEmpty() && drowned.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && drowned.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && drowned.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                    if (drowned.getRandom().nextFloat() < 0.2) {
                        drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ACItemRegistry.DIVING_HELMET.get()));
                        drowned.setDropChance(EquipmentSlot.HEAD, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < 0.2) {
                        drowned.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ACItemRegistry.DIVING_CHESTPLATE.get()));
                        drowned.setDropChance(EquipmentSlot.CHEST, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < 0.2) {
                        drowned.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ACItemRegistry.DIVING_LEGGINGS.get()));
                        drowned.setDropChance(EquipmentSlot.LEGS, 0.5F);
                    }
                    if (drowned.getRandom().nextFloat() < 0.2) {
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
    public void livingRemoveEffect(MobEffectEvent.Remove event) {
        if(event.getEffect() instanceof DarknessIncarnateEffect darknessIncarnateEffect){
            darknessIncarnateEffect.toggleFlight(event.getEntity(), false);
            event.getEntity().playSound(ACSoundRegistry.DARKNESS_INCARNATE_EXIT.get());
        }
    }


    @SubscribeEvent
    public void livingAddEffect(MobEffectEvent.Added event) {
        if(event.getEffectInstance().getEffect() instanceof DarknessIncarnateEffect){
            event.getEntity().playSound(ACSoundRegistry.DARKNESS_INCARNATE_ENTER.get());
        }
    }

    @SubscribeEvent
    public void livingExpireEffect(MobEffectEvent.Expired event) {
        if(event.getEffectInstance().getEffect() instanceof DarknessIncarnateEffect darknessIncarnateEffect){
            darknessIncarnateEffect.toggleFlight(event.getEntity(), false);
            event.getEntity().playSound(ACSoundRegistry.DARKNESS_INCARNATE_EXIT.get());
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
    public void onVillagerTradeSetup(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.CARTOGRAPHER && AlexsCaves.COMMON_CONFIG.cartographersSellCabinMaps.get()) {
            int level = 2;
            List<VillagerTrades.ItemListing> list = event.getTrades().get(level);
            list.add(new VillagerUndergroundCabinMapTrade(5, 10, 6));
            event.getTrades().put(level, list);
        }
    }

    @SubscribeEvent
    public void onWanderingTradeSetup(WandererTradesEvent event) {
        if(AlexsCaves.COMMON_CONFIG.wanderingTradersSellCabinMaps.get()){
            event.getGenericTrades().add(new VillagerUndergroundCabinMapTrade(8, 1, 10));
        }
    }

    private static void checkAndDestroyExploitItem(Player player, EquipmentSlot slot) {
        ItemStack itemInHand = player.getItemBySlot(slot);
        if (itemInHand.is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)) {
            CompoundTag tag = itemInHand.getTag();
            if(tag != null){
                if (itemTagContainsAC(tag, "BiomeKey", false) || itemTagContainsAC(tag, "Structure", true) || itemTagContainsAC(tag, "structurecompass:structureName", true)) {
                    itemInHand.shrink(1);
                    player.broadcastBreakEvent(slot);
                    player.playSound(ACSoundRegistry.DISAPPOINTMENT.get());
                    if (!player.level().isClientSide) {
                        player.displayClientMessage(Component.translatable("item.alexscaves.natures_compass_warning"), true);
                    }
                }
            }
        }
    }

    private static boolean itemTagContainsAC(CompoundTag tag, String tagID, boolean allowUndergroundCabin){
        if (tag.contains(tagID)) {
            String resourceLocation = tag.getString(tagID);
            if (resourceLocation.contains("alexscaves:") && (!allowUndergroundCabin || !resourceLocation.contains("underground_cabin"))) {
                return true;
            }
        }
        return false;
    }
}

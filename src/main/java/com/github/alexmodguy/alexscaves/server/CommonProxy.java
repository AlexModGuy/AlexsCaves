package com.github.alexmodguy.alexscaves.server;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
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
        if(event.getEntity() instanceof MagneticEntityAccessor magnet && event.getEntity().getEntityData().isDirty()){
            Direction dir = magnet.getMagneticAttachmentFace();
            float defaultHeight = event.getEntity().getDimensions(Pose.STANDING).height;
            float defaultWidth = event.getEntity().getDimensions(Pose.STANDING).width;
            float defaultEyeHeight = event.getEntity().getEyeHeight(Pose.STANDING);
            if(dir == Direction.DOWN && event.getEntity() instanceof Player && event.getEntity().getPose() == Pose.STANDING){
                event.setNewEyeHeight(defaultEyeHeight);
            }else if(dir == Direction.UP){
                event.setNewEyeHeight(defaultHeight - defaultEyeHeight);
            }else if(dir.getAxis() != Direction.Axis.Y){
                event.setNewEyeHeight(0.0F);
            }
        }
    }

    @SubscribeEvent
    public void livingDie(LivingDeathEvent event) {
        if(event.getEntity().getType() == EntityType.MAGMA_CUBE && event.getSource() != null && event.getSource().getEntity() instanceof Frog frog){
            if(frog.getVariant() == ACFrogRegistry.PRIMORDIAL.get()){
                event.getEntity().spawnAtLocation(new ItemStack(ACBlockRegistry.AMBER.get()));
            }
        }
    }

    @SubscribeEvent
    public void livingHeal(LivingHealEvent event) {
        if(event.getEntity().hasEffect(ACEffectRegistry.IRRADIATED.get()) && !event.getEntity().getType().is(ACTagRegistry.RESISTS_RADIATION)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(LivingSpawnEvent.SpecialSpawn event) {
        try {
            if (event.getEntity() instanceof Creeper creeper) {
                creeper.targetSelector.addGoal(3, new AvoidEntityGoal<>(creeper, RaycatEntity.class, 10.0F, 1.0D, 1.2D));
            }
        } catch (Exception e) {
            AlexsCaves.LOGGER.warn("Tried to add unique behaviors to vanilla mobs and encountered an error");
        }
    }

    @SubscribeEvent
    public void onReplaceBiome(EventReplaceBiome event){
        ResourceKey<Biome> biome = BiomeGenerationConfig.getBiomeForEvent(event);
        if(biome != null){
            event.setResult(Event.Result.ALLOW);
            event.setBiomeToGenerate(event.getBiomeSource().getResourceKeyMap().get(biome));
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if(!event.player.isCreative()){
            if(event.player.getItemInHand(InteractionHand.MAIN_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)){
                checkAndDestroyExploitItem(event.player, EquipmentSlot.MAINHAND);
            }
            if(event.player.getItemInHand(InteractionHand.OFF_HAND).is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)){
                checkAndDestroyExploitItem(event.player, EquipmentSlot.OFFHAND);
            }
        }
    }

    private static void checkAndDestroyExploitItem(Player player, EquipmentSlot slot) {
        ItemStack itemInHand = player.getItemBySlot(slot);
        if(itemInHand.is(ACTagRegistry.RESTRICTED_BIOME_LOCATORS)){
            boolean flag = false;
            CompoundTag tag = itemInHand.getTag();
            if(tag != null && tag.contains("BiomeKey")){
                String biomeKey = tag.getString("BiomeKey");
                if(biomeKey.contains("alexscaves:")){
                    flag = true;
                }
            }
            if(flag){
                itemInHand.shrink(1);
                player.broadcastBreakEvent(slot);
                player.playSound(ACSoundRegistry.DISAPPOINTMENT.get());
                if(!player.level.isClientSide){
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

    public boolean checkIfParticleAt(SimpleParticleType simpleParticleType, BlockPos at){
        return false;
    }

    public float getPartialTicks() {
        return 1.0F;
    }
}

package com.github.alexmodguy.alexscaves.server;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexthe666.citadel.server.event.EventReplaceBiome;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            if(dir == Direction.DOWN && event.getEntity() instanceof Player && event.getEntity().getPose() == Pose.STANDING){
                event.setNewEyeHeight(event.getNewSize().height * 0.9F);
            }else if(dir == Direction.UP){
                float eye = Mth.clamp(0.2F, event.getNewSize().height * 0.1F, event.getNewSize().height * 0.9F);
                event.setNewEyeHeight(eye);
            }else if(dir.getAxis() != Direction.Axis.Y){
                float eye = (event.getOldEyeHeight() / event.getOldSize().height) * event.getOldSize().width;
                event.setNewEyeHeight(eye);
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
    public void onReplaceBiome(EventReplaceBiome event){
        ResourceKey<Biome> biome = ACBiomeRegistry.getBiomeForEvent(event);
        if(biome != null){
            event.setResult(Event.Result.ALLOW);
            event.setBiomeToGenerate(event.getBiomeSource().getResourceKeyMap().get(biome));
        }
    }

    public Player getClientSidePlayer() {
        return null;
    }
}

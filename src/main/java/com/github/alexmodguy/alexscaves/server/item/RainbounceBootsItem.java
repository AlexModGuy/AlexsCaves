package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;

public class RainbounceBootsItem extends ArmorItem implements CustomArmorPostRender {
    public RainbounceBootsItem(ACArmorMaterial rainbounceArmorMaterial) {
        super(rainbounceArmorMaterial, Type.BOOTS, new Properties());
    }

    @Override
    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return AlexsCaves.MODID + ":textures/armor/rainbounce_boots.png";
    }

    public static void onEntityLand(LivingEntity living, Vec3 vec3) {
        float f = (float) Math.abs(vec3.y);
        float f1 = f > 0.42 ? 1.05F : 0.7F;
        if (!living.isSuppressingBounce() && f > 0.2F && vec3.y < 0.0) {
            BlockState blockstate = living.level().getBlockState(living.getOnPosLegacy());
            double f2 = Math.abs(vec3.y) * f1;
            if(blockstate.is(ACTagRegistry.REDUCE_RAINBOUNCE_BOOTS_EFFECT_ON)){
                f2 *= 0.15F;
            }
            float xzInertia = living.hasEffect(ACEffectRegistry.SUGAR_RUSH.get()) ? 1.2F : 1.9F;
            living.setDeltaMovement(living.getDeltaMovement().multiply(xzInertia, 1F, xzInertia).add(0, f2, 0));
            living.fallDistance = 0.0F;
            living.playSound(ACSoundRegistry.RAINBOUNCE_BOOTS_BOUNCE.get());
            living.level().addParticle(ACParticleRegistry.PLAYER_RAINBOW.get(), living.xo, living.yo, living.zo, living.getId(), 0, 0);
            for(int i = 0; i < 3 + living.getRandom().nextInt(3); i++){
                living.level().addParticle(ParticleTypes.CLOUD, living.getRandomX(0.8F), living.getY() + 0.3F + living.getRandom().nextFloat() * 0.2F, living.getRandomZ(0.8F), 0, 0, 0);
            }
        }
    }
}

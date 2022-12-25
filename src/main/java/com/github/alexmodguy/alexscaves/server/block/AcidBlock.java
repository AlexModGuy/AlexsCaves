package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

public class AcidBlock extends LiquidBlock {
    public AcidBlock(RegistryObject<FlowingFluid> flowingFluid, BlockBehaviour.Properties properties) {
        super(flowingFluid, properties);
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        boolean top = level.getFluidState(pos.above()).isEmpty();
        if (randomSource.nextInt(top ? 10 : 40) == 0) {
            float height = top ? state.getFluidState().getHeight(level, pos) : randomSource.nextFloat();
            level.addParticle(ACParticleRegistry.ACID_BUBBLE.get(), pos.getX() + randomSource.nextFloat(), pos.getY() + height, pos.getZ() + randomSource.nextFloat(), (randomSource.nextFloat() - 0.5F) * 0.1F, 0.05F + randomSource.nextFloat() * 0.1F, (randomSource.nextFloat() - 0.5F) * 0.1F);
        }
    }

    public void entityInside(BlockState blockState, Level level, BlockPos pos, Entity entity) {
        if (!entity.getType().is(ACTagRegistry.RESISTS_ACID) && entity.getFluidTypeHeight(ACFluidRegistry.ACID_FLUID_TYPE.get()) > 0.1) {
            boolean armor = false;
            if (entity instanceof LivingEntity living && !(entity instanceof Player player && player.isCreative())) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if(slot.isArmor()){
                        ItemStack item = living.getItemBySlot(slot);
                        if (item != null && item.isDamageableItem()) {
                            armor = true;
                            if(living.getRandom().nextFloat() < 0.1F){
                                item.hurtAndBreak(1, living,  e -> e.broadcastBreakEvent(slot));
                            }
                        }
                    }
                }
            }
            entity.hurt(ACDamageTypes.ACID, (float) (armor ? 0.01D : 3.0D));
        }
    }
}

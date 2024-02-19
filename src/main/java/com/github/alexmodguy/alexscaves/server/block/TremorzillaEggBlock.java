package com.github.alexmodguy.alexscaves.server.block;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TremorzillaEggBlock extends DinosaurEggBlock {
    public TremorzillaEggBlock(Properties properties) {
        super(properties, ACEntityRegistry.TREMORZILLA, 10, 16);
    }

    @Override
    public boolean canHatchAt(BlockGetter reader, BlockPos pos){
        return false;
    }

    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
    }

    public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
        entityIn.causeFallDamage(fallDistance, 1.0F, entityIn.damageSources().fall());
    }

    @Override
    public void spawnDinosaurs(Level level, BlockPos pos, BlockState state) {
        super.spawnDinosaurs(level, pos, state);
        if(!level.isClientSide){
            for(Player player : level.getEntitiesOfClass(Player.class, new AABB(pos, pos.offset(1, 1, 1)).inflate(200))){
                ACAdvancementTriggerRegistry.HATCH_TREMORZILLA_EGG.triggerForEntity(player);
            }
        }
    }
}

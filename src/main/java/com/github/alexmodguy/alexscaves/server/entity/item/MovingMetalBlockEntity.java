package com.github.alexmodguy.alexscaves.server.entity.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;

public class MovingMetalBlockEntity extends AbstractMovingBlockEntity {

    public MovingMetalBlockEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MovingMetalBlockEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ACEntityRegistry.MOVING_METAL_BLOCK.get(), level);
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    public boolean movesEntities() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

}
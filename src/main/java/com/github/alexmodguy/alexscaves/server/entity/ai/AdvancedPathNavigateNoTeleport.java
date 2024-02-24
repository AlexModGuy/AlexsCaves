package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.PathingStuckHandler;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class AdvancedPathNavigateNoTeleport extends AdvancedPathNavigate {

    public AdvancedPathNavigateNoTeleport(Mob entity, Level world, MovementType type) {
        super(entity, world, type, entity.getBbWidth(), entity.getBbHeight(), PathingStuckHandler.createStuckHandler());
    }

    public AdvancedPathNavigateNoTeleport(Mob entity, Level world) {
        this(entity, world, MovementType.WALKING);
    }

    protected boolean canUpdatePath() {
        // ignore dismounting logic
        return true;
    }
}

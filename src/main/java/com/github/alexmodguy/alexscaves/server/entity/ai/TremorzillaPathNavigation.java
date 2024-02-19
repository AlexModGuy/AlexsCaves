package com.github.alexmodguy.alexscaves.server.entity.ai;

import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.AdvancedPathNavigate;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class TremorzillaPathNavigation extends AdvancedPathNavigate {

    public TremorzillaPathNavigation(Mob entity, Level world) {
        super(entity, world);
        //this.nodeEvaluator = new AllFluidsNodeEvaluator(true);
    }
}

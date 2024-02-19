package com.github.alexmodguy.alexscaves.server.entity.util;

import com.github.alexthe666.citadel.animation.LegSolver;

public class GrottoceratopsLegSolver extends LegSolver {
    public final LegSolver.Leg backLeft;
    public final LegSolver.Leg backRight;
    public final LegSolver.Leg frontLeft;
    public final LegSolver.Leg frontRight;

    public GrottoceratopsLegSolver() {
        this(0.0F, 0.65F, 0.5F, 0.5F, 1.0F);
    }

    public GrottoceratopsLegSolver(float forwardCenter, float forward, float sideBack, float sideFront, float range) {
        super(new LegSolver.Leg[]{new LegSolver.Leg(forwardCenter - forward, sideBack, range, false), new LegSolver.Leg(forwardCenter - forward, -sideBack, range, false), new LegSolver.Leg(forwardCenter + forward, sideFront, range, true), new LegSolver.Leg(forwardCenter + forward, -sideFront, range, true)});
        this.backLeft = this.legs[0];
        this.backRight = this.legs[1];
        this.frontLeft = this.legs[2];
        this.frontRight = this.legs[3];
    }


    protected float getFallSpeed() {
        return 0.1F;
    }

    protected float getRiseSpeed() {
        return 0.1F;
    }
}
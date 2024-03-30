package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.RectDrawer;

public class FluidDrawer implements FluidManager.Exporter {
    private Vector2[][] u;
    private float[][] p;
    private RectDrawer rectDrawer;

    public FluidDrawer(RectDrawer rectDrawer) {
        this.rectDrawer = rectDrawer;
    }
    @Override
    public void addState(Vector2[][] u, float[][] p) {
        this.u = u;
        this.p = p;
    }

    public void draw() {

    }
}

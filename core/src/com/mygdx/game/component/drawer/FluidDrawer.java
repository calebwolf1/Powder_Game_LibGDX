package com.mygdx.game.component.drawer;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.component.view.Fluid;
import com.mygdx.game.utils.RectDrawer;

public class FluidDrawer {
    private static final float MAX_PRESSURE = 1f;

    private RectDrawer drawer;

    public FluidDrawer(RectDrawer drawer) {
        this.drawer = drawer;
    }


    public void draw(Fluid fluid) {

    }

    private Color getPressureColor(float p) {
        if(p >= 0) {
            return new Color(0f, p / MAX_PRESSURE, 0f, 0f);
        }
        return null;
    }
}

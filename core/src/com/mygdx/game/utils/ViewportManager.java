package com.mygdx.game.utils;

import com.badlogic.gdx.utils.viewport.Viewport;

// not really a "manager" like the other manager classes are
public class ViewportManager {
    private static final float T_PROP = 0.75f; // proportion of game screen taken up by top layer

    private Viewport top, bottom;

    public ViewportManager(int xRes, int yRes) {
        top = new LayeredFitViewport(xRes, yRes, T_PROP, true, true);
        bottom = new LayeredFitViewport(xRes, yRes / T_PROP - yRes, 1 - T_PROP, false);
    }

    public Viewport getTop() {
        return top;
    }

    public Viewport getBottom() {
        return bottom;
    }
}

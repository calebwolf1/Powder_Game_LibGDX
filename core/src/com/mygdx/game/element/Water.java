package com.mygdx.game.element;

import com.badlogic.gdx.graphics.Color;

public class Water extends Liquid {
    private static final Color color = new Color(0x4040FF00);
    private static final float density = 0.4f;
    private static final float dispersionRate = 0.3f;

    public Water(int x, int y) {
        super(x, y);
    }

    public Color getColor() {
        return color;
    }

    public float getDensity() {
        return density;
    }

    public float getDispersionRate() {
        return dispersionRate;
    }
}

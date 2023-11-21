package com.mygdx.game.element;

import com.badlogic.gdx.graphics.Color;

public class Water extends Liquid {
    private static final Color color = new Color(0x4040FF00);
    private static final double density = 0.15;
    private static final double dispersionRate = 0.3;

    public Water(int x, int y) {
        super(x, y);
    }

    public Color getColor() {
        return color;
    }

    public double getDensity() {
        return density;
    }

    public double getDispersionRate() {
        return dispersionRate;
    }
}

package com.mygdx.game.element;

import com.badlogic.gdx.graphics.Color;

public class Stone extends Solid {
    private static final Color color = new Color(0x80808000);
    private static final float density = 0.5f;

    public Stone(int x, int y) {
        super(x, y);
    }

    /**
     * Gets the color of this Particle. Every concrete descendant of Particle has a static color,
     * but the value of a static field in a class is shared by every subclass, which is not what
     * we want. So, the solution is to make an abstract getColor method that returns the static
     * color of each concrete class. Not perfect because getColor() has to be an instance method
     * when it would make most sense to be static.
     *
     * @return The Color of this type of Particle
     */
    @Override
    public Color getColor() {
        return color;
    }

    protected float getDensity() {
        return density;
    }
}

package com.mygdx.game.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.RectDrawer;

public abstract class Element {
    private Vector2 pos;
    /**
     * Gets the color of this Particle. Every concrete descendant of Particle has a static color,
     * but the value of a static field in a class is shared by every subclass, which is not what
     * we want. So, the solution is to make an abstract getColor method that returns the static
     * color of each concrete class. Not perfect because getColor() has to be an instance method
     * when it would make most sense to be static.
     * @return The Color of this type of Particle
     */
    public abstract Color getColor();

    public Element(Vector2 pos) {
        this.pos = pos;
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 newPos) {
        this.pos = newPos;
    }

    /**
     * Draws this Element at the given position
     * @param pos The position of this Element in simulation coordinates
     */
    public void draw(RectDrawer shape) {
        shape.drawRect(Math.round(pos.x), Math.round(pos.y), 1, 1, getColor());
    }

}

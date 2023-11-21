package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;

public abstract class Liquid extends Particle {

    public Liquid(int x, int y) {
        super(x, y);
    }

    public abstract double getDispersionRate();

    /**
     * Calculates the farthest position this Particle can travel to if not obstructed. Based on
     * its current position, the velocity of the frame it is in, and what type of matter it is.
     * Defined in Solid, Liquid, and Gas, but can be overridden if a Particle has a different
     * movement pattern.
     *
     * @param velMap
     * @return the distance this Particle should travel without obstructions.
     */
    @Override
    public Vector2 getNewPos(ArrayMap<Vector2> velMap) {
        return null;
    }

    @Override
    public boolean move(ElementMap elementMap) {
        return  applyGravity(elementMap) &&
                applyDispersion(elementMap);
    }

//    public boolean applySwap(ElementMap elementMap) {
//        if()
//        return true;
//    }

    public boolean applyDispersion(ElementMap elementMap) {
        int dx = Math.random() < getDispersionRate() ? 1 : 0;
        if(dx != 0) {
            dx *= Math.random() < 0.5 ? 1 : -1;
            if(x == elementMap.getWidth() - 1 && dx == 1) {
                // going to move off the right side of map
                elementMap.remove(x, y);
                return false;
            }
            if(x == 0 && dx == -1) {
                // going to move off the left side of map
                elementMap.remove(x, y);
                return false;
            }
            if(!elementMap.moveIfEmpty(x, y, x + dx, y)) {
                elementMap.moveIfEmpty(x, y, x - dx, y);
            }
        }
        return true;
    }
}

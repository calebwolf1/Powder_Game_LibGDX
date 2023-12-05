package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;
import com.mygdx.game.Coords;

public abstract class Liquid extends Particle {

    private float dispersion;

    public Liquid(int x, int y) {
        super(x, y);
    }

    public abstract float getDispersionRate();

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
        return applyGravity(elementMap)
//                && applyVelocity(elementMap)
                && applyDispersion(elementMap);
    }

    public boolean applyDispersion(ElementMap elementMap) {
        // calculate dispersion
        boolean canGoLeft = elementMap.isEmpty(x - 1, y);
        boolean canGoRight = elementMap.isEmpty(x + 1, y);
        if((canGoLeft || canGoRight) && !elementMap.isEmpty(x, y + 1)) {
            float v = Coords.randFloat() / 2;
            if(canGoLeft && !canGoRight) {  // can only go left, negative vel
                dispersion -= v;
            } else if(!canGoLeft) {  // can only go right, positive vel
                dispersion += v;
            } else if(Coords.randBool(getDispersionRate())) {  // can go either direction, choose
                dispersion += Coords.coinToss() ? v : -v;
            }
        }

        // apply dispersion
        // TODO: 12/5/2023 refactor
        if(dispersion < 0 && elementMap.isEmpty(x - 1, y) && Coords.randBool(dispersion * -1)) {
            dispersion /= 1.5;
            return elementMap.moveLeft(x, y);
        } else if(dispersion > 0 && elementMap.isEmpty(x + 1, y) && Coords.randBool(dispersion * 1)) {
            dispersion /= 1.5;
            return elementMap.moveRight(x, y);
        }
        return true;
    }


}

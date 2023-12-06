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
    public boolean move(Neighborhood neighbors) {
        return applyGravity(neighbors)
//                && applyVelocity(elementMap)
                && applyDispersion(neighbors);
    }

    public boolean applyDispersion(Neighborhood neighbors) {
        // calculate dispersion
        boolean canGoLeft = neighbors.isEmpty(Neighborhood.Dir.LEFT);
        boolean canGoRight = neighbors.isEmpty(Neighborhood.Dir.RIGHT);
        if((canGoLeft || canGoRight) && !neighbors.isEmpty(Neighborhood.Dir.DOWN)) {
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
        if(dispersion < 0 && neighbors.isEmpty(Neighborhood.Dir.LEFT) && Coords.randBool(dispersion * -1)) {
            dispersion /= 1.5;
            return neighbors.move(Neighborhood.Dir.LEFT);
        } else if(dispersion > 0 && neighbors.isEmpty(Neighborhood.Dir.RIGHT) && Coords.randBool(dispersion * 1)) {
            dispersion /= 1.5;
            return neighbors.move(Neighborhood.Dir.RIGHT);
        }
        return true;
    }


}

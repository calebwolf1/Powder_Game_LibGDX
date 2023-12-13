package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;
import com.mygdx.game.element.Neighborhood.Dir;
import com.mygdx.game.utils.Random;


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
     * @param velMap the velocity map
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
        boolean canGoLeft = neighbors.isEmpty(Dir.LEFT);
        boolean canGoRight = neighbors.isEmpty(Dir.RIGHT);
        if((canGoLeft || canGoRight) && !neighbors.isEmpty(Dir.DOWN)) {
            float v = Random.randFloat() / 2;
            if(canGoLeft && !canGoRight) {  // can only go left, negative vel
                dispersion -= v;
            } else if(!canGoLeft) {  // can only go right, positive vel
                dispersion += v;
            } else if(Random.randBool(getDispersionRate())) {  // can go either direction, choose
                dispersion += Random.coinToss() ? v : -v;
            }
        }

        // apply dispersion
        if(shouldDisperse(neighbors, Dir.LEFT)) {
            return dispersionDirection(neighbors, Dir.LEFT);
        } else if(shouldDisperse(neighbors, Dir.RIGHT)) {
            return dispersionDirection(neighbors, Dir.RIGHT);
        }
        return true;
    }

    private boolean shouldDisperse(Neighborhood neighbors, Dir d) {
        return dispersion * d.dx > 0 && neighbors.isEmpty(d) && Random.randBool(dispersion * d.dx);
    }

    private boolean dispersionDirection(Neighborhood neighbors, Dir d) {
        dispersion /= 1.5;
        return neighbors.move(d);
    }

}

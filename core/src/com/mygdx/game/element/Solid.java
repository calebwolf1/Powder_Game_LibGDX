package com.mygdx.game.element;

import com.mygdx.game.element.Neighborhood.Dir;
import com.mygdx.game.utils.Random;


public abstract class Solid extends Particle {

    public Solid(int x, int y) {
        super(x, y);
    }

    // true if stayed in bounds, false if not and was removed
    public boolean move(Neighborhood neighbors) {
        return applyGravity(neighbors)
                && applySwap(neighbors);
    }

    public boolean applySwap(Neighborhood neighbors) {
        if(neighbors.isLiquid(Dir.DOWN)) {
            if(Random.randBool(getDensity() / 2)) {
                neighbors.swap(Dir.DOWN);
            }
        }
        return true;
    }
}

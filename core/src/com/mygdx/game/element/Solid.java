package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;
import com.mygdx.game.Coords;
import com.mygdx.game.ElementManager;

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
        if(neighbors.isLiquid(Neighborhood.Dir.DOWN)) {
            if(Coords.randBool(getDensity() / 2)) {
                neighbors.swap(Neighborhood.Dir.DOWN);
            }
        }
        return true;
    }

    public Vector2 getNewPos(ArrayMap<Vector2> velMap) {
        Vector2 oldPos = new Vector2(x, y);
        Vector2 res = oldPos
                .cpy()
                .add(getVel())
                .add(ElementManager.G);
        if(velMap.get(oldPos) != null) {
            res.add(velMap.get(oldPos));
        }
        return res;
    }
}

package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;
import com.mygdx.game.ElementManager;
import com.mygdx.game.ElementMap;

public abstract class Solid extends Particle {

    public Solid(int x, int y) {
        super(x, y);
    }

    // true if stayed in bounds, false if not and was removed
    public boolean move(ElementMap elementMap) {
        return applyGravity(elementMap);
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

package com.mygdx.game.element;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ArrayMap;
import com.mygdx.game.ElementManager;

public abstract class Solid extends Particle {

    public Solid(Vector2 pos) {
        super(pos);
    }

    public Vector2 getNewPos(ArrayMap<Vector2> velMap) {
        Vector2 oldPos = getPos();
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

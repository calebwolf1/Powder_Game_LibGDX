package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

// manages conversions between game and screen coordinates.
public class Projector {
    private Viewport viewport;

    public Projector(Viewport viewport) {
        this.viewport = viewport;
    }

    // converts screen coords to game coords
    public void unproject(Vector2 vec) {
        if(viewport == null) System.out.println("NULL VIEWPORT");
        viewport.unproject(vec);
    }
}

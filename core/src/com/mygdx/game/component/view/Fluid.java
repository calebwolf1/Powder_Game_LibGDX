package com.mygdx.game.component.view;

import com.badlogic.gdx.math.Vector2;

public interface Fluid {
    float pressure(int x, int y);
    Vector2 velocity(int x, int y);
}

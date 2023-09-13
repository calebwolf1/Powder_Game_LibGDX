package com.mygdx.game.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Block extends Element {
    private static Color color = Color.GRAY;

    public Block(Vector2 pos) {
        super(pos);
    }

    @Override
    public Color getColor() {
        return color;
    }
}

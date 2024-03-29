package com.mygdx.game.element;

import com.badlogic.gdx.graphics.Color;

public class Block extends Element {
    private static Color color = new Color(0x60606000);

    public Block(int x, int y) {
        super(x, y);
    }

    @Override
    public Color getColor() {
        return color;
    }
}

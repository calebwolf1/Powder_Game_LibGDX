package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class ArrayMap<E> {
    private E[][] con;
    public final int width;
    public final int height;

    @SuppressWarnings("unchecked")
    public ArrayMap(int width, int height) {
        con = (E[][]) new Object[height][width];
        this.width = width;
        this.height = height;
    }

    public E get(Vector2 pos) {
        return con[Math.round(pos.y)][Math.round(pos.x)];
    }

    public E get(int x, int y) {
        return con[y][x];
    }

    public void set(Vector2 pos, E val) {
        con[Math.round(pos.y)][Math.round(pos.x)] = val;
    }

    public void set(int posX, int posY, E val) {
        con[posY][posX] = val;
    }
}

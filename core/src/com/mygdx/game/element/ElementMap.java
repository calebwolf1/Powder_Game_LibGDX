package com.mygdx.game.element;

import com.mygdx.game.ArrayMap;
import com.mygdx.game.GameManager;

import java.util.function.BooleanSupplier;

import static com.mygdx.game.GameManager.boundsCheck;

public class ElementMap {
    private ArrayMap<Element> elements;

    public ElementMap(int xRes, int yRes) {
        elements = new ArrayMap<>(xRes, yRes);
    }

    // return false if elements[x][y] is empty x or y is out of bounds
    public boolean isEmpty(int x, int y) {
        return !boundsCheck(x, y) || elements.get(x, y) == null;
    }

    // move Element e to position (x, y) and returns true. returns false and removes element from
    // the map if (x, y) is out of bounds.
    private boolean move(Element e, int x, int y) {
        // pre: e != null, isEmpty(x, y) == true
        if(e == null || !isEmpty(x, y)) {
            throw new IllegalArgumentException("e is null or (x, y) is occupied");
        }
        elements.set(e.x, e.y, null);
        if(!boundsCheck(x, y)) {
            return false;
        }
        elements.set(x, y, e);
        e.x = x;
        e.y = y;
        return true;
    }

    public void swap(int x1, int y1, int x2, int y2) {
        Element e1 = elements.get(x1, y1);
        Element e2 = elements.get(x2, y2);
        move(e2, x1, y1);
        move(e1, x2, y2);
//        elements.set(x1, y1, elements.get(x2, y2));
//        elements.set(x2, y2, temp);
    }

    public void set(int x, int y, Element e) {
        elements.set(x, y, e);
    }

    public Element get(int x, int y) {
        return elements.get(x, y);
    }

    public int getWidth() {
        return elements.width;
    }

    public int getHeight() {
        return elements.height;
    }

    // moves the Element at (x1, y1) to the position (x2, y2) if (x2, y2) is empty. returns true
    // if the element was moved, false if not.
    public boolean moveIfEmpty(int x1, int y1, int x2, int y2) {
        if(isEmpty(x2, y2)) {
            move(elements.get(x1, y1), x2, y2);
            elements.set(x1, y1, null);
            return true;
        }
        return false;
    }

    // moves the element at the given position 1 space to the right
    public boolean moveRight(int x, int y) {
        return moveDir(x, y, 1, 0);
    }

    // moves the element at the given position 1 space to the left and returns true. throws
    // exception if that space is occupied. returns false and removes this particle from the map
    // if the particle moved off the map
    public boolean moveLeft(int x, int y) {
        return moveDir(x, y, -1, 0);
    }

    public boolean moveUp(int x, int y) {
        return moveDir(x, y, 0, -1);
    }

    public boolean moveDown(int x, int y) {
        return moveDir(x, y, 0, 1);
    }

    private boolean moveDir(int x, int y, int dx, int dy) {
        if(!boundsCheck(x, y)) {
            throw new IllegalArgumentException("x or y OOB");
        }
        return move(elements.get(x, y), x + dx, y + dy);
    }


    public boolean remove(int x, int y) {
        if(isEmpty(x, y)) {
            return false;
        }
        elements.set(x, y, null);
        return true;
    }

    public boolean isLiquid(int x, int y) {
        return boundsCheck(x, y) && elements.get(x, y) instanceof Liquid;
    }
}

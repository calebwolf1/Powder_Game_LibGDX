package com.mygdx.game.element;

import com.mygdx.game.ArrayMap;

import java.util.function.BooleanSupplier;

public class ElementMap {
    private ArrayMap<Element> elements;

    public ElementMap(int xRes, int yRes) {
        elements = new ArrayMap<>(xRes, yRes);
    }

    public boolean isEmpty(int x, int y) {
        return elements.get(x, y) == null;
    }

    // move Element e to position (x, y)
    private void move(Element e, int x, int y) {
        elements.set(x, y, e);
        if(e != null) {
            e.x = x;
            e.y = y;
        }
    }

    public void swap(int x1, int y1, int x2, int y2) {
        Element e1 = elements.get(x1, y1);
        move(elements.get(x2, y2), x1, y1);
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

    public boolean remove(int x, int y) {
        if(isEmpty(x, y)) {
            return false;
        }
        elements.set(x, y, null);
        return true;
    }

    public boolean isLiquid(int x, int y) {
        // TODO: 11/21/2023 bounds check x and y instead of making client do it
        return elements.get(x, y) instanceof Liquid;
    }
}

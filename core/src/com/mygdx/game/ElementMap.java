package com.mygdx.game;

import com.mygdx.game.element.Element;

public class ElementMap {
    private ArrayMap<Element> elements;

    public ElementMap(int xRes, int yRes) {
        elements = new ArrayMap<>(xRes, yRes);
    }

    public boolean isEmpty(int x, int y) {
        return elements.get(x, y) == null;
    }

    public void swap(int x1, int y1, int x2, int y2) {
        Element temp = elements.get(x1, y1);
        elements.set(x1, y1, elements.get(x2, y2));
        elements.set(x2, y2, temp);
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
            swap(x1, y1, x2, y2);
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
}

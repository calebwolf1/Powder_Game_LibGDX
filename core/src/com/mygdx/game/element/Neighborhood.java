package com.mygdx.game.element;

import com.mygdx.game.utils.ArrayMap;

import static com.mygdx.game.GameManager.boundsCheck;

// gives an interface for Elements to interact with neighboring elements
public class Neighborhood {
    private ArrayMap<Element> elements;
    private int x, y;  // current element

    public enum Dir {
        // beautiful
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        public final int dx;
        public final int dy;

        Dir(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public Neighborhood(ArrayMap<Element> elements, Element cur) {
        this.elements = elements;
        this.x = cur.x;
        this.y = cur.y;
    }

    // return true if given direction is unoccupied or out of bounds
    public boolean isEmpty(Dir d) {
        return !boundsCheck(x + d.dx, y + d.dy) || elements.get(x + d.dx, y + d.dy) == null;
    }

    // moves this element 1 space in the given direction and returns true. throws
    // exception if that space is occupied. returns false and removes this particle from the map
    // if the particle moved off the map
    public boolean move(Dir d) {
        if(!isEmpty(d)) {
            throw new IllegalArgumentException("Direction is occupied!");
        }
        Element e = elements.get(x, y);
        elements.set(x, y, null);
        if(!boundsCheck(x + d.dx, y + d.dy)) {
            return false;
        }
        elements.set(x + d.dx, y + d.dy, e);
        e.x += d.dx;
        e.y += d.dy;
        x += d.dx;
        y += d.dy;
        return true;
    }

    // returns whether the given direction has a Liquid
    public boolean isLiquid(Dir d) {
        return boundsCheck(x + d.dx, y + d.dy) && elements.get(x + d.dx, y + d.dy) instanceof Liquid;
    }

    // swaps this element with the neighbor in the given direction. Pre: direction is occupied
    public void swap(Dir d) {
        Element e1 = elements.get(x, y);
        Element e2 = elements.get(x + d.dx, y + d.dy);
        elements.set(x, y, e2);
        elements.set(x + d.dx, y + d.dy, e1);
        e1.x += d.dx;
        e1.y += d.dy;
        e2.x -= d.dx;
        e2.y -= d.dy;
        x += d.dx;
        y += d.dy;
    }
}

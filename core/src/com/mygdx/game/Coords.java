package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

// Collection of static methods to translate between the three coordinate systems used by this
// application: touch, screen, and simulation.
public class Coords {

    // get the x component of the given vector rounded to the nearest integer
    public static int intX(Vector2 v) {
        return Math.round(v.x);
    }

    // get the y component of the given vector rounded to the nearest integer
    public static int intY(Vector2 v) {
        return Math.round(v.y);
    }

    public static void line(int x0, int y0, int x1, int y1,
                                         int radius, BiIntConsumer lineFn) {
        if(x0 == x1 && y0 == y1)
            return;
        if(Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if(x0 < x1) {
                getLineLow(x0, y0, x1, y1, radius, lineFn);
            } else {
                getLineLow(x1, y1, x0, y0, radius, lineFn);
            }
        } else {
            if(y0 < y1) {
                getLineHigh(x0, y0, x1, y1, radius, lineFn);
            } else {
                getLineHigh(x1, y1, x0, y0, radius, lineFn);
            }
        }
    }

    private static void getLineLow(int x0, int y0, int x1, int y1, int radius,
                                             BiIntConsumer lineFn) {
        // dx > dy
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if(dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int D = (2 * dy) - dx;

        for(int x = x0, y = y0; x <= x1; x++) {
            circle(x, y, radius, true, lineFn);
            if(D > 0) {
                y += yi;
                D += (2 * (dy - dx));
            } else {
                D += 2 * dy;
            }
        }
    }

    private static void getLineHigh(int x0, int y0, int x1, int y1, int radius,
                                    BiIntConsumer lineFn) {
        // dy > dx
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if(dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int D = (2 * dx) - dy;

        for(int y = y0, x = x0; y <= y1; y++) {
            circle(x, y, radius, true, lineFn);
            if(D > 0) {
                x += xi;
                D += 2 * (dx - dy);
            } else {
                D += 2 * dx;
            }
        }
    }

    public static void circle(int x, int y, int radius, boolean filled,
                               BiIntConsumer circleFn) {
        if(radius == 0) {
            circleFn.accept(x, y);
            return;
        }
        for(int curY = -radius; curY <= radius; curY++) {
            for(int curX = -radius; curX <= radius; curX++) {
                boolean check = curX * curX + curY * curY < radius * radius + radius;
                if(filled && check) {
                    circleFn.accept(x + curX, y + curY);
                } else if(!filled && check && curX * curX + curY * curY > radius * radius - radius) {
                    circleFn.accept(x + curX, y + curY);
                }
            }
        }
    }
}

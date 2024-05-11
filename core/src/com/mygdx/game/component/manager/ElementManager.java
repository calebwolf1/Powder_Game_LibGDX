package com.mygdx.game.component.manager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.element.*;
import com.mygdx.game.utils.ArrayMap;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.Shape;
import com.mygdx.game.component.view.Elements;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import static com.mygdx.game.GameManager.boundsCheck;

public class ElementManager implements Elements {
    // constants
    private static final int MAX_PARTICLES = 20_000;
    private static final int BORDER_WIDTH = 4;
    public static final Vector2 G = new Vector2(0, 0.7f);

    // data structures
    private ArrayMap<Element> elementMap;  // map of on-screen Elements in each game position
    private ObjectSet<Element> elements; // set of Elements in the game

    private ElementFactory elementFactory;

    @Override
    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    @Override
    public Element elementAt(int x, int y) {
        return elementMap.get(x, y);
    }


    public ElementManager(int xRes, int yRes) {
        elementMap = new ArrayMap<>(xRes, yRes);
        elements = new ObjectSet<>(MAX_PARTICLES);
        elementFactory = new ElementFactory();
        makeBorder();
    }

    public void update(FluidManager fluid) {
        // move particles
        Iterator<Element> it = elements.iterator();
        while(it.hasNext()) {
            Element e = it.next();
            if(e instanceof Particle) {
                Particle p = (Particle) e;
                if(!p.move(new Neighborhood(elementMap, p))) {
                    it.remove();
                }
            }
        }
    }

    public void placeElement(int x, int y, String name) {
        if(elements.size < MAX_PARTICLES) {
            if(boundsCheck(x, y) && elementMap.isEmpty(x, y)) {
                Element e = elementFactory.createElement(name, x, y);
                elementMap.set(x, y, e);
                elements.add(e);
            }
        }
    }

    // remove the Element at the given position if it is within bounds and is not a Block
    public void clearElement(int x, int y) {
        if(boundsCheck(x, y)) {
            Element e = elementMap.get(x, y);
            if(e != null && !(e instanceof Block)) {
                elementMap.set(x, y, null);
                elements.remove(e);
            }
        }
    }

    public void eraseElement(int x, int y) {
        if(boundsCheck(x, y)) {
            Element e = elementMap.get(x, y);
            if(e instanceof Block) {
                elementMap.set(x, y, null);
                elements.remove(e);
            }
        }
    }

    public void reset() {
        for(int y = 0; y < elementMap.height; y++) {
            for(int x = 0; x < elementMap.width; x++) {
                elementMap.set(x, y, null);
            }
        }
        elements.clear();
        makeBorder();
    }

    private void makeBorder() {
        BiIntConsumer lineFn = (x, y) -> placeElement(x, y, "block");

        int i = 0;
        while(i < BORDER_WIDTH) {
            int yLim = elementMap.height - 1 - i;
            int xLim = elementMap.width - 1 - i;
            Shape.line(i, i, i, yLim, 0, lineFn);  // left
            Shape.line(xLim, i, xLim, yLim, 0, lineFn);  // right
            Shape.line(i, i, xLim, i, 0, lineFn);  // top
            Shape.line(i, yLim, xLim, yLim, 0, lineFn);  // bottom
            i++;
        }
    }
}

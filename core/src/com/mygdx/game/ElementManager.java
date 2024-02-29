package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.element.*;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.Shape;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import static com.mygdx.game.GameManager.boundsCheck;

public class ElementManager {
    // constants
    private static final int MAX_PARTICLES = 20_000;
    private static final int BORDER_WIDTH = 4;
    public static final Vector2 G = new Vector2(0, 0.7f);
    private static final Array<Class<? extends Element>> P_TYPES;

    // data structures
    private ArrayMap<Element> elementMap;  // map of on-screen Elements in each game position
    private ObjectSet<Element> elements; // set of Elements in the game

    static {
        // initialize P_TYPES with every concrete descendant of Element
        Reflections ref = new Reflections("com.mygdx.game.element");
        Set<Class<? extends Element>> set = ref.getSubTypesOf(Element.class);
        set.removeIf(c -> Modifier.isAbstract(c.getModifiers()));
        P_TYPES = new Array<>();
        for(Class<? extends Element> c : set) {
            P_TYPES.add(c);
        }
    }

    public ElementManager(int xRes, int yRes) {
        elementMap = new ArrayMap<>(xRes, yRes);
        elements = new ObjectSet<>(MAX_PARTICLES);
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

    public void placeElement(int x, int y, Class<? extends Element> c) {
        if(elements.size < MAX_PARTICLES) {
            if (boundsCheck(x, y) && elementMap.isEmpty(x, y)) {
                Element e = null;
                try {
                    e = c.getConstructor(int.class, int.class).newInstance(x, y);
                } catch (InstantiationException | NoSuchMethodException |
                        IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }
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

    public void forEachElement(Consumer<Element> elemFn) {
        elements.forEach(elemFn);
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
        BiIntConsumer lineFn = (x, y) -> placeElement(x, y, Block.class);

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

    public void forEachPType(Consumer<Class<? extends Element>> pTypeFn) {
        P_TYPES.forEach(pTypeFn);
    }
}

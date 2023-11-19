package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.element.Block;
import com.mygdx.game.element.Element;
import com.mygdx.game.element.Particle;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import static com.mygdx.game.GameManager.boundsCheck;

public class ElementManager {
    // constants
    public static final int X_RES = 300, Y_RES = 200;  // dimensions of game area
    private static final int MAX_PARTICLES = 20_000;
    private static final int BORDER_WIDTH = 2;
    public static final Vector2 G = new Vector2(0, 0.7f);
    private static final Array<Class<? extends Element>> P_TYPES;

    // data structures
    private ArrayMap<Element> elementMap;  // map of on-screen Elements in each game position
    private ObjectSet<Element> elements; // set of Elements in the game

    static {
        // initialize P_TYPES with every concrete descendant of Element
        Reflections ref = new Reflections("com.mygdx.game");
        Set<Class<? extends Element>> set = ref.getSubTypesOf(Element.class);
        set.removeIf(c -> Modifier.isAbstract(c.getModifiers()));
        P_TYPES = new Array<>();
        for(Class<? extends Element> c : set) {
            P_TYPES.add(c);
        }
    }

    public ElementManager() {
        elementMap = new ArrayMap<>(X_RES, Y_RES);
        elements = new ObjectSet<>(MAX_PARTICLES);
        makeBorder();
    }

    // private pos getObstruction(start, end)

    public void update(ArrayMap<Vector2> velocityMap) {
        // move particles
        Iterator<Element> it = elements.iterator();
        while(it.hasNext()) {
            Element e = it.next();
            if(e instanceof Particle) {
                Particle p = (Particle) e;
                if(!p.move(velocityMap, elementMap)) {
                    it.remove();
                }
            }
        }
    }

    public void placeElement(int x, int y, Class<? extends Element> c) {
        if(elements.size < MAX_PARTICLES) {
            if (boundsCheck(x, y) && elementMap.get(x, y) == null) {
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
        // relies on each element storing its position, will probably have to change
//        for(Element e : elements) {
//            elementMap.set(e.getPos(), null);
//        }
        for(int y = 0; y < Y_RES; y++) {
            for(int x = 0; x < X_RES; x++) {
                elementMap.set(x, y, null);
            }
        }
        elements.clear();
        makeBorder();
    }

    private void makeBorder() {
        BiIntConsumer lineFn = (x, y) -> {
            Block block = new Block(x, y);
            elementMap.set(x, y, block);
            elements.add(block);
        };
        int i = 0;
        while(i < BORDER_WIDTH) {
            int yLim = Y_RES - 1 - i;
            int xLim = X_RES - 1 - i;
            Coords.line(i, i, i, yLim, 0, lineFn);  // left
            Coords.line(xLim, i, xLim, yLim, 0, lineFn);  // right
            Coords.line(i, i, xLim, i, 0, lineFn);  // top
            Coords.line(i, yLim, xLim, yLim, 0, lineFn);  // bottom
            i++;
        }
    }

    public void forEachPType(Consumer<Class<? extends Element>> pTypeFn) {
        P_TYPES.forEach(pTypeFn);
    }
}

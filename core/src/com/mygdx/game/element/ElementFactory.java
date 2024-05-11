package com.mygdx.game.element;

public class ElementFactory {
    public static final String[] ELEMENT_NAMES = {
            "powder",
            "water",
            "stone",
            "block"
    };

    public Element createElement(String name, int x, int y) {
        switch(name) {
            case "powder":
                return new Powder(x, y);
            case "water":
                return new Water(x, y);
            case "stone":
                return new Stone(x, y);
            case "block":
                return new Block(x, y);
            default:
                throw new IllegalArgumentException("Invalid element type: " + name);
        }
    }
}

package com.mygdx.game;

import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.game.element.Element;
import com.mygdx.game.utils.RectDrawer;

public class ElementDrawer implements ElementManager.Exporter {
    private ObjectSet<Element> elements;
    private RectDrawer rectDrawer;

    public ElementDrawer(RectDrawer rectDrawer) {
        this.rectDrawer = rectDrawer;
    }

    @Override
    public void addState(ObjectSet<Element> elements) {
        this.elements = elements;
    }

    public void draw() {
        elements.forEach(e -> e.draw(rectDrawer));
    }
}

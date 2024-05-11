package com.mygdx.game.component.drawer;

import com.mygdx.game.utils.RectDrawer;
import com.mygdx.game.component.view.Elements;

public class ElementDrawer {
    private RectDrawer drawer;

    public ElementDrawer(RectDrawer drawer) {
        this.drawer = drawer;
    }

    public void draw(Elements elems) {
        elems.forEach(e -> e.draw(drawer));
    }
}

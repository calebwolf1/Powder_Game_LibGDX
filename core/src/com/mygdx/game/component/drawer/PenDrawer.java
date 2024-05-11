package com.mygdx.game.component.drawer;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.component.manager.PenManager;
import com.mygdx.game.component.view.Pen;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.RectDrawer;
import com.mygdx.game.utils.Shape;

public class PenDrawer {
    private RectDrawer drawer;

    public PenDrawer(RectDrawer drawer) {
        this.drawer = drawer;
    }

    public void draw(Pen pen) {
        BiIntConsumer drawRedPoint = (x, y) -> drawer.drawRect(x, y, 1, 1, Color.RED);
        if(pen.lineType() == PenManager.LineType.LINE && pen.placing()) {
            Shape.line(pen.lineStart(), pen.mouse(), 0, drawRedPoint);
        }
        Shape.circle(pen.mouse(), pen.penSize(), false, drawRedPoint);
    }
}

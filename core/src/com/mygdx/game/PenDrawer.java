package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.RectDrawer;
import com.mygdx.game.utils.Shape;

public class PenDrawer implements PenManager.Exporter {
    private PenManager.PenType penType;
    private boolean placing;
    private int lineStartX, lineStartY, mouseX, mouseY, penSize;
    private RectDrawer rectDrawer;

    public PenDrawer(RectDrawer rectDrawer) {
        this.rectDrawer = rectDrawer;
    }

    @Override
    public void addState(PenManager.PenType penType, boolean placing, int lineStartX,
                         int lineStartY, int mouseX, int mouseY, int penSize) {
        this.penType = penType;
        this.placing = placing;
        this.lineStartX = lineStartX;
        this.lineStartY = lineStartY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.penSize = penSize;
    }

    public void draw() {
        BiIntConsumer drawRedRect = (x, y) -> rectDrawer.drawRect(x, y, 1, 1, Color.RED);
        if(penType == PenManager.PenType.LINE && placing) {
            Shape.line(lineStartX, lineStartY, mouseX, mouseY, 0, drawRedRect);
        }
        Shape.circle(mouseX, mouseY, penSize, false, drawRedRect);
    }
}

package com.mygdx.game.component.view;

import com.mygdx.game.component.manager.PenManager;
import com.mygdx.game.utils.Position;

public interface Pen {
    PenManager.LineType lineType();
    boolean placing();
    Position lineStart();
    Position mouse();
    int penSize();
}

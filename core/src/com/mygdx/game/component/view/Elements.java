package com.mygdx.game.component.view;

import com.mygdx.game.element.Element;

public interface Elements extends Iterable<Element> {
    Element elementAt(int x, int y);
}

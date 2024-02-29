package com.mygdx.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.element.Element;
import com.mygdx.game.element.Powder;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.Shape;

import java.util.Locale;

public class PenManager implements InputProcessor {
    private GameManager game;

    private boolean placing;
    private int mouseX, mouseY;  // game coordinates
    private int mousePrevX, mousePrevY;  // mouse coords from previous interrupt
    private int lineStartX, lineStartY;
    private Class<? extends Element> activeElement = Powder.class;
    private int penSize = 2;
    private PenType penType = PenType.FREE;
    private BiIntConsumer penAction;

    public enum PenType {
        FREE, LINE;

        private static final PenType[] vals = values();

        public PenType next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    // not ideal to store GM
    PenManager(GameManager game) {
        this.game = game;
    }

    PenManager(GameManager game, BiIntConsumer penAction) {
        this.game = game;
        this.penAction = penAction;
    }

    // perform pen action
    public void act() {
        if(penType == PenType.FREE && placing) {
            Shape.line(mousePrevX, mousePrevY, mouseX, mouseY, penSize, penAction);
        }
    }

    // draw pen outline and line
    public void draw(BiIntConsumer drawFn) {
        if(penType == PenType.LINE && placing) {
            Shape.line(lineStartX, lineStartY, mouseX, mouseY, 0, drawFn);
        }
        Shape.circle(mouseX, mouseY, penSize, false, drawFn);
    }

    public Class<? extends Element> getActiveElement() {
        return activeElement;
    }

    public void setActiveElement(Class<? extends Element> c) {
        activeElement = c;
    }

    public void setPenAction(BiIntConsumer penAction) {
        this.penAction = penAction;
    }

    public void penSizeUp() {
        penSize = penSize == 9 ? 0 : penSize + 1;
    }

    public void penSizeDown() {
        penSize = penSize == 0 ? 9 : penSize - 1;
    }

    public void penTypeNext() {
        penType = penType.next();
    }

    public String getPenType() {
        return penType.toString().toLowerCase(Locale.ROOT);
    }

    public int getPenSize() {
        return penSize;
    }

    /**
     * Called when a key was pressed
     *
     * @param keycode one of the constants in
     * @return whether the input was processed
     */
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Called when a key was released
     *
     * @param keycode one of the constants in
     * @return whether the input was processed
     */
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    /**
     * Called when a key was typed
     *
     * @param character The character
     * @return whether the input was processed
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Called when the screen was touched or a mouse button was pressed.
     *
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @param button  the button
     * @return whether the input was processed
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        placing = true;
        Vector2 vec = new Vector2((float) screenX, (float) screenY);
        game.getGameViewport().unproject(vec);
        lineStartX = Math.round(vec.x);
        lineStartY = Math.round(vec.y);
        return setMouse(screenX, screenY);
    }

    /**
     * Called when a finger was lifted or a mouse button was released.
     *
     * @param screenX the screen X
     * @param screenY the screen Y
     * @param pointer the pointer for the event.
     * @param button  the button
     * @return whether the input was processed
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        placing = false;
        if(penType == PenType.LINE) {
            Shape.line(lineStartX, lineStartY, mouseX, mouseY, penSize, penAction);
        }
        return true;
    }

    /**
     * Called when a finger or the mouse was dragged.
     *
     * @param screenX the screen X
     * @param screenY the screen Y
     * @param pointer the pointer for the event.
     * @return whether the input was processed
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return setMouse(screenX, screenY);
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. Will not be called on iOS.
     *
     * @param screenX the screen X
     * @param screenY the screen Y
     * @return whether the input was processed
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return setMouse(screenX, screenY);
    }

    /**
     * Called when the mouse wheel was scrolled. Will not be called on iOS.
     *
     * @param amountX the horizontal scroll amount, negative or positive depending on the
     *                direction the wheel was scrolled.
     * @param amountY the vertical scroll amount, negative or positive depending on the direction
     *               the wheel was scrolled.
     * @return whether the input was processed.
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    // sets the game's mouse to the unprojected and y-flipped given screen x and y coordinates
    private boolean setMouse(int screenX, int screenY) {
        mousePrevX = mouseX;
        mousePrevY = mouseY;
        Vector2 vec = new Vector2((float) screenX, (float) screenY);
        game.getGameViewport().unproject(vec);
        mouseX = Math.round(vec.x);
        mouseY = Math.round(vec.y);
        return true;
    }
}

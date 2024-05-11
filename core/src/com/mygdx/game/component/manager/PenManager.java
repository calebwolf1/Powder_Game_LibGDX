package com.mygdx.game.component.manager;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.component.view.Pen;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.Position;
import com.mygdx.game.utils.Projector;
import com.mygdx.game.utils.Shape;

import java.util.Locale;

public class PenManager implements InputProcessor, Pen {
    private Projector projector;
    private boolean placing;
    private int mouseX, mouseY;  // game coordinates
    private int mousePrevX, mousePrevY;  // mouse coords from previous interrupt
    private int lineStartX, lineStartY;
    private String activeElement = "powder";
    private int penSize = 2;
    private LineType lineType = LineType.FREE;
    private BiIntConsumer penAction;

    public enum LineType {
        FREE, LINE;

        private static final LineType[] vals = values();

        public LineType next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    public PenManager(Projector projector, BiIntConsumer penAction) {
        this.projector = projector;
        this.penAction = penAction;
    }

    // perform pen action
    public void act() {
        if(lineType == LineType.FREE && placing) {
            Shape.line(mousePrevX, mousePrevY, mouseX, mouseY, penSize, penAction);
        }
    }

    public String getActiveElement() {
        return activeElement;
    }

    public void setActiveElement(String c) {
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
        lineType = lineType.next();
    }

    public String getPenType() {
        return lineType.toString().toLowerCase(Locale.ROOT);
    }

    public int getPenSize() {
        return penSize;
    }


    public PenManager.LineType lineType() {
        return lineType;
    }
    public boolean placing() {
        return placing;
    }
    public Position lineStart() {
        return new Position(lineStartX, lineStartY);
    }
    public Position mouse() {
        return new Position(mouseX, mouseY);
    }
    public int penSize() {
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
//        game.getGameViewport().unproject(vec);
        projector.unproject(vec);
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
        if(lineType == LineType.LINE) {
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
        projector.unproject(vec);
        mouseX = Math.round(vec.x);
        mouseY = Math.round(vec.y);
        return true;
    }
}

package com.mygdx.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

// Handles any changes to the provided game's state due to input events
public class EventHandler implements InputProcessor {
    final GameManager game;

    EventHandler(GameManager game) {
        this.game = game;
    }

    /**
     * Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Input.Keys}
     * @return whether the input was processed
     */
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Called when a key was released
     *
     * @param keycode one of the constants in {@link Input.Keys}
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
     * Called when the screen was touched or a mouse button was pressed. The button parameter
     * will be {@link Buttons#LEFT} on iOS.
     *
     * @param screenX The x coordinate, origin is in the upper left corner
     * @param screenY The y coordinate, origin is in the upper left corner
     * @param pointer the pointer for the event.
     * @param button  the button
     * @return whether the input was processed
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        game.setPlacing(true);
        Vector2 vec = new Vector2((float) screenX, (float) screenY);
        game.getGameViewport().unproject(vec);
        game.setLineStart(Math.round(vec.x), Math.round(vec.y));
        return setMouse(screenX, screenY);
    }

    /**
     * Called when a finger was lifted or a mouse button was released. The button parameter will
     * be {@link Buttons#LEFT} on iOS.
     *
     * @param screenX
     * @param screenY
     * @param pointer the pointer for the event.
     * @param button  the button
     * @return whether the input was processed
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        game.setPlacing(false);
        if(game.getPenType() == GameManager.PenType.LINE) {
            Coords.line(game.getLineStartX(), game.getLineStartY(), game.getMouseX(),
                    game.getMouseY(), game.getPenSize(), game.getPenAction());
        }
        return true;
    }

    /**
     * Called when a finger or the mouse was dragged.
     *
     * @param screenX
     * @param screenY
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
     * @param screenX
     * @param screenY
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
        game.setMousePrev(game.getMouseX(), game.getMouseY());
        Vector2 vec = new Vector2((float) screenX, (float) screenY);
        game.getGameViewport().unproject(vec);
        game.setMouse(Math.round(vec.x), Math.round(vec.y));
        return true;
    }
}

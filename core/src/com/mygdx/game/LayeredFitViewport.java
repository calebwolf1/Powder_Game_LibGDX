package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LayeredFitViewport extends FitViewport {
    private final float layerProp;  // proportion of the game world taken up by this layer
    private final boolean top;  // whether this is the top (true) or bottom (false) layer


    public LayeredFitViewport(float worldWidth, float worldHeight, float layerProp, boolean top) {
        this(worldWidth, worldHeight, layerProp, top, new OrthographicCamera(), false);
    }

    public LayeredFitViewport(float worldWidth, float worldHeight, float layerProp, boolean top,
                              Camera camera, boolean yDown) {
        super(worldWidth, worldHeight, camera);
        this.layerProp = layerProp;
        this.top = top;
        if(yDown) {
            camera.up.set(0, -1, 0);
            camera.direction.set(0, 0, 1);
        }
    }

    public LayeredFitViewport(float worldWidth, float worldHeight, float layerProp, boolean top,
                              boolean yDown) {
        this(worldWidth, worldHeight, layerProp, top, new OrthographicCamera(), yDown);
    }

    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        Vector2 scaled = getScaling().apply(getWorldWidth(),
                getWorldHeight() / layerProp, screenWidth, screenHeight);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);
        int amountMissing = viewportHeight - Math.round(scaled.y * layerProp);

        // the combined top and bottom layers are centered, but this viewport alone should be
        // slightly offset
        int screenY = (screenHeight - viewportHeight) / 2;
        viewportHeight -= amountMissing;
        if (top) {
            screenY += amountMissing;
        }
        // Center.
        setScreenBounds((screenWidth - viewportWidth) / 2, screenY, viewportWidth, viewportHeight);
        apply(centerCamera);
    }
}
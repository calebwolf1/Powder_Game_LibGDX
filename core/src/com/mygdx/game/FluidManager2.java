package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Manages the state of the fluid simulation. Update the simulation to the next timestep with step.
 * Access the state using getVelocity() and getPressure().
 */
public class FluidManager2 {
    private final int X_RES, Y_RES;
    private Vector2[][] velocity;
    private Vector2[][] velPrev;
    private float[][] pressure;
    private ArrayMap<Vector2> velocityMap;

    public FluidManager2(int xRes, int yRes) {
        X_RES = xRes;
        Y_RES = yRes;
        velocity = new Vector2[Y_RES + 2][X_RES + 2];
        velPrev = new Vector2[Y_RES + 2][X_RES + 2];
        pressure = new float[Y_RES + 2][X_RES + 2];
        for(int i = 0; i < Y_RES + 2; i++) {
            for (int j = 0; j < X_RES + 2; j++) {
                velocity[i][j] = new Vector2(0f, 0f);
                velPrev[i][j] = new Vector2(0f, 0f);
            }
        }
        velocityMap = new ArrayMap<>(X_RES, Y_RES);
        System.out.println(velocity[0][0]);
        System.out.println(yRes);

    }

    public Vector2 getVelocity(int posX, int posY) {
        return velocity[posY][posX];
    }

    public float getPressure(int posX, int posY) {
        return pressure[posY][posX];
    }

    // post: velocity and pressure arrays hold velocity and pressure for the next timestep
    public void step(float dt) {
//        swap();
//        advect(dt);
    }

    public ArrayMap<Vector2> getVelocityMap() {
        return velocityMap;
    }

    private void advect(float dt) {
        for(int y = 1; y < Y_RES; y++) {
            for (int x = 1; x < X_RES; x++) {
                Vector2 coords = new Vector2(x - dt * velPrev[y][x].x, y - dt * velPrev[y][x].y);
                // since this is the first operation, we need to set instead of add to clear u
                velocity[y][x].set(bilerp(coords, velocity));
            }
        }
    }

    // kinda f'd up solution, TODO: fix
    private interface VectorFn {
        float apply(Vector2 v);
    }

    private float bilerp(Vector2 coords, Vector2[][] field, VectorFn fn) {
        int x0 = (int) coords.x;
        int y0 = (int) coords.y;
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        float wx = coords.x - x0;
        float a = (1 - wx) * fn.apply(field[y0][x0]) + wx * fn.apply(field[y0][x1]);
        float b = (1 - wx) * fn.apply(field[y1][x0]) + wx * fn.apply(field[y1][x1]);
        float wy = coords.y - y0;
        return (1 - wy) * a + wy * b;
    }

    private Vector2 bilerp(Vector2 coords, Vector2[][] field) {
        return new Vector2(bilerp(coords, field, v -> v.x), bilerp(coords, field, v -> v.y));
    }

    public void applyPressureFn(ScalarFn f) {
        for(int y = 1; y < Y_RES; y++) {
            for (int x = 1; x < X_RES; x++) {
                f.apply(x, y, pressure[y][x]);
            }
        }
    }

    public void applyVelocityFn(VectorFn2 f) {
        for(int y = 1; y < Y_RES; y++) {
            for(int x = 1; x < X_RES; x++) {
                f.apply(x, y, velocity[y][x].x, velocity[y][x].y);
            }
        }
    }

    public interface ScalarFn {
        void apply(int xPos, int yPos, float s);
    }

    public interface VectorFn2 {
        void apply(int xPos, int yPos, float vx, float vy);
    }

}

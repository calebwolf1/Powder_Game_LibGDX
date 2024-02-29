package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.BiIntConsumer;
import com.mygdx.game.utils.Shape;

public class FluidManager {
    private final int ELEMS_PER_FLUID = 3; // number of Element cells that fit across a single fluid cell
    private final int KD = 20;
    private final int KP = 40;
    private final int dx = 1;
    private final float visc = 0.3f;
    private final float halfrdx = 0.5f / dx;
    private Vector2[][] u;
    private Vector2[][] uTmp;
    private float[][] p;
    private float[][] pTmp;
    private float[][] div;

    public FluidManager(int xRes, int yRes) {
        u = new Vector2[xRes / ELEMS_PER_FLUID + 2][yRes / ELEMS_PER_FLUID + 2];
        uTmp = new Vector2[xRes / ELEMS_PER_FLUID + 2][yRes / ELEMS_PER_FLUID + 2];
        for(int y = 0; y < u.length; y++) {
            for (int x = 0; x < u[0].length; x++) {
                u[y][x] = new Vector2(0, 0);
                uTmp[y][x] = new Vector2(0, 0);
            }
        }
        p = new float[xRes / ELEMS_PER_FLUID + 2][yRes / ELEMS_PER_FLUID + 2];
        pTmp = new float[xRes / ELEMS_PER_FLUID + 2][yRes / ELEMS_PER_FLUID + 2];
        div = new float[xRes / ELEMS_PER_FLUID + 2][yRes / ELEMS_PER_FLUID + 2];
        // testing
        u[40][40].x = 10f;
        u[40][40].y = 5f;
    }

    public void step(float dt) {
        advect(dt);
        diffuse(dt);
        solveForP();
        gradientSubtract();
        // TODO: 12/27/2023 determine if setBounds should happen before or after rest of step
        setBounds();
    }

    private void advect(float dt) {
        for(int y = 1; y < u.length - 1; y++) {
            for(int x = 1; x < u[0].length - 1; x++) {
                float valX = x - u[y][x].x * dt;
                float valY = y - u[y][x].y * dt;
                uTmp[y][x] = bilerp(u, valX, valY);
            }
        }
        swap(u, uTmp);
    }

    private <T> void swap(T a, T b) {
        T tmp = a;
        a = b;
        b = tmp;
    }

    private Vector2 bilerp(Vector2[][] f, float x, float y) {
        // TODO: 12/27/2023 determine if should clamp to 0 or 1
        if(x < 0) {
            x = 0;
        }
        if(x > u[0].length) {
            x = u[0].length;
        }
        if(y < 0) {
            y = 0;
        }
        if(y > u.length) {
            y = u.length;
        }
        Vector2 a = f[MathUtils.floor(y)][MathUtils.floor(x)];
        Vector2 b = f[MathUtils.floor(y)][MathUtils.ceil(x)];
        Vector2 c = f[MathUtils.ceil(y)][MathUtils.floor(x)];
        Vector2 d = f[MathUtils.ceil(y)][MathUtils.ceil(x)];
        Vector2 abx = lerp(a, b, x);
        Vector2 dcx = lerp(d, c, x);
        return lerp(abx, dcx, y);
    }

    private Vector2 lerp(Vector2 a, Vector2 b, float t) {
        // TODO: 12/26/2023 optimize, don't make new Vector2
        return new Vector2((1-t) * a.x + b.x * t,
                        (1 - t) * a.y + b.y * t);
    }

    private void diffuse(float dt) {
        float alpha = (dx * dx) / (visc * dt);
        for(int i = 0; i < KD; i++) {
            for(int y = 1; y < u.length - 1; y++) {
                for (int x = 1; x < u[0].length - 1; x++) {
                    uTmp[y][x] = jacobi(u, u, alpha, 4 + alpha, x, y);
                }
            }
            swap(u, uTmp);
        }
    }

    private Vector2 jacobi(Vector2[][] x, Vector2[][] b, float alpha, float beta, int px, int py) {
        // TODO: 12/26/2023 avoid Vector2 creation
        float resX = (x[py - 1][px].x + x[py + 1][px].x + x[py][px - 1].x + x[py][px + 1].x + alpha * b[py][px].x) / beta;
        float resY = (x[py - 1][px].y + x[py + 1][px].y + x[py][px - 1].y + x[py][px + 1].y + alpha * b[py][px].y) / beta;
        return new Vector2(resX, resY);
    }

    private float jacobi(float[][] x, float[][] b, float alpha, float beta, int px, int py) {
        return (x[py - 1][px] + x[py + 1][px] + x[py][px - 1] + x[py][px + 1] + alpha * b[py][px]) / beta;
    }

    private void solveForP() {
        divergence();
        clear(p);  // determine if border should be cleared
        for(int i = 0; i < KP; i++) {
            for(int y = 1; y < u.length - 1; y++) {
                for (int x = 1; x < u[0].length - 1; x++) {
                    pTmp[y][x] = jacobi(p, div, -(dx * dx), 4, x, y);
                }
            }
            swap(p, pTmp);
        }
    }

    private void divergence() {
        for(int y = 1; y < u.length - 1; y++) {
            for (int x = 1; x < u[0].length - 1; x++) {
                Vector2 uL = u[y][x - 1];
                Vector2 uR = u[y][x + 1];
                Vector2 uT = u[y - 1][x];
                Vector2 uB = u[y + 1][x];
                div[y][x] = halfrdx * ((uR.x - uL.x) + (uT.y - uB.y));
            }
        }
    }

    private void clear(float[][] p) {
        for(int y = 0; y < p.length; y++) {
            for(int x = 0; x < p[0].length; x++) {
                p[y][x] = 0;
            }
        }
    }

    private void gradientSubtract() {
        for(int y = 1; y < u.length - 1; y++) {
            for (int x = 1; x < u[0].length - 1; x++) {
                float pL = p[y][x - 1];
                float pR = p[y][x + 1];
                float pT = p[y - 1][x];
                float pB = p[y + 1][x];
                uTmp[y][x].x = u[y][x].x - halfrdx * (pR - pL);
                uTmp[y][x].y = u[y][x].y - halfrdx * (pT - pB);
            }
        }
        swap(u, uTmp);
    }

    private void setBounds() {
        // top and bottom
        for(int i = 1; i < u[0].length - 1; i++) {
            u[0][i].x = -u[1][i].x;
            u[0][i].y = -u[1][i].y;
            u[u.length - 1][i].x = -u[u.length - 2][i].x;
            u[u.length - 1][i].y = -u[u.length - 2][i].y;

            p[0][i] = p[1][i];
            p[u.length - 1][i] = p[u.length - 2][i];
        }

        // left and right
        for(int i = 1; i < u.length - 1; i++) {
            u[i][0].x = -u[i][1].x;
            u[i][0].y = -u[i][1].y;
            u[i][u[0].length - 1].x = -u[i][u[0].length - 2].x;
            u[i][u[0].length - 1].y = -u[i][u[0].length - 2].y;

            p[i][0] = p[i][1];
            p[i][u[0].length - 1] = -p[i][u[0].length - 2];
        }
    }

    public Vector2 getVelocity(int x, int y) {
        int fx = x / ELEMS_PER_FLUID + 1;
        int fy = y / ELEMS_PER_FLUID + 1;
        if(fx == 0 || fx == u[0].length - 1 || fy == 0 || fy == u.length - 1) {
            throw new IllegalArgumentException("x or y OOB");
        }
        return u[fy][fx];
    }

    public float getPressure(int x, int y) {
        int fx = x / ELEMS_PER_FLUID + 1;
        int fy = y / ELEMS_PER_FLUID + 1;
        if(fx == 0 || fx == u[0].length - 1 || fy == 0 || fy == u.length - 1) {
            throw new IllegalArgumentException("given coords OOB");
        }
        return p[fy][fx];
    }

    // TODO: 12/27/2023 consider refactoring drawing responsibility
    public void draw(RectDrawer shape) {
        for(int fy = 1; fy < u.length - 1; fy++) {
            for (int fx = 1; fx < u[0].length - 1; fx++) {
                int ex = 3 * (fx - 1);
                int ey = 3 * (fy - 1);
                // draw pressure
                shape.drawRect(ex, ey, ELEMS_PER_FLUID, ELEMS_PER_FLUID,
                        getPressureColor(p[fy][fx]));
                // draw velocity
                drawVector(ex, ey, u[fy][fx], shape);
            }
        }
    }

    private void drawVector(int ex, int ey, Vector2 v, RectDrawer shape) {
        if(v.len() > 1) {
            BiIntConsumer drawRedRect = (x, y) -> shape.drawRect(x, y, 1, 1, Color.RED);
            Shape.line(ex, ey, ex + (int) v.x, ey + (int) v.y, 1, drawRedRect);
        }
    }

    private Color getPressureColor(float p) {
        return new Color(0, p * 5, 0, 0);
    }
}

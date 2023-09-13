//package com.mygdx.game;
//
//import com.badlogic.gdx.math.Vector2;
//
//public class FluidManager {
//    private final int X_RES;
//    private final int Y_RES;
//    private Vector2[][] u;
//    private float[][] p;
//    private Vector2[][] uPrev;
//    private float[][] pPrev;
//
//    public FluidManager(int xRes, int yRes) {
//        u = new Vector2[xRes][yRes];
//        p = new float[xRes][yRes];
//        uPrev = new Vector2[xRes][yRes];
//        pPrev = new float[xRes][yRes];
//        X_RES = xRes;
//        Y_RES = yRes;
//    }
//
//    public void step() {
//        swap();
//
//        advect();
//        diffuse();
//        applyForces();
//        // at this point, we have w
//        project();  // transform w to u and calculate p
//    }
//
//    private void advect() {
//        for(int i = 0; i < X_RES; i++) {
//            for(int j = 0; j < Y_RES; j++) {
//                Vector2 coords = new Vector2(i - uPrev[i][j].x, j - uPrev[i][j].y);
//                // since this is the first operation, we need to set instead of add to clear u
//                u[i][j].set(bilerp(coords, u));
//            }
//        }
//    }
//
//    private void diffuse() {
//        for(int i = 0; i < X_RES; i++) {
//            for(int j = 0; j < Y_RES; j++) {
//                u[i][j]
//            }
//        }
//    }
//
//    private void applyForces() {
//
//    }
//
//    private void project() {
//
//    }
//
//    private void swap() {
//        Vector2[][] uTmp = u;
//        u = uPrev;
//        uPrev = uTmp;
//        float[][] pTmp = p;
//        p = pPrev;
//        pPrev = pTmp;
//    }
//
//    public interface VectorFn {
//        float apply(Vector2 v);
//    }
//
//    private float bilerp(Vector2 coords, Vector2[][] field, VectorFn fn) {
//        int x0 = (int) coords.x;
//        int y0 = (int) coords.y;
//        int x1 = x0 + 1;
//        int y1 = y0 + 1;
//        float wx = coords.x - x0;
//        float a = (1 - wx) * fn.apply(field[y0][x0]) + wx * fn.apply(field[y0][x1]);
//        float b = (1 - wx) * fn.apply(field[y1][x0]) + wx * fn.apply(field[y1][x1]);
//        float wy = coords.y - y0;
//        return (1 - wy) * a + wy * b;
//    }
//
//    private Vector2 bilerp(Vector2 coords, Vector2[][] field) {
//        return new Vector2(bilerp(coords, field, v -> v.x), bilerp(coords, field, v -> v.y));
//    }
//
//}
package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class FluidManager {
    final int X_RES, Y_RES;
    private final static float VISCOSITY = 0.2f;
    // velocity fields are composed of two scalar fields
    // s# are scratch arrays
    float[][] u, v, u0, v0, wx, wy, p, s0, s1, s2, s3, s4, s5;

    public FluidManager(int xRes, int yRes) {
        X_RES = xRes;
        Y_RES = yRes;
        u = new float[xRes][yRes];
        v = new float[xRes][yRes];
        p = new float[xRes][yRes];
        u0 = new float[xRes][yRes];
        v0 = new float[xRes][yRes];
        wx = new float[xRes][yRes];
        wy = new float[xRes][yRes];
        s0 = new float[xRes][yRes];
        s1 = new float[xRes][yRes];
        s2 = new float[xRes][yRes];
        s3 = new float[xRes][yRes];
        s4 = new float[xRes][yRes];
        s5 = new float[xRes][yRes];
    }

    public void step() {
        swap(u, u0);  // old u is now u from previous timestamp
        swap(v, v0);  // same with v
        advect();
        diffuse();
//        applyForces();
        project();
    }

    // input: u0, v0
    // output: s0, s1
    private void advect() {
        for(int i = 0; i < X_RES; i++) {
            for(int j = 0; j < Y_RES; j++) {
                float x = i - u0[i][j];
                float y = j - v0[i][j];
                s0[i][j] = bilerp(x, y, u0);
                s1[i][j] = bilerp(x, y, v0);
            }
        }
    }

    // input: s0, s1
    // output: wx, wy
    // requires: s2, s3
    private void diffuse() {
        float alpha = 1 / VISCOSITY;
        float beta = 4 + alpha;
        for(int k = 0; k < 20; k++) {
            // wx and wy store iteration k + 1, s2 and s3 store iteration k
            swap(wx, s2);
            swap(wy, s3);
            for(int i = 1; i < X_RES; i++) {
                for (int j = 1; j < Y_RES; j++) {
                    wx[i][j] = jacobi(s2, s0, i, j, alpha, beta);
                    wy[i][j] = jacobi(s3, s1, i, j, alpha, beta);
                }
            }
        }
        // at this point, wx and wy should store the final iteration of the Jacobi method
    }

    // input: wx, wy
    // output: u, v
    // requires: s4, zeroed p, zeroed s5
    private void project() {
        pressure();  // stored in p
        for (int i = 0; i < X_RES; i++) {
            for (int j = 0; j < Y_RES; j++) {
                u[i][j] -= (p[i+1][j] - p[i-1][j]) / 2;
                v[i][j] -= (p[i][j+1] - p[i][j-1]) / 2;
            }
        }

    }

    private void pressure() {
        reset(p);
        reset(s5);
        divergence();  // stored in s4
        for (int k = 0; k < 20; k++) {
            swap(p, s5);
            for (int i = 0; i < X_RES; i++) {
                for (int j = 0; j < Y_RES; j++) {
                    p[i][j] = jacobi(s5, s4, i, j, -1, 4);
                }
            }
        }
    }

    private void divergence() {
        for (int i = 1; i < X_RES; i++) {
            for (int j = 1; j < Y_RES; j++) {
                s4[i][j] = (u[i + 1][j] - u[i - 1][j]) / 2 + (v[i][j + 1] - v[i][j - 1]) / 2;
            }
        }
    }

    private void reset(float[][] f) {
        for (int i = 0; i < X_RES; i++) {
            for (int j = 0; j < Y_RES; j++) {
                f[i][j] = 0;
            }
        }
    }

    // 1 jacobi iteration at cell (i, j); returns the value of x(k + 1) at i and j
    private float jacobi(float[][] xk, float[][] b, int i, int j, float alpha, float beta) {
        return (xk[i-1][j] + xk[i+1][j] + xk[i][j-1] + xk[i][j+1] + alpha * b[i][j]) / beta;
    }

    private float bilerp(float x, float y, float[][] field) {
        int x0 = (int) x;
        int y0 = (int) y;
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        float wx = x - x0;
        float a = (1 - wx) * field[y0][x0] + wx * field[y0][x1];
        float b = (1 - wx) * field[y1][x0] + wx * field[y1][x1];
        float wy = y - y0;
        return (1 - wy) * a + wy * b;
    }

    private void swap(float[][] a, float[][] b) {
        float[][] tmp = a;
        a = b;
        b = tmp;
    }
}

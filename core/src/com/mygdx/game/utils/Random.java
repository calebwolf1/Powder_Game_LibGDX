package com.mygdx.game.utils;

public class Random {
    // TODO: 12/4/2023 make faster
    public static boolean randBool(float p) {
        return Math.random() < p;
    }

    public static boolean coinToss() {
        return randBool(0.5f);
    }

    public static float randFloat() {
        return (float) Math.random();
    }
}

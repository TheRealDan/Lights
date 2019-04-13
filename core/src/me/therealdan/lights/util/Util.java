package me.therealdan.lights.util;

public class Util {

    public static String getPercentage(float value) {
        return Float.toString(value * 100.0f).replace(".", "X").split("X")[0] + "%";
    }
}
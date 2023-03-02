package org.firstinspires.ftc.teamcode.viperCamp.core;

import org.firstinspires.ftc.robotcore.internal.system.Assert;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;

/**
 * Global utility functions
 */
public final class ViperUtils {
    private static final String TAG = "ViperUtils";
    private static final boolean enableMethodTrace = BuildConfig.DEBUG;
    public static final double EPSILON = 1e-3;

    /* Constructor */
    public ViperUtils() {
    }

    public static boolean areEqual(double a, double b) {
        return Math.abs(a - b) <= EPSILON;
    }

    public static boolean areEqual(float a, float b) {
        return Math.abs(a - b) <= EPSILON;
    }

    /**
     * A method to determine if a given value lies inside a given range.
     *
     * @param value    The value to evaluate
     * @param minValue The minimum (inclusive) value
     * @param maxValue The maximum (inclusive) value
     * @return Returns true if the given value is in the range [minValue, maxValue], false otherwise
     */
    public static boolean inRange(double value, double minValue, double maxValue) {
        return value >= minValue && value <= maxValue;
    }

    /**
     * A method to determine if a given value lies outside a given range.
     *
     * @param value    The value to evaluate
     * @param minValue The minimum value
     * @param maxValue The maximum value
     * @return Returns true if the given value is outside the range [minValue, maxValue], false otherwise
     */
    public static boolean outsideRange(double value, double minValue, double maxValue) {
        return value < minValue || value > maxValue;
    }

    public static boolean contains(int[] array, int value) {
        Assert.assertNotNull(array, "contains>array");
        for (int i : array) {
            if (i == value) return true;
        }

        return false;
    }

    public static int sign(double number) {
        return number > 0 ? 1 : -1;
    }

    /**
     * Sleep for given milli seconds.
     *
     * @param milliseconds The time to sleep in milliseconds.
     */
    public static void sleep(long milliseconds) {
        Assert.assertTrue(milliseconds >= 0, "sleep>milliseconds");
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }
}


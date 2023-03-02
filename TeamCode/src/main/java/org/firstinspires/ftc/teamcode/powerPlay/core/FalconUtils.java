package org.firstinspires.ftc.teamcode.powerPlay.core;

import org.firstinspires.ftc.robotcore.internal.system.Assert;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;

/**
 * Global utility functions
 */
public final class FalconUtils {
    private static final String TAG = "FalconUtils";
    private static final boolean enableMethodTrace = BuildConfig.DEBUG;
    public static final double EPSILON = 1e-3;

    /* Constructor */
    public FalconUtils() {
    }

    public static Scalar add(Scalar a, Scalar b) {
        return new Scalar(a.val[0] + b.val[0],
                a.val[1] + b.val[1],
                a.val[2] + b.val[2],
                a.val[3] + b.val[3]);
    }

    public static Scalar average(Scalar a, Scalar b) {
        return new Scalar((a.val[0] + b.val[0]) / 2.0,
                (a.val[1] + b.val[1]) / 2.0,
                (a.val[2] + b.val[2]) / 2.0,
                (a.val[3] + b.val[3]) / 2.0);
    }

    public static boolean areEqual(double a, double b) {
        return Math.abs(a - b) <= EPSILON;
    }

    public static boolean areEqual(float a, float b) {
        return Math.abs(a - b) <= EPSILON;
    }

    /**
     * Copies source Rect into destination Rect
     *
     * @param source
     * @param destination
     */
    public static void copyRect(Rect source, Rect destination) {
        synchronized (destination) {
            destination.x = source.x;
            destination.y = source.y;
            destination.height = source.height;
            destination.width = source.width;
        }
    }

    /**
     * Copies source Rect into destination Rect
     *
     * @param source
     * @param destination
     */
    public static void copyRect(RotatedRect source, RotatedRect destination) {
        synchronized (destination) {
            destination.center.x = source.center.x;
            destination.center.y = source.center.y;
            destination.size.height = source.size.height;
            destination.size.width = source.size.width;
            destination.angle = source.angle;
        }
    }

    /**
     * Zeros out the rect values.
     *
     * @param rect
     */
    public static void zeroRect(Rect rect) {
        synchronized (rect) {
            rect.x = 0;
            rect.y = 0;
            rect.height = 0;
            rect.width = 0;
        }
    }

    public static void zeroRect(RotatedRect rRect) {
        synchronized (rRect) {
            rRect.center.x = 0;
            rRect.center.y = 0;
            rRect.size.height = 0;
            rRect.size.width = 0;
            rRect.angle = 0;
        }
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

    public static double getMidpointX(Rect rect) {
        return rect.x + (rect.width / 2.0);
    }

    public static double getMidpointY(Rect rect) {
        return rect.y + (rect.height / 2.0);
    }

    public static Point getMidpoint(Rect rect) {
        return new Point(getMidpointX(rect), getMidpointY(rect));
    }

    public static double getAspectRatio(Rect rect) {
        if (rect.size().height == 0)
            return Double.MAX_VALUE;
        else
            return (double) rect.width / (double) rect.height;
    }

    /**
     * Returns ratio of width to height.
     *
     * @param rect
     * @return
     */
    public static double getAspectRatio(RotatedRect rect) {
        if (rect.size.height == 0)
            return Integer.MAX_VALUE;
        else
            return (double) rect.size.width / (double) rect.size.height;
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


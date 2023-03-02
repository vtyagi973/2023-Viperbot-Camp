package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.system.Assert;
import org.firstinspires.ftc.teamcode.BuildConfig;

/**
 * A global utility for managing robot execution logs.
 */
public final class FalconLogger {
    private static final String TAG = "FalconLogger";

    /* Constructor */
    public FalconLogger() {
    }

    public static void debug(String format, Object... args) {
        if (BuildConfig.DEBUG) {
            RobotLog.dd(TAG, format, args);
        }
    }

    public static void enter() {
        if (BuildConfig.DEBUG) {
            StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
            Assert.assertTrue(steArray.length > 3, "enter>steArray.length");
            StackTraceElement ste = steArray[3];
            RobotLog.dd(TAG, "%s.%s - enter", getClassNameOnly(ste.getClassName()), ste.getMethodName());
        }
    }

    public static void exit() {
        if (BuildConfig.DEBUG) {
            StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
            Assert.assertTrue(steArray.length > 3, "exit>steArray.length");
            StackTraceElement ste = steArray[3];
            RobotLog.dd(TAG, "%s.%s - exit", getClassNameOnly(ste.getClassName()), ste.getMethodName());
        }
    }

    private static String getClassNameOnly(String fullClassName) {
        Assert.assertNotNull(fullClassName, "getClassNameOnly>fullClassName");
        String classNameOnly;
        String[] dataArray = fullClassName.split("\\.");
        if (dataArray != null && dataArray.length > 0) {
            classNameOnly = dataArray[dataArray.length - 1];
        } else {
            classNameOnly = fullClassName;
        }

        return classNameOnly;
    }
}


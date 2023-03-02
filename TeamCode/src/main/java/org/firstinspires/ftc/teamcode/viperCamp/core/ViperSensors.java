package org.firstinspires.ftc.teamcode.viperCamp.core;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * A class to manage the parent sensors.
 */
public class ViperSensors {
    private static final String TAG = "ViperSensors";
    private HardwareMap hwMap = null;
    private Telemetry telemetry = null;
    private ViperBot parent = null;
    public boolean showTelemetry = true;

    private ViperSensors() {
    }

    /**
     * Initialize standard Hardware interfaces
     *
     * @param hardwareMap The hardware map to use for initialization.
     * @param telemetry   The telemetry to use.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry, ViperBot robot) {
        ViperLogger.enter();
        // Save reference to Hardware map
        hwMap = hardwareMap;
        this.telemetry = telemetry;
        this.parent = robot;
        showTelemetry();
        ViperLogger.exit();
    }

    public void showTelemetry() {
        ViperLogger.enter();
        ViperLogger.exit();
    }
}

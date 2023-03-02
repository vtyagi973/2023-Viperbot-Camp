package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * A class to manage the parent sensors.
 */
public class FalconSensors {
    private static final String TAG = "FalconSensors";
    private HardwareMap hwMap = null;
    private Telemetry telemetry = null;
    private FalconBot parent = null;
    public boolean showTelemetry = true;

    private FalconSensors() {
    }

    /**
     * Initialize standard Hardware interfaces
     *
     * @param hardwareMap The hardware map to use for initialization.
     * @param telemetry   The telemetry to use.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry, FalconBot robot) {
        FalconLogger.enter();
        // Save reference to Hardware map
        hwMap = hardwareMap;
        this.telemetry = telemetry;
        this.parent = robot;
        showTelemetry();
        FalconLogger.exit();
    }

    public void showTelemetry() {
        FalconLogger.enter();
        FalconLogger.exit();
    }
}

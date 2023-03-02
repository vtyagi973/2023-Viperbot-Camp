package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class FalconBot {
    private static final String TAG = "FalconBot";
    private boolean showTelemetry = true;
    public FalconBulkRead bulkRead = null;
    public FalconDriveTrain driveTrain = null;

    // hand, lift, picker, aligner
    public FalconGyro gyro = null;
    public FalconLift falconLift = null;
    public FalconConePicker conePicker = null;
    public FalconHand falconHand = null;
    HardwareMap hwMap = null;
    Telemetry telemetry = null;

    /* Constructor */
    public FalconBot() {
    }

    public void disableTelemetry() {
        FalconLogger.enter();
        showTelemetry = false;
        driveTrain.showTelemetry = showTelemetry;
        gyro.showTelemetry = showTelemetry;
        falconLift.showTelemetry = showTelemetry;
        conePicker.showTelemetry = showTelemetry;
        falconHand.showTelemetry = showTelemetry;
        FalconLogger.exit();
    }

    public void enableTelemetry() {
        FalconLogger.enter();
        showTelemetry = true;
        driveTrain.showTelemetry = showTelemetry;
        gyro.showTelemetry = showTelemetry;
        falconLift.showTelemetry = showTelemetry;
        conePicker.showTelemetry = showTelemetry;
        falconHand.showTelemetry = showTelemetry;
        FalconLogger.exit();
    }

    public boolean telemetryEnabled() {
        return showTelemetry;
    }

    /**
     * Initialize standard Hardware interfaces
     *
     * @param hardwareMap The hardware map to use for initialization.
     * @param telemetry   The telemetry to use
     * @param initWebCams If true, initializes the webcams. Otherwise save on processing by not initializing the webcams.
     *                    Typically, you would initialize the webcams in autoOps only.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry, Boolean initWebCams) {
        FalconLogger.enter();
        // Save reference to Hardware map
        hwMap = hardwareMap;
        this.telemetry = telemetry;

        bulkRead = new FalconBulkRead(hardwareMap);
        driveTrain = new FalconDriveTrain(this);
        driveTrain.init(hardwareMap, telemetry);
        gyro = new FalconGyro();
        gyro.init(hardwareMap, telemetry);
        falconLift = new FalconLift();
        falconLift.init(hardwareMap, telemetry);
        conePicker = new FalconConePicker();
        conePicker.init(hardwareMap, telemetry);
        falconHand = new FalconHand();
        falconHand.init(hardwareMap, telemetry, this);

        telemetry.addData(TAG, "initialized");
        FalconLogger.exit();
    }

    /**
     * Operate the robot in tele operation.
     */
    public void operateRobot(Gamepad gamepad1, Gamepad gamepad2, ElapsedTime loopTime) {
        FalconLogger.enter();
        // Drive operation
        driveTrain.fieldOrientedDrive(gamepad1, gamepad2, loopTime);

        // Hand operation
        falconHand.operateHand(gamepad1, gamepad2);

        // Cone picker operation
        conePicker.operateConePicker(gamepad1, gamepad2);

        // Lift operation
        falconLift.operateLift(gamepad1, gamepad2);

        if (showTelemetry) {
            gyro.showTelemetry();
            showGamePadTelemetry(gamepad1);
            driveTrain.showTelemetry();
            falconLift.showTelemetry();
            conePicker.showTelemetry();
            falconHand.showTelemetry();
        }

        FalconLogger.exit();
    }

    /**
     * Display game pad telemetry.
     *
     * @param gamePad The gamePad.
     */
    public void showGamePadTelemetry(Gamepad gamePad) {
        FalconLogger.enter();
        if (showTelemetry) {
            telemetry.addData("LeftStick", "%.2f %.2f",
                    gamePad.left_stick_x, gamePad.left_stick_y);
            telemetry.addData("RightStick", "%.2f, %.2f",
                    gamePad.right_stick_x, gamePad.right_stick_y);
        }

        FalconLogger.exit();
    }

    public void stopEverything() {
        FalconLogger.enter();
        if (driveTrain != null) {
            driveTrain.stop();
        }

        if (falconLift != null) {
            falconLift.stop();
        }

        FalconLogger.exit();
    }
}

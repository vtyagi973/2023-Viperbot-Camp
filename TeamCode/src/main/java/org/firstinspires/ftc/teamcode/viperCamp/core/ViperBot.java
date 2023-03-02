package org.firstinspires.ftc.teamcode.viperCamp.core;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ViperBot {
    private static final String TAG = "ViperBot";
    private boolean showTelemetry = true;
    public ViperBulkRead bulkRead = null;
    public ViperDriveTrain driveTrain = null;

    // hand, lift, picker, aligner
    public ViperGyro gyro = null;
    public ViperLift viperLift = null;
    public ViperConePicker conePicker = null;
    public ViperHand viperHand = null;
    HardwareMap hwMap = null;
    Telemetry telemetry = null;

    /* Constructor */
    public ViperBot() {
    }

    public void disableTelemetry() {
        ViperLogger.enter();
        showTelemetry = false;
        driveTrain.showTelemetry = showTelemetry;
        gyro.showTelemetry = showTelemetry;
        viperLift.showTelemetry = showTelemetry;
        conePicker.showTelemetry = showTelemetry;
        viperHand.showTelemetry = showTelemetry;
        ViperLogger.exit();
    }

    public void enableTelemetry() {
        ViperLogger.enter();
        showTelemetry = true;
        driveTrain.showTelemetry = showTelemetry;
        gyro.showTelemetry = showTelemetry;
        viperLift.showTelemetry = showTelemetry;
        conePicker.showTelemetry = showTelemetry;
        viperHand.showTelemetry = showTelemetry;
        ViperLogger.exit();
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
        ViperLogger.enter();
        // Save reference to Hardware map
        hwMap = hardwareMap;
        this.telemetry = telemetry;

        bulkRead = new ViperBulkRead(hardwareMap);
        driveTrain = new ViperDriveTrain(this);
        driveTrain.init(hardwareMap, telemetry);
        gyro = new ViperGyro();
        gyro.init(hardwareMap, telemetry);
        viperLift = new ViperLift();
        viperLift.init(hardwareMap, telemetry);
        conePicker = new ViperConePicker();
        conePicker.init(hardwareMap, telemetry);
        viperHand = new ViperHand();
        viperHand.init(hardwareMap, telemetry, this);

        telemetry.addData(TAG, "initialized");
        ViperLogger.exit();
    }

    /**
     * Operate the robot in tele operation.
     */
    public void operateRobot(Gamepad gamepad1, Gamepad gamepad2, ElapsedTime loopTime) {
        ViperLogger.enter();
        // Drive operation
        driveTrain.fieldOrientedDrive(gamepad1, gamepad2, loopTime);

        // Hand operation
        viperHand.operateHand(gamepad1, gamepad2);

        // Cone picker operation
        conePicker.operateConePicker(gamepad1, gamepad2);

        // Lift operation
        viperLift.operateLift(gamepad1, gamepad2);

        if (showTelemetry) {
            gyro.showTelemetry();
            showGamePadTelemetry(gamepad1);
            driveTrain.showTelemetry();
            viperLift.showTelemetry();
            conePicker.showTelemetry();
            viperHand.showTelemetry();
        }

        ViperLogger.exit();
    }

    /**
     * Display game pad telemetry.
     *
     * @param gamePad The gamePad.
     */
    public void showGamePadTelemetry(Gamepad gamePad) {
        ViperLogger.enter();
        if (showTelemetry) {
            telemetry.addData("LeftStick", "%.2f %.2f",
                    gamePad.left_stick_x, gamePad.left_stick_y);
            telemetry.addData("RightStick", "%.2f, %.2f",
                    gamePad.right_stick_x, gamePad.right_stick_y);
        }

        ViperLogger.exit();
    }

    public void stopEverything() {
        ViperLogger.enter();
        if (driveTrain != null) {
            driveTrain.stop();
        }

        if (viperLift != null) {
            viperLift.stop();
        }

        ViperLogger.exit();
    }
}

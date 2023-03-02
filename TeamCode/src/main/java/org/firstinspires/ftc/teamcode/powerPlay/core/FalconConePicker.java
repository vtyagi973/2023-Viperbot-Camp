package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

/**
 * A class to manage the robot cone picker.
 */
public class FalconConePicker {
    private static final String TAG = "FalconConePicker";
    public static final String CONE_PICKER_SERVO_NAME = "conePickerServo";
    private static final double UP_POSITION = 0.5610;
    private static final double DOWN_POSITION = 0.4890;
    private static final long UP_DOWN_TIME_MS = 500;
    public boolean showTelemetry = true;
    public boolean pickerIsUp;
    private HardwareMap hwMap = null;
    private Telemetry telemetry = null;
    private Servo conePickerServo = null;

    /**
     * Initialize standard Hardware interfaces
     *
     * @param hardwareMap The hardware map to use for initialization.
     * @param telemetry   The telemetry to use.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        FalconLogger.enter();
        // Save reference to Hardware map
        hwMap = hardwareMap;
        this.telemetry = telemetry;
        conePickerServo = hardwareMap.get(Servo.class, CONE_PICKER_SERVO_NAME);
        if (conePickerServo == null) {
            telemetry.addData(TAG, "%s not found. Check the servo connection. Re initialize", CONE_PICKER_SERVO_NAME);
        }

        // Set picker to up so that it can be moved down.
        pickerIsUp = true;
        moveDown(false);
        showTelemetry();
        telemetry.addData(TAG, "initialized");
        FalconLogger.exit();
    }

    /**
     * Moves the cone picker up.
     *
     * @param waitTillUp When true, waits till the cone picker is fully up.
     */
    public void moveUp(boolean waitTillUp) {
        FalconLogger.enter();
        if (!pickerIsUp) {
            if (conePickerServo != null) {
                conePickerServo.setPosition(UP_POSITION);
            } else {
                telemetry.addData(TAG, "%s not found. Check the servo connection.", CONE_PICKER_SERVO_NAME);
            }

            if (waitTillUp) {
                FalconUtils.sleep(UP_DOWN_TIME_MS);
            }

            pickerIsUp = true;
        }

        FalconLogger.exit();
    }

    /**
     * Moves the cone picker down.
     *
     * @param waitTillDown When true, waits till the cone picker is fully down.
     */
    public void moveDown(boolean waitTillDown) {
        FalconLogger.enter();
        if (pickerIsUp) {
            if (conePickerServo != null) {
                conePickerServo.setPosition(DOWN_POSITION);
            } else {
                telemetry.addData(TAG, "%s not found. Check the servo connection.", CONE_PICKER_SERVO_NAME);
            }

            if (waitTillDown) {
                FalconUtils.sleep(UP_DOWN_TIME_MS);
            }

            pickerIsUp = false;
        }

        FalconLogger.exit();
    }

    /**
     * Operates the cone picker using the gamepads.
     * Left bumper -> move the cone picker up
     * Left trigger -> move the cone picker down
     *
     * @param gamepad1 The first gamepad to use.
     * @param gamepad2 The second gamepad to use.
     */
    public void operateConePicker(Gamepad gamepad1, Gamepad gamepad2) {
        if (gamepad1.left_bumper || gamepad2.left_bumper) {
            moveUp(false);
        } else if (gamepad1.left_trigger >= 0.5 || gamepad2.left_trigger >= 0.5) {
            moveDown(false);
        }
    }

    /**
     * Emits cone picker telemetry. Helps with debugging.
     */
    public void showTelemetry() {
        FalconLogger.enter();
        if (showTelemetry && conePickerServo != null) {
            telemetry.addData(TAG, String.format(Locale.US, "Position %5.4f",
                    conePickerServo.getPosition()));
        }

        FalconLogger.exit();
    }
}

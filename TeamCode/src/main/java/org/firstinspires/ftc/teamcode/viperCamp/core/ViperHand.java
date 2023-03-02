package org.firstinspires.ftc.teamcode.viperCamp.core;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

/**
 * A class to manage the robot hand.
 */
public class ViperHand {
    private static final String TAG = "ViperHand";
    public static final String LEFT_PINCER_SERVO_NAME = "leftPincerServo";
    public static final String RIGHT_PINCER_SERVO_NAME = "rightPincerServo";
    private static final double LEFT_PINCER_CLOSE_POSITION = 0.4610;
    private static final double LEFT_PINCER_OPEN_POSITION = 0.5260;
    private static final double RIGHT_PINCER_CLOSE_POSITION = 0.5640;
    private static final double RIGHT_PINCER_OPEN_POSITION = 0.4920;
    private static final double HAND_SERVO_ERROR_MARGIN = 0.0010;
    private static final long CLOSE_TIME_MS = 400;
    private static final long OPEN_TIME_MS = 200;
    public boolean showTelemetry = true;
    public boolean enableDriverAssistance = false;
    public boolean handIsOpen;
    private HardwareMap hwMap = null;
    private Telemetry telemetry = null;
    private ViperBot parent = null;
    private Servo leftPincerServo = null;
    private Servo rightPincerServo = null;

    /* Constructor */
    public ViperHand() {
    }

    /**
     * Close the hand, thus grabbing the cone.
     *
     * @param waitTillClosed When true, waits till the hand closes.
     */
    public void close(boolean waitTillClosed) {
        ViperLogger.enter();
        if (handIsOpen) {
            if (leftPincerServo != null) {
                leftPincerServo.setPosition(LEFT_PINCER_CLOSE_POSITION);
            } else {
                telemetry.addData(TAG, "%s not found. Check the servo connection.", LEFT_PINCER_SERVO_NAME);
            }

            if (rightPincerServo != null) {
                rightPincerServo.setPosition(RIGHT_PINCER_CLOSE_POSITION);
            } else {
                telemetry.addData(TAG, "%s not found. Check the servo connection.", RIGHT_PINCER_SERVO_NAME);
            }

            if (waitTillClosed) {
                ViperUtils.sleep(CLOSE_TIME_MS);
            }

            handIsOpen = false;
        }

        ViperLogger.exit();
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
        parent = robot;
        leftPincerServo = hardwareMap.get(Servo.class, LEFT_PINCER_SERVO_NAME);
        if (leftPincerServo == null) {
            telemetry.addData(TAG, "%s not found. Check the servo connection.", LEFT_PINCER_SERVO_NAME);
        }

        rightPincerServo = hardwareMap.get(Servo.class, RIGHT_PINCER_SERVO_NAME);
        if (rightPincerServo == null) {
            telemetry.addData(TAG, "%s not found. Check the servo connection.", RIGHT_PINCER_SERVO_NAME);
        }

        // Set hand to open so that it can be closed.
        handIsOpen = true;
        //close(false);
        showTelemetry();
        telemetry.addData(TAG, "initialized");
        ViperLogger.exit();
    }

    /**
     * Open the hand, thus releasing the cone.
     *
     * @param waitTillOpen When true, waits till the hand opens fully.
     */
    public void open(boolean autoOpExecution, boolean waitTillOpen) {
        ViperLogger.enter();

        if (!handIsOpen) {
            if (enableDriverAssistance) {
                int targetPosition;
                if (autoOpExecution) {
                    targetPosition = parent.viperLift.getPosition() - ViperLift.LIFT_POSITION_MANUAL_CHANGE;
                } else {
                    targetPosition = ViperLift.LIFT_POSITION_SUB_STATION - ViperLift.endAutoOpLiftPosition;
                }

                if (parent.viperLift.getPosition() >
                        ViperLift.LIFT_POSITION_LOW_JUNCTION - ViperLift.LIFT_POSITION_ERROR_MARGIN - ViperLift.endAutoOpLiftPosition) {
                    parent.viperLift.moveLift(targetPosition, false);
                }
            }

            if (leftPincerServo != null) {
                leftPincerServo.setPosition(LEFT_PINCER_OPEN_POSITION);
            } else {
                telemetry.addData(TAG, "%s not found. Check the servo connection.", LEFT_PINCER_SERVO_NAME);
            }

            if (rightPincerServo != null) {
                rightPincerServo.setPosition(RIGHT_PINCER_OPEN_POSITION);
            } else {
                telemetry.addData(TAG, "%s not found. Check the servo connection.", RIGHT_PINCER_SERVO_NAME);
            }

            if (waitTillOpen) {
                ViperUtils.sleep(OPEN_TIME_MS);
            }

            handIsOpen = true;
        }

        ViperLogger.exit();
    }

    /**
     * Operates the hand using the gamepads.
     * Right bumper -> open the hand.
     * Right trigger -> close the hand.
     *
     * @param gamepad1 The first gamepad to use.
     * @param gamepad2 The second gamepad to use.
     */
    public void operateHand(Gamepad gamepad1, Gamepad gamepad2) {
        if (gamepad1.right_trigger >= 0.5 || gamepad2.right_trigger >= 0.5) {
            close(false);
        } else if (gamepad1.right_bumper || gamepad2.right_bumper) {
            open(false, false);
        }
    }

    /**
     * Emits lift motor telemetry. Helps with debugging.
     */
    public void showTelemetry() {
        ViperLogger.enter();
        if (showTelemetry && leftPincerServo != null && rightPincerServo != null) {
            telemetry.addData(TAG, String.format(Locale.US, "Left %5.4f, Right %5.4f",
                    leftPincerServo.getPosition(), rightPincerServo.getPosition()));
        }

        ViperLogger.exit();
    }
}

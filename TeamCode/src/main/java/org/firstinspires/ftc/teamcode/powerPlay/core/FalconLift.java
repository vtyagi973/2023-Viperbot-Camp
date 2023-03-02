package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.BuildConfig;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;

/**
 * A class to manage the robot lift.
 */
public class FalconLift {
    private static final String TAG = "FalconLift";
    public static final int CONE_HEIGHT = 600;
    public static final int LIFT_POSITION_FIFTH_CONE = 600;
    public static final int LIFT_POSITION_FOURTH_CONE = 560; // keep it close to 5th in case 5th is missed.
    public static final int LIFT_POSITION_THIRD_CONE = 400;
    public static final int LIFT_POSITION_SECOND_CONE = 300;
    public static final int LIFT_POSITION_HIGH_JUNCTION = 4200;
    public static final int LIFT_POSITION_MEDIUM_JUNCTION = 3000;
    public static final int LIFT_POSITION_LOW_JUNCTION = 1800;
    public static final int LIFT_POSITION_GROUND_JUNCTION = 130;
    public static final int LIFT_POSITION_SUB_STATION = 5;
    public static final int LIFT_POSITION_ABSOLUTE_MINIMUM = 0;
    public static final int LIFT_POSITION_ERROR_MARGIN = 10;
    public static final int LIFT_POSITION_MANUAL_CHANGE = 100;
    public static final int LIFT_POSITION_INVALID = Integer.MIN_VALUE;
    public static final double LIFT_UP_POWER = 1.0;
    public static final double LIFT_DOWN_POWER = -1.0;
    public static final double LIFT_TIME_MAX_MS = 3000;
    public static final double LIFT_TIME_HIGH_JUNCTION_MS = 2500;
    public static final double LIFT_TIME_MEDIUM_JUNCTION_MS = 2000;
    public static final double LIFT_TIME_LOW_JUNCTION_MS = 1500;
    public boolean showTelemetry = true;
    private HardwareMap hwMap = null;
    private Telemetry telemetry = null;
    public DcMotor liftMotor = null;
    public static int endAutoOpLiftPosition;

    /**
     * Estimate approximate time (in milliseconds) the lift will take
     * to travel from currentPosition to targetPosition.
     *
     * @param currentPosition Current position of the lift encoder.
     * @param targetPosition  Target position of the lift encoder.
     * @return Estimated lift travel time in milliseconds.
     */
    public double estimateLiftTravelTime(int currentPosition, int targetPosition) {
        // Factor is (LIFT_TIME_HIGH_JUNCTION_MS - LIFT_TIME_LOW_JUNCTION_MS) / (LIFT_TIME_HIGH_JUNCTION_MS - LIFT_TIME_LOW_JUNCTION_MS)
        int distance = Math.abs(Math.abs(targetPosition) - Math.abs(currentPosition));
        return 750 + distance * 5.0 / 12.0;
    }

    /**
     * Get the current lift position.
     *
     * @return The current lift position.
     */
    public int getPosition() {
        int position = LIFT_POSITION_INVALID;
        FalconLogger.enter();
        if (liftMotor != null) {
            position = liftMotor.getCurrentPosition();
        }

        FalconLogger.exit();
        return position;
    }

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
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        if (liftMotor != null) {
            liftMotor.setDirection(DcMotor.Direction.FORWARD);
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            telemetry.addData(TAG, "liftMotor unavailable, check motor connection.");
        }

        telemetry.addData(TAG, "endAutoOpLiftPosition=%d,", endAutoOpLiftPosition);

        showTelemetry();
        telemetry.addData(TAG, "initialized");
        FalconLogger.exit();
    }

    /**
     * Determines if the lift is near its target position.
     *
     * @param currentPosition Lift's current position.
     * @param targetPosition  Lift's target position.
     * @return True if lift is close to its target position, false otherwise.
     */
    public Boolean liftNearTarget(int currentPosition, int targetPosition) {
        return Math.abs(currentPosition - targetPosition) <= LIFT_POSITION_ERROR_MARGIN;
    }

    /**
     * Operates lift based on gamepad inputs.
     *
     * @param gamepad1 The gamepad1 to control the lift operation.
     * @param gamepad2 The gamepad2 to control the lift operation.
     */
    public void operateLift(Gamepad gamepad1, Gamepad gamepad2) {
        if (liftMotor != null) {
            int targetLiftPosition = LIFT_POSITION_INVALID;
            if (gamepad1.dpad_down && gamepad2.dpad_down) {
                // Manually lower the lift stuck at a high position.
                endAutoOpLiftPosition += 2;
                if (BuildConfig.DEBUG) {
                    telemetry.addData(TAG, "Increased lift initial position to %d", endAutoOpLiftPosition);
                }
            }

            // If lift zero is being reset, we want lower the lift physically as well.
            if (gamepad1.a || gamepad2.a) {
                targetLiftPosition = FalconLift.LIFT_POSITION_SUB_STATION - endAutoOpLiftPosition;
            } else if (gamepad1.x || gamepad2.x) {
                targetLiftPosition = FalconLift.LIFT_POSITION_LOW_JUNCTION - endAutoOpLiftPosition;
            } else if (gamepad1.b || gamepad2.b) {
                targetLiftPosition = FalconLift.LIFT_POSITION_MEDIUM_JUNCTION - endAutoOpLiftPosition;
            } else if (gamepad1.y || gamepad2.y) {
                targetLiftPosition = FalconLift.LIFT_POSITION_HIGH_JUNCTION - endAutoOpLiftPosition;
            } else if (gamepad1.dpad_up || gamepad2.dpad_up) {
                if (liftNearTarget(liftMotor.getCurrentPosition(), LIFT_POSITION_SUB_STATION - endAutoOpLiftPosition)) {
                    // Only when the lift is at intake level, bump it to the ground junction height.
                    targetLiftPosition = FalconLift.LIFT_POSITION_GROUND_JUNCTION - endAutoOpLiftPosition;
                } else {
                    targetLiftPosition = liftMotor.getCurrentPosition() + LIFT_POSITION_MANUAL_CHANGE;
                }
            } else if (gamepad1.dpad_down || gamepad2.dpad_down) {
                targetLiftPosition = liftMotor.getCurrentPosition() - LIFT_POSITION_MANUAL_CHANGE;
            }

            if (targetLiftPosition != LIFT_POSITION_INVALID) {
                moveLift(targetLiftPosition, false);
            }
        } else {
            telemetry.addData(TAG, "liftMotor unavailable, check motor connection.");
        }
    }

    /**
     * Moves lift to the target motor encoder position.
     *
     * @param targetPosition The target motor encoder position.
     * @param waitTillEnd    When true, waits for lift to reach target position.
     */
    public void moveLift(int targetPosition, boolean waitTillEnd) {
        if (liftMotor != null) {
            targetPosition = Range.clip(targetPosition,
                    LIFT_POSITION_ABSOLUTE_MINIMUM - endAutoOpLiftPosition,
                    LIFT_POSITION_HIGH_JUNCTION - endAutoOpLiftPosition);
            int currentPosition = liftMotor.getCurrentPosition();
            if (targetPosition != currentPosition) {
                // Must set motor position before setting motor mode.
                liftMotor.setTargetPosition(targetPosition);
                liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            double liftPower = FalconDriveTrain.ZERO_POWER;
            if (targetPosition > currentPosition) {
                liftPower = LIFT_UP_POWER;
            } else if (targetPosition < currentPosition) {
                liftPower = LIFT_DOWN_POWER;
            }

            liftMotor.setPower(liftPower);
            if (waitTillEnd) {
                double waitTime = estimateLiftTravelTime(currentPosition, targetPosition);
                ElapsedTime runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
                while (!liftNearTarget(liftMotor.getCurrentPosition(), targetPosition) &&
                        runtime.milliseconds() < waitTime) {
                    FalconUtils.sleep(10);
                }
            }
        } else {
            telemetry.addData(TAG, "liftMotor unavailable, check motor connection.");
        }
    }

    /**
     * Emits lift motor telemetry. Helps with debugging.
     */
    public void showTelemetry() {
        FalconLogger.enter();
        if (showTelemetry && liftMotor != null) {
            telemetry.addData(TAG, String.format(Locale.US, "Power %.2f, distance %d",
                    liftMotor.getPower(), liftMotor.getCurrentPosition()));
        }

        FalconLogger.exit();
    }

    /**
     * Stops the lift by setting lift motor power to zero.
     */
    public void stop() {
        if (liftMotor != null) {
            liftMotor.setPower(FalconDriveTrain.ZERO_POWER);
        }
    }
}

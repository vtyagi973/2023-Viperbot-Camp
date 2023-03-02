package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;
import java.util.List;

/**
 * A class to manage the robot drive train.
 * Note:  All names are lower case and have no spaces between words.
 * <p>
 * Motor:  Left  drive motor: "leftFrontMotor"
 * Motor:  Left  drive motor: "leftRearMotor"
 * Motor:  Right drive motor: "rightFrontMotor"
 * Motor:  Right drive motor: "rightRearMotor"
 */
public class FalconDriveTrain {
    private static final String TAG = "FalconDriveTrain";
    public static final double MAXIMUM_FORWARD_POWER = 1.00;
    public static final double MECANUM_POWER_BOOST_FACTOR = 0.90;
    public static final double CRAWL_TELE_OP_FACTOR = 5.0;
    public static final double MINIMUM_FORWARD_TELE_OP_POWER = 0.10;
    public static final double MAXIMUM_TURN_POWER = 0.75;
    public static final double MINIMUM_TURN_POWER = 0.20;
    public static final double ZERO_POWER = 0.0;

    static final double JITTER = 0.01;
    static final double POWER_RAMP_UP_DOWN_TIME = 200.0; // milliseconds

    static final double KP_DRIVE = 0.01;// Larger is more responsive, but also less stable
    public DcMotor.ZeroPowerBehavior ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
    public boolean showTelemetry = true;
    private boolean precisionDriveMode = false;
    HardwareMap hwMap = null;
    Telemetry telemetry = null;
    private final FalconBot parent;
    private DcMotor leftFrontMotor = null;
    private DcMotor leftRearMotor = null;
    private DcMotor rightFrontMotor = null;
    private DcMotor rightRearMotor = null;
    private List<DcMotor> motors = null;

    // Strafing correction for unbalanced robot
    private double lastY = 0;
    private double lastX = 0;
    private double lastHeading = 0;
    private final Boolean useMotorEncoders = false;
    private final boolean enableUnbalancedRobotCorrection = true;
    private final boolean enableMecanumPowerBoost = true;

    public FalconDriveTrain(FalconBot robot) {
        parent = robot;
    }

    /**
     * Moves the robot maintaining robot heading relative to the field.
     *
     * @param gamePad1 The first gamePad to use for driving.
     * @param gamePad2 The second gamePad to use for driving.
     * @param loopTime The loopTime passed in by TeleOp that determines how fast the TeleOp loop
     *                 is executing. Shorter the loopTime, shorter the braking power step.
     */
    public void fieldOrientedDrive(Gamepad gamePad1, Gamepad gamePad2, ElapsedTime loopTime) {
        FalconLogger.enter();

        // Setup a variable for each side drive wheel to display power level for telemetry
        double leftFrontPower = ZERO_POWER;
        double leftRearPower = ZERO_POWER;
        double rightFrontPower = ZERO_POWER;
        double rightRearPower = ZERO_POWER;

        // Use left stick for forward/backward/strafe, use right stick for turn
        double y = -gamePad1.left_stick_y;
        double x = gamePad1.left_stick_x;
        double yMagnitude = Math.abs(y);
        double xMagnitude = Math.abs(x);
        double powerMagnitude = Math.max(yMagnitude, xMagnitude);
        double maxWheelPower;
        if (powerMagnitude > JITTER) {
            // All angles are in radians
            // Calculate default formulaic wheel power.
            double theta = Math.atan2(y, x) - (parent.gyro.Heading + FalconGyro.endAutoOpHeading) * Math.PI / 180.0;
            leftFrontPower = rightRearPower = Math.sin(theta + Math.PI / 4.0) * powerMagnitude;
            rightFrontPower = leftRearPower = Math.sin(theta - Math.PI / 4.0) * powerMagnitude;

            if (enableMecanumPowerBoost) {
                // Boost power up to the powerMagnitude
                maxWheelPower = Math.max(Math.abs(leftFrontPower), Math.abs(leftRearPower));
                maxWheelPower = Math.max(maxWheelPower, Math.abs(rightFrontPower));
                maxWheelPower = Math.max(maxWheelPower, Math.abs(rightRearPower));
                if (maxWheelPower < powerMagnitude) {
                    powerMagnitude *= MECANUM_POWER_BOOST_FACTOR;
                    leftFrontPower = powerMagnitude * leftFrontPower / maxWheelPower;
                    leftRearPower = powerMagnitude * leftRearPower / maxWheelPower;
                    rightFrontPower = powerMagnitude * rightFrontPower / maxWheelPower;
                    rightRearPower = powerMagnitude * rightRearPower / maxWheelPower;
                }
            }
        }

        double turn = gamePad1.right_stick_x;
        double turnMagnitude = Math.abs(turn);
        if (turnMagnitude <= JITTER) {
            turn = 0;

            // Apply fix only if driver is not explicitly turning the robot
            if (enableUnbalancedRobotCorrection) {
                // Fix for left/right pull for unbalanced robot
                // TODO compute the tangent and not compare raw x, y
                // Tangent comparison will maintain heading when driver changes power
                // proportionally to slow-down/speed-up AND wants to maintain heading.
                if (powerMagnitude > JITTER && FalconUtils.areEqual(lastX, xMagnitude)
                        && FalconUtils.areEqual(lastY, yMagnitude)) {
                    // maintain last heading by turning the robot slightly, if needed.
                    double headingOffset = parent.gyro.getHeadingOffset(lastHeading);
                    turn = parent.gyro.getSteeringCorrection(headingOffset, KP_DRIVE);
                } else {
                    // Driver is changing power, not orientation
                    // Store power only, not the Heading
                    lastX = xMagnitude;
                    lastY = yMagnitude;
                }
            }
        } else {
            int turnDirection = FalconUtils.sign(turn);
            turn = MINIMUM_TURN_POWER + turnMagnitude * (MAXIMUM_TURN_POWER - MINIMUM_TURN_POWER);
            turn *= turnDirection;
            if (enableUnbalancedRobotCorrection) {
                // Driver is changing robot orientation explicitly.
                // Store new heading
                lastHeading = parent.gyro.Heading;
            }
        }

        // Add turn power to linear power
        leftFrontPower += turn;
        leftRearPower += turn;
        rightFrontPower -= turn;
        rightRearPower -= turn;

        // Constrain powers in the range of [-MAXIMUM_FORWARD_POWER,MAXIMUM_FORWARD_POWER]
        // Not checking would cause the robot to always drive at full speed
        maxWheelPower = Math.max(Math.abs(leftFrontPower), Math.abs(leftRearPower));
        maxWheelPower = Math.max(maxWheelPower, Math.abs(rightFrontPower));
        maxWheelPower = Math.max(maxWheelPower, Math.abs(rightRearPower));

        if (maxWheelPower > MAXIMUM_FORWARD_POWER) {
            // Divide everything by max
            // it's positive so we don't need to worry about signs
            leftFrontPower /= maxWheelPower;
            leftRearPower /= maxWheelPower;
            rightFrontPower /= maxWheelPower;
            rightRearPower /= maxWheelPower;
        }

        // Handle precision drive mode
        if (gamePad1.dpad_left || gamePad2.dpad_left) {
            // set global state variable
            precisionDriveMode = true;
        } else if (gamePad1.dpad_right || gamePad2.dpad_right) {
            // set global state variable
            precisionDriveMode = false;
        } else {
            // Maintain last precision mode.
        }

        if (precisionDriveMode) {
            // E.g. Robot is turning with minimum power
            // Precision mode should not make it turn slower than minimum
            maxWheelPower = Math.max(Math.abs(leftFrontPower), Math.abs(leftRearPower));
            maxWheelPower = Math.max(maxWheelPower, Math.abs(rightFrontPower));
            maxWheelPower = Math.max(maxWheelPower, Math.abs(rightRearPower));

            // No matter the joystick magnitude, the max power will be MINIMUM_TURN_POWER
            // This means that in precision mode, robot will move with MINIMUM_TURN_POWER in any
            // direction or rotation
            double crawlFactor = maxWheelPower / MINIMUM_TURN_POWER;
            leftFrontPower /= crawlFactor;
            leftRearPower /= crawlFactor;
            rightFrontPower /= crawlFactor;
            rightRearPower /= crawlFactor;
        }

        setDrivePowerSmooth(leftFrontPower, leftRearPower, rightFrontPower, rightRearPower, loopTime);
        FalconLogger.exit();
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

        // Initialize the hardware variables.
        leftFrontMotor = hardwareMap.get(DcMotor.class, "leftFrontMotor");
        leftRearMotor = hardwareMap.get(DcMotor.class, "leftRearMotor");
        rightFrontMotor = hardwareMap.get(DcMotor.class, "rightFrontMotor");
        rightRearMotor = hardwareMap.get(DcMotor.class, "rightRearMotor");

        motors = Arrays.asList(leftFrontMotor, leftRearMotor, rightFrontMotor, rightRearMotor);

        // Reverse the motor that runs backwards when connected via a gear to the wheel.
        leftFrontMotor.setDirection(DcMotor.Direction.FORWARD);
        leftRearMotor.setDirection(DcMotor.Direction.FORWARD);
        rightFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        rightRearMotor.setDirection(DcMotor.Direction.REVERSE);

        for (DcMotor motor : motors) {
            if (useMotorEncoders) {
                // Resetting encoders is important. If the RC is not reset, encoder counts keep
                // going up/down as team operates the robot.
                motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                // After STOP_AND_RESET_ENCODER, must specify the runMode
                // Otherwise motor power may stay off indefinitely.
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            } else {
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }

        showTelemetry();
        telemetry.addData("Robot", "initialized");
        FalconLogger.exit();
    }

    /**
     * POV Drive
     *
     * @param gamePad The gamePad used for driving.
     */
    public void povDrive(Gamepad gamePad) {
        FalconLogger.enter();
        // Setup a variable for each side drive wheel to display power level for telemetry
        double leftFrontPower;
        double leftRearPower;
        double rightFrontPower;
        double rightRearPower;

        // POV Mode uses left stick to go forward/backward, and right stick to turn left/right.
        double drive = -gamePad.left_stick_y;
        double driveMagnitude = Math.abs(drive);
        int driveDirection;
        if (driveMagnitude <= JITTER) {
            drive = ZERO_POWER;
        } else {
            driveDirection = FalconUtils.sign(drive);
            drive = MINIMUM_FORWARD_TELE_OP_POWER + driveMagnitude * (MAXIMUM_FORWARD_POWER - MINIMUM_FORWARD_TELE_OP_POWER);
            drive *= driveDirection;
            if (gamePad.left_stick_button) {
                drive /= CRAWL_TELE_OP_FACTOR;
            }
        }

        double turn = gamePad.right_stick_x;
        double turnMagnitude = Math.abs(turn);
        if (turnMagnitude <= JITTER) {
            turn = ZERO_POWER;
        } else {
            int turnDirection = FalconUtils.sign(turn);
            turn = MINIMUM_TURN_POWER + turnMagnitude * (MAXIMUM_TURN_POWER - MINIMUM_TURN_POWER);
            turn *= turnDirection;
            if (gamePad.right_stick_button) {
                turn /= CRAWL_TELE_OP_FACTOR;
            }
        }

        double strafe = -gamePad.left_stick_x;
        double strafeMagnitude = Math.abs(strafe);
        int strafeDirection;
        if (strafeMagnitude <= JITTER) {
            strafe = ZERO_POWER;
        } else {
            strafeDirection = FalconUtils.sign(strafe);
            strafe = MINIMUM_TURN_POWER + strafeMagnitude * (MAXIMUM_TURN_POWER - MINIMUM_TURN_POWER);
            strafe *= strafeDirection;
            if (gamePad.left_stick_button) {
                strafe /= CRAWL_TELE_OP_FACTOR;
            }
        }

        // Calculate individual motor power
        leftFrontPower = drive - strafe + turn;
        leftRearPower = drive + strafe + turn;
        rightFrontPower = drive + strafe - turn;
        rightRearPower = drive - strafe - turn;

        // Constrain powers in the range of [-MAXIMUM_FORWARD_POWER,MAXIMUM_FORWARD_POWER]
        // Not checking would cause the robot to always drive at full speed
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(leftRearPower));
        max = Math.max(max, Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(rightRearPower));

        if (max > MAXIMUM_FORWARD_POWER) {
            // Divide everything by max
            // it's positive so we don't need to worry about signs
            leftFrontPower /= max;
            leftRearPower /= max;
            rightFrontPower /= max;
            rightRearPower /= max;
        }

        setDrivePowerSmooth(leftFrontPower, leftRearPower, rightFrontPower, rightRearPower, new ElapsedTime());
        showTelemetry();
        FalconLogger.exit();
    }

    /**
     * Send given power to respective wheels.
     */
    public void setDrivePower(double leftFrontPower, double leftRearPower, double rightFrontPower, double rightRearPower) {
        FalconLogger.enter();
        leftFrontMotor.setPower(leftFrontPower);
        leftRearMotor.setPower(leftRearPower);
        rightFrontMotor.setPower(rightFrontPower);
        rightRearMotor.setPower(rightRearPower);
        FalconLogger.exit();
    }

    /**
     * Increase/decrease motor power incrementally for a smooth acceleration/deceleration.
     * Must call the method repeatedly (e.g. via tele Op).
     *
     * @param leftFrontPower  Left front motor power
     * @param leftRearPower   Left rear motor power
     * @param rightFrontPower Right front motor power
     * @param rightRearPower  Right rear motor power
     * @param loopTime        The loopTime passed in by TeleOp that determines how fast the TeleOp loop
     *                        is executing. Shorter the loopTime, shorter the braking power step.
     */
    public void setDrivePowerSmooth(double leftFrontPower, double leftRearPower,
                                    double rightFrontPower, double rightRearPower, ElapsedTime loopTime) {
        FalconLogger.enter();
        setDrivePowerSmooth(leftFrontMotor, leftFrontPower, loopTime);
        setDrivePowerSmooth(leftRearMotor, leftRearPower, loopTime);
        setDrivePowerSmooth(rightFrontMotor, rightFrontPower, loopTime);
        setDrivePowerSmooth(rightRearMotor, rightRearPower, loopTime);
        FalconLogger.exit();
    }

    /**
     * Increases or decreases the power incrementally for a smooth acceleration/deceleration.
     *
     * @param motor    The motor to set the power to.
     * @param power    The power to set to the motor to.
     * @param loopTime The loopTime passed in by TeleOp that determines how fast the TeleOp loop
     *                 is executing. Shorter the loopTime, shorter the braking power step.
     */
    public void setDrivePowerSmooth(DcMotor motor, double power, ElapsedTime loopTime) {
        // loopTime may not have been initialized. Minimum time is 1ms.
        double loopTimeMs = Math.max(1, loopTime.milliseconds());

        // Power step needs to be directly proportional to the loop time.
        // Shorter loop time => use shorter power step, experiment and adjust POWER_RAMP_UP_DOWN_TIME as needed.
        // E.g.: If tele Op loop time is 5ms, then it would require 5 * 1.0 / 200 = .025 power step
        // increment per cycle to go from stationary (0) to full (1) speed in 200ms.
        double powerStep = loopTimeMs * MAXIMUM_FORWARD_POWER / POWER_RAMP_UP_DOWN_TIME;
        double currentPower = motor.getPower();
        double delta = power - currentPower;
        double newPower;
        if (Math.abs(delta) >= powerStep)
            newPower = currentPower + powerStep * Math.signum(delta);
        else
            newPower = power;

        motor.setPower(newPower);
    }

    /**
     * Sets the zero power behavior for the drive motors.
     *
     * @param zeroPowerBehavior The zero power behavior (brake or roll).
     */
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        FalconLogger.enter();
        ZeroPowerBehavior = zeroPowerBehavior;
        for (DcMotor motor : motors) {
            motor.setZeroPowerBehavior(ZeroPowerBehavior);
        }

        FalconLogger.exit();
    }

    /**
     * Display motor power.
     */
    public void showTelemetry() {
        FalconLogger.enter();
        if (showTelemetry) {
            telemetry.addData("Left Front Motor", "power %.2f distance %d", leftFrontMotor.getPower(), leftFrontMotor.getCurrentPosition());
            telemetry.addData("Left Rear Motor", "power %.2f distance %d", leftRearMotor.getPower(), leftRearMotor.getCurrentPosition());
            telemetry.addData("Right Front Motor", "power %.2f distance %d", rightFrontMotor.getPower(), rightFrontMotor.getCurrentPosition());
            telemetry.addData("Right Rear Motor", "power %.2f distance %d", rightRearMotor.getPower(), rightRearMotor.getCurrentPosition());
        }

        FalconLogger.exit();
    }

    /**
     * Stops the robot by sending zero power to all drive motors.
     */
    public void stop() {
        FalconLogger.enter();
        setDrivePower(ZERO_POWER, ZERO_POWER, ZERO_POWER, ZERO_POWER);
        FalconLogger.exit();
    }
}

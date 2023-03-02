package org.firstinspires.ftc.teamcode.viperCamp.core;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.system.Assert;

/**
 * A class to manage the built-in Gyroscope.
 */
public class ViperGyro {
    private static final String TAG = "ViperGyro";
    static final double HEADING_THRESHOLD = 0.20;
    public boolean showTelemetry = true;
    Telemetry telemetry = null;
    private BNO055IMU imu = null;
    private Orientation angles = null;
    public static double endAutoOpHeading = 0;
    private double initialTeleOpHeading = 0.0;
    private double initialTeleOpPitch = 0.0;
    private double initialTeleOpRoll = 0.0;

    public static double Heading = 0.0;
    public static double Pitch = 0.0;
    public static double Roll = 0.0;

    /* Constructor */
    public ViperGyro() {
    }

    /**
     * Get the IMU heading in degrees.
     * Heading is the Z axis orientation of the imu in the Control Hub.
     * Z increases when robot turns left!
     *
     * @return The robot heading.
     */
    private double getHeading() {
        ViperLogger.enter();
        Assert.assertNotNull(imu, "getHeading>imu");
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        ViperLogger.exit();
        return angles.firstAngle;
    }

    /**
     * Determine the difference between the target heading and the robot's current heading
     *
     * @param targetHeading Desired heading (relative to global reference established at last gyro Reset).
     * @return headingOffset Degrees in the range +/- 180. Centered on the robot's frame of reference
     * Positive headingOffset means the robot should spinAbsolute THREE (Clockwise) (CW) to reduce error.
     */
    public double getHeadingOffset(double targetHeading) {
        ViperLogger.enter();
        // Z increases when robot turns left!
        // Heading is never more than 180 or less than -180

        // Normalize targetHeading to be within [-359, 359].
        while (targetHeading >= 360) targetHeading -= 360.0;
        while (targetHeading <= -360) targetHeading += 360.0;

        // Normalize targetHeading to be within [-180, 179.9999]
        if (targetHeading >= 180)
            targetHeading = targetHeading - 360;
        else if (targetHeading < -180)
            targetHeading = targetHeading + 360;

        double headingOffset;
        if (targetHeading * Heading >= 0) {
            // Target and Heading are either both positive or both negative
            headingOffset = Heading - targetHeading;
        } else {
            // They are of opposite signs
            headingOffset = targetHeading + Heading;
        }

        ViperLogger.exit();
        return headingOffset;
    }

    /**
     * Get the IMU roll in degrees.
     * Roll is the Distance axis orientation of the IMU in the Control Hub.
     * Roll increases when the robot nose dives!
     *
     * @param The robot roll.
     */
    private double getRoll() {
        ViperLogger.enter();
        Assert.assertNotNull(imu, "getRoll>imu");
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        ViperLogger.exit();
        return angles.secondAngle;
    }

    /**
     * Get the IMU pitch in degrees.
     * Pitch is the Heading axis orientation of the IMU in the Control Hub.
     * Pitch increases when the right robot side goes down.
     *
     * @param The robot pitch
     */
    private double getPitch() {
        ViperLogger.enter();
        Assert.assertNotNull(imu, "getPitch>imu");
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        ViperLogger.exit();
        return angles.thirdAngle;
    }

    /**
     * Returns desired steering correction force within [-1,1] range.
     * Positive correction force implies robot should steer right
     *
     * @param headingOffset Robot heading offset in degrees
     * @param pCoEff        Proportional Gain Coefficient
     * @return The steering correction after applying the proportional gain coefficient
     */
    public double getSteeringCorrection(double headingOffset, double pCoEff) {
        ViperLogger.enter();
        double steeringCorrection = headingOffset * pCoEff;
        ViperLogger.exit();
        return steeringCorrection;
    }

    /**
     * Initialize IMU
     *
     * @param hardwareMap The hardware map to use for initialization.
     * @param telemetry   The telemetry object to use.
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        ViperLogger.enter();
        // Save reference to Hardware map
        this.telemetry = telemetry;
        BNO055IMU.Parameters imuParameters;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        Assert.assertNotNull(imu, "init>imu");

        // Create new IMU Parameters object.
        imuParameters = new BNO055IMU.Parameters();

        // Use degrees as angle unit.
        imuParameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;

        // Express acceleration as m/s^2.
        imuParameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        // Disable logging.
        imuParameters.loggingEnabled = false;

        // IMU must be initialized
        imu.initialize(imuParameters);
        while (!imu.isGyroCalibrated()) {
            // Wait till gyro is fully calibrated.
            ViperUtils.sleep(100);
        }

        read();
        if (endAutoOpHeading == 0) {
            initialTeleOpHeading = Heading;
        } else {
            initialTeleOpHeading = endAutoOpHeading;
        }

        initialTeleOpRoll = Roll;
        initialTeleOpPitch = Pitch;

        showTelemetry();
        telemetry.addData("gyro", "initialized");
        ViperLogger.exit();
    }

    /**
     * Read the gyro and store information for later use.
     */
    public void read() {
        ViperLogger.enter();
        Assert.assertNotNull(imu, "read>imu");
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        Heading = angles.firstAngle;
        Roll = angles.secondAngle;
        Pitch = angles.thirdAngle;
        ViperLogger.exit();
    }

    /**
     * Display IMU (gyro) telemetry.
     */
    public void showTelemetry() {
        ViperLogger.enter();
        if (showTelemetry) {
            telemetry.addData("Heading (Z)", "%.2f, offset: %.2f", Heading, initialTeleOpHeading);
            telemetry.addData("Roll", Roll);
            telemetry.addData("Pitch", Pitch);
        }

        ViperLogger.exit();
    }
}


package org.firstinspires.ftc.teamcode.roadRunner.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.roadRunner.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadRunner.drive.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.roadRunner.drive.TwoWheelTrackingLocalizer;

/**
 * This is a simple teleop routine for testing localization. Drive the robot around like a normal
 * teleop routine and make sure the robot's estimated pose matches the robot's actual pose (slight
 * errors are not out of the ordinary, especially with sudden drive motions). The goal of this
 * exercise is to ascertain whether the localizer has been configured properly (note: the pure
 * encoder localizer heading may be significantly off if the track width has not been tuned).
 */
@Disabled
@TeleOp(group = "drive")
public class LocalizationTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftFrontMotor = null, leftRearMotor = null;
        DcMotor rightFrontMotor = null, rightRearMotor = null;
        DcMotor leftEncoder = null, frontEncoder = null, rightEncoder = null;
        DcMotor parallelEncoder = null, perpendicularEncoder = null;
        int startParallelPosition = 0, startPerpendicularPosition = 0;
        int startLeftPosition = 0, startFrontPosition = 0, startRightPosition = 0;

        if (DriveConstants.RUN_USING_ENCODER) {
            leftFrontMotor = hardwareMap.get(DcMotor.class, "leftFrontMotor");
            leftRearMotor = hardwareMap.get(DcMotor.class, "leftRearMotor");
            rightFrontMotor = hardwareMap.get(DcMotor.class, "rightFrontMotor");
            rightRearMotor = hardwareMap.get(DcMotor.class, "rightRearMotor");
            rightFrontMotor.setDirection(DcMotor.Direction.REVERSE);
            rightRearMotor.setDirection(DcMotor.Direction.REVERSE);
        }

        if (SampleMecanumDrive.use2WheelTrackingLocalizer) {
            parallelEncoder = hardwareMap.get(DcMotor.class, "parallelEncoder");
            perpendicularEncoder = hardwareMap.get(DcMotor.class, "perpendicularEncoder");
            parallelEncoder.setDirection(DcMotor.Direction.REVERSE);
            perpendicularEncoder.setDirection(DcMotor.Direction.REVERSE);
            startParallelPosition = parallelEncoder.getCurrentPosition();
            startPerpendicularPosition = perpendicularEncoder.getCurrentPosition();
        } else if (SampleMecanumDrive.use3WheelTrackingLocalizer) {
            leftEncoder = hardwareMap.get(DcMotor.class, "leftEncoder");
            frontEncoder = hardwareMap.get(DcMotor.class, "frontEncoder");
            rightEncoder = hardwareMap.get(DcMotor.class, "rightEncoder");
            leftEncoder.setDirection(DcMotor.Direction.REVERSE);
            frontEncoder.setDirection(DcMotor.Direction.REVERSE);
            rightEncoder.setDirection(DcMotor.Direction.REVERSE);
            startLeftPosition = leftEncoder.getCurrentPosition();
            startFrontPosition = frontEncoder.getCurrentPosition();
            startRightPosition = rightEncoder.getCurrentPosition();
        } else {
            telemetry.addData("Misconfiguration", "No localizer specified. Check configuration");
        }

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();

        while (!isStopRequested()) {
            drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y,
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x
                    )
            );

            drive.update();

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("Drive pose estimate", "x: %.1f, y: %.1f, heading: %.1f",
                    poseEstimate.getX(), poseEstimate.getY(), poseEstimate.getHeading());

            if (DriveConstants.RUN_USING_ENCODER) {
                telemetry.addData("Motors", "LF: %d, LR: %d, RF: %d, RR: %d",
                        leftFrontMotor.getCurrentPosition(),
                        leftRearMotor.getCurrentPosition(),
                        rightFrontMotor.getCurrentPosition(),
                        rightRearMotor.getCurrentPosition());
            }

            if (SampleMecanumDrive.use2WheelTrackingLocalizer) {
                telemetry.addData("Encoders", "Parallel: %d, Perpendicular: %d",
                        parallelEncoder.getCurrentPosition(), perpendicularEncoder.getCurrentPosition());
                telemetry.addData("Expected pose", "x: %.1f, y: %.1f",
                        TwoWheelTrackingLocalizer.encoderTicksToInches(
                                parallelEncoder.getCurrentPosition() - startParallelPosition) * TwoWheelTrackingLocalizer.X_MULTIPLIER,
                        TwoWheelTrackingLocalizer.encoderTicksToInches(
                                perpendicularEncoder.getCurrentPosition() - startPerpendicularPosition) * TwoWheelTrackingLocalizer.Y_MULTIPLIER);
            } else if (SampleMecanumDrive.use3WheelTrackingLocalizer) {
                telemetry.addData("Encoders", "Left: %d, Front: %d, Right: %d",
                        leftEncoder.getCurrentPosition(), frontEncoder.getCurrentPosition(),
                        rightEncoder.getCurrentPosition());
                telemetry.addData("Expected pose", "x1: %.1f, x2: %.1f, y: %.1f",
                        TwoWheelTrackingLocalizer.encoderTicksToInches(
                                leftEncoder.getCurrentPosition() - startLeftPosition) * StandardTrackingWheelLocalizer.X_MULTIPLIER,
                        TwoWheelTrackingLocalizer.encoderTicksToInches(
                                rightEncoder.getCurrentPosition() - startRightPosition) * StandardTrackingWheelLocalizer.X_MULTIPLIER,
                        TwoWheelTrackingLocalizer.encoderTicksToInches(
                                frontEncoder.getCurrentPosition() - startFrontPosition) * StandardTrackingWheelLocalizer.Y_MULTIPLIER);
            }

            telemetry.update();
        }
    }
}

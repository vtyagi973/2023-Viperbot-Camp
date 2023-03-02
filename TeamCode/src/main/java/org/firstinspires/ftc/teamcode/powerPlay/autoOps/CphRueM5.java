package org.firstinspires.ftc.teamcode.powerPlay.autoOps;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.ParkingPositionEnum;
import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.TeamPositionEnum;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadRunner.trajectorysequence.TrajectorySequence;

public class CphRueM5 {
    final double TILE_WIDTH = 24;
    final double TILE_HEIGHT = 24;
    final double ROBOT_WIDTH = 13;
    final double ROBOT_HEIGHT = 13;
    final double EPSILON = 1e-3;

    LinearOpMode autoOpMode;
    FalconBot robot;
    SampleMecanumDrive drive;
    TeamPositionEnum teamPosition;
    ParkingPositionEnum parkingPosition;

    double x, y;
    Vector2d vector1;
    Pose2d startPose, pose1, pose2;
    TrajectorySequence ts1, ts2, tsEnd;

    public CphRueM5(LinearOpMode autoOpMode, FalconBot robot,
                    SampleMecanumDrive roadRunnerDrive,
                    TeamPositionEnum teamPosition, ParkingPositionEnum parkingPosition) {
        this.autoOpMode = autoOpMode;
        this.robot = robot;
        this.drive = roadRunnerDrive;
        this.teamPosition = teamPosition;
        this.parkingPosition = parkingPosition;
    }

    public CphRueM5 init() {
        // Starting position
        startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        return this;
    }

    public CphRueM5 execute() {
        FalconLogger.enter();
        Boolean deliverPreLoadedCone = true, deliverFifthCone = true, deliverFourthCone = true, parkRobot = true;

        if (deliverPreLoadedCone) {
            pose1 = new Pose2d(lRValue(48, 49.5), lRValue(3.5, 0.50),
                    Math.toRadians(lRValue(-90, 90)));
            ts1 = drive.trajectorySequenceBuilder(startPose)
                    .addTemporalMarker(() -> {
                        robot.falconLift.moveLift(FalconLift.LIFT_POSITION_MEDIUM_JUNCTION, false);
                    })
                    .lineToLinearHeading(pose1)
                    .build();

            drive.followTrajectorySequence(ts1);
            tsEnd = ts1;
            robot.falconHand.open(true, true);
            if (!autoOpMode.opModeIsActive()) return this;
        }

        if (deliverFifthCone) {
            // Goto cone stack
            vector1 = new Vector2d(61, lRValue(5, 0));
            pose1 = new Pose2d(lRValue(65, 60.5), lRValue(30, -26.5),
                    Math.toRadians(lRValue(90.0, -90.0)));
            ts1 = drive.trajectorySequenceBuilder(tsEnd.end())
                    .lineToConstantHeading(vector1)
                    .lineToLinearHeading(pose1)
                    .addTemporalMarker(2, () -> robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FIFTH_CONE, false))
                    .build();

            drive.followTrajectorySequence(ts1);

            tsEnd = ts1;
            //tsEnd =  alignWithConeStack(ts1);
            robot.falconHand.close(true);
            robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FIFTH_CONE + FalconLift.CONE_HEIGHT, true);

            // Goto junction
            pose2 = new Pose2d(pose1.getX() - 4.55, lRValue(-11, 18),
                    Math.toRadians(lRValue(180, -180)));
            ts2 = drive.trajectorySequenceBuilder(tsEnd.end())
                    .addTemporalMarker(0.1, () -> robot.falconLift.moveLift(FalconLift.LIFT_POSITION_MEDIUM_JUNCTION, false))
                    .lineToLinearHeading(pose2)
                    .build();

            drive.followTrajectorySequence(ts2);
            tsEnd = ts2;

            robot.falconHand.open(true, true);
            if (!autoOpMode.opModeIsActive()) return this;
        }

        if (deliverFourthCone) {
            // Goto cone stack
            pose1 = new Pose2d(lRValue(57, 53), lRValue(28.0, -20.0),
                    Math.toRadians(lRValue(90.0, -90.0)));
            ts1 = drive.trajectorySequenceBuilder(tsEnd.end())
                    .lineToLinearHeading(pose1)
                    .addTemporalMarker(0.5, () -> robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FOURTH_CONE, false))
                    .build();

            drive.followTrajectorySequence(ts1);
            tsEnd = ts1;
            robot.falconHand.close(true);
            robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FOURTH_CONE + FalconLift.CONE_HEIGHT, true);

            // Goto junction
            pose2 = new Pose2d(pose1.getX() - 4.55, lRValue(-11, 21),
                    Math.toRadians(lRValue(180, -180)));
            ts2 = drive.trajectorySequenceBuilder(tsEnd.end())
                    .lineToLinearHeading(pose2)
                    .addTemporalMarker(0.1, () -> robot.falconLift.moveLift(FalconLift.LIFT_POSITION_MEDIUM_JUNCTION, false))
                    .build();

            drive.followTrajectorySequence(ts2);
            tsEnd = ts2;
            robot.falconHand.open(true, true);
            if (!autoOpMode.opModeIsActive()) return this;
        }

        if (parkRobot) {
            x = tsEnd.end().getX() - 2.0;
            y = tsEnd.end().getY();
            // Robot is facing down
            if (teamPosition == TeamPositionEnum.RIGHT) {
                if (parkingPosition == ParkingPositionEnum.ONE) {
                    y += 14;
                } else if (parkingPosition == ParkingPositionEnum.TWO) {
                    y -= 12;
                } else {
                    y -= TILE_WIDTH * 1.5;
                }
            } else {
                if (parkingPosition == ParkingPositionEnum.ONE) {
                    y += 40;
                } else if (parkingPosition == ParkingPositionEnum.TWO) {
                    y += TILE_WIDTH * 0.5;
                } else {
                    x -= 5.0;
                    y -= 15;
                }
            }

            // Don't lower the lift during parking movement
            // as it tends to hit the middle junction.
            pose1 = new Pose2d(x, y, 0);
            ts1 = drive.trajectorySequenceBuilder(tsEnd.end())
                    .lineToLinearHeading(pose1)
                    .build();
            drive.followTrajectorySequence(ts1);
        }

        // Servos lose power at the end of autoOp and the undelivered cone will fall.
        // Lower the lift to sub station level.
        robot.falconLift.moveLift(FalconLift.LIFT_POSITION_SUB_STATION, false);
        FalconLogger.exit();
        return this;
    }

    private double lRValue(double leftValue, double rightValue) {
        if (teamPosition == TeamPositionEnum.LEFT) return leftValue;
        else return rightValue;
    }
}
package org.firstinspires.ftc.teamcode.powerPlay.autoOps;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.ParkingPositionEnum;
import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.TeamPositionEnum;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

public class CphTwoH5 {
    LinearOpMode autoOpMode;
    FalconBot robot;
    SampleMecanumDrive drive;
    TeamPositionEnum teamPosition;
    ParkingPositionEnum parkingPosition;
    int teamPositionInt;

    double x, y;
    Pose2d startPose, pose1, pose2;
    Trajectory t1, tEnd;

    public CphTwoH5(LinearOpMode autoOpMode, FalconBot robot,
                    SampleMecanumDrive roadRunnerDrive,
                    TeamPositionEnum teamPosition, ParkingPositionEnum parkingPosition) {
        this.autoOpMode = autoOpMode;
        this.robot = robot;
        this.drive = roadRunnerDrive;
        this.teamPosition = teamPosition;
        this.parkingPosition = parkingPosition;
    }

    public CphTwoH5 init() {
        // All trajectories developed using RIGHT location
        teamPositionInt = (teamPosition == TeamPositionEnum.RIGHT) ? 1 : -1;

        // Starting position
        startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        return this;
    }

    public CphTwoH5 execute() {
        FalconLogger.enter();
        Boolean deliverPreLoadedCone = true, deliverFifthCone = true, deliverFourthCone = true, parkRobot = true;

        if (deliverPreLoadedCone) {
            pose1 = new Pose2d(lRValue(53, 53), lRValue(-5.0, 4),
                    Math.toRadians(35 * teamPositionInt));
            tEnd = drive.trajectoryBuilder(startPose)
                    .addTemporalMarker(0.10, () -> {
                        robot.falconLift.moveLift(FalconLift.LIFT_POSITION_HIGH_JUNCTION, false);
                    })
                    .splineToLinearHeading(pose1, pose1.getHeading())
                    .build();

            drive.followTrajectory(tEnd);
            FalconUtils.sleep(500); // wait for lift to stop swinging
            robot.falconHand.open(true, true);
            if (!autoOpMode.opModeIsActive()) return this;
        }

        if (deliverFifthCone) {
            // Goto cone stack
            pose1 = new Pose2d(lRValue(49.5, 50.5), lRValue(23.0, -22),
                    Math.toRadians(-90 * teamPositionInt));
            t1 = drive.trajectoryBuilder(tEnd.end(), true)
                    .splineToLinearHeading(pose1, pose1.getHeading())
                    .addTemporalMarker(1, () -> robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FIFTH_CONE, false))
                    .build();

            drive.followTrajectory(t1);
            robot.falconHand.close(true);
            robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FIFTH_CONE + FalconLift.CONE_HEIGHT, true);

            // Goto junction
            pose2 = new Pose2d(53, lRValue(-8.5, 6),
                    Math.toRadians(35 * teamPositionInt));
            tEnd = drive.trajectoryBuilder(t1.end(), true)
                    .addTemporalMarker(0.10, () -> {
                        robot.falconLift.moveLift(FalconLift.LIFT_POSITION_HIGH_JUNCTION, false);
                    })
                    .splineToLinearHeading(pose2, pose2.getHeading())
                    .build();

            drive.followTrajectory(tEnd);
            FalconUtils.sleep(500); // wait for lift to stop swinging
            robot.falconHand.open(true, true);
            if (!autoOpMode.opModeIsActive()) return this;
        }

        if (deliverFourthCone) {
            // Goto cone stack
            pose1 = new Pose2d(lRValue(51.5, 51), lRValue(24, -23),
                    Math.toRadians(-90 * teamPositionInt));
            t1 = drive.trajectoryBuilder(tEnd.end(), true)
                    .splineToLinearHeading(pose1, pose1.getHeading())
                    .addTemporalMarker(1, () -> robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FOURTH_CONE, false))
                    .build();

            drive.followTrajectory(t1);
            robot.falconHand.close(true);
            robot.falconLift.moveLift(FalconLift.LIFT_POSITION_FOURTH_CONE + FalconLift.CONE_HEIGHT, true);

            // Goto junction
            pose2 = new Pose2d(55, lRValue(-8, 6),
                    Math.toRadians(35 * teamPositionInt));
            tEnd = drive.trajectoryBuilder(t1.end(), true)
                    .addTemporalMarker(0.10, () -> {
                        robot.falconLift.moveLift(FalconLift.LIFT_POSITION_HIGH_JUNCTION, false);
                    })
                    .splineToLinearHeading(pose2, pose2.getHeading())
                    .build();

            drive.followTrajectory(tEnd);
            FalconUtils.sleep(500); // wait for lift to stop swinging
            robot.falconHand.open(true, true);
            if (!autoOpMode.opModeIsActive()) return this;
        }

        if (parkRobot) {
            x = 50;
            // Robot is facing 35 deg towards the junction
            if (teamPosition == TeamPositionEnum.RIGHT) {
                if (parkingPosition == ParkingPositionEnum.ONE) {
                    y = 27;
                } else if (parkingPosition == ParkingPositionEnum.TWO) {
                    y = 0;
                } else {
                    x += 1.0; // Ensure robot doesn't strike low junction
                    y = -25;
                }
            } else {
                if (parkingPosition == ParkingPositionEnum.ONE) {
                    x += 1.0; // Ensure robot doesn't strike low junction
                    y = 27;
                } else if (parkingPosition == ParkingPositionEnum.TWO) {
                    y = 2;
                } else {
                    y = -25;
                }
            }

            // Don't lower the lift during parking movement
            // as it tends to hit the junction.
            pose1 = new Pose2d(x, y, 0);
            t1 = drive.trajectoryBuilder(tEnd.end())
                    .lineToLinearHeading(pose1)
                    .build();
            drive.followTrajectory(t1);
        }

        // Servos lose power at the end of autoOp and any undelivered cone will fall.
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
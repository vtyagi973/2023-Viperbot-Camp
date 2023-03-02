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

public class CphRueM1 {
    final double TILE_WIDTH = 24;
    final double TILE_HEIGHT = 24;

    LinearOpMode autoOpMode;
    FalconBot robot;
    SampleMecanumDrive drive;
    TeamPositionEnum teamPosition;
    ParkingPositionEnum parkingPosition;

    int teamPositionInt;
    Vector2d vector1, vector2, vectorP;
    double x, y, endTangent;
    Pose2d startPose;
    TrajectorySequence ts1, ts2, tsP;

    public CphRueM1(LinearOpMode autoOpMode, FalconBot robot,
                    SampleMecanumDrive roadRunnerDrive,
                    TeamPositionEnum teamPosition, ParkingPositionEnum parkingPosition) {
        this.autoOpMode = autoOpMode;
        this.robot = robot;
        this.drive = roadRunnerDrive;
        this.teamPosition = teamPosition;
        this.parkingPosition = parkingPosition;
    }

    public CphRueM1 init() {
        // All trajectories developed using RIGHT location
        teamPositionInt = (teamPosition == TeamPositionEnum.RIGHT) ? 1 : -1;

        // Starting position
        startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        return this;
    }

    public CphRueM1 execute() {
        FalconLogger.enter();

        vector1 = new Vector2d(startPose.getX() + TILE_HEIGHT, startPose.getY());
        vector2 = new Vector2d(vector1.getX() + lRValue(6, 6),
                lRValue(-16, 17.6));
        ts1 = drive.trajectorySequenceBuilder(startPose)
                .addTemporalMarker(0, () -> {
                    robot.falconLift.moveLift(FalconLift.LIFT_POSITION_MEDIUM_JUNCTION, false);
                })
                .splineToConstantHeading(vector1, startPose.getHeading())
                .splineToConstantHeading(vector2, Math.toRadians(45 * teamPositionInt))
                .build();

        drive.followTrajectorySequence(ts1);
        robot.falconHand.open(true, true);
        if (!autoOpMode.opModeIsActive()) return this;

        // Parking
        x = ts1.end().getX();
        y = ts1.end().getY();
        if (teamPosition == TeamPositionEnum.RIGHT) {
            if (parkingPosition == ParkingPositionEnum.ONE) {
                y += 13.2;
                endTangent = Math.toRadians(90);
            } else if (parkingPosition == ParkingPositionEnum.TWO) {
                y -= TILE_WIDTH * 0.5;
                endTangent = Math.toRadians(-90);
            } else {
                y -= TILE_WIDTH * 1.5;
                endTangent = Math.toRadians(-90);
            }
        } else {
            if (parkingPosition == ParkingPositionEnum.ONE) {
                y += 39.6;
                endTangent = Math.toRadians(90);
            } else if (parkingPosition == ParkingPositionEnum.TWO) {
                y += 13.2;
                endTangent = Math.toRadians(90);
            } else {
                y -= 15;
                endTangent = Math.toRadians(-90);
            }
        }

        vectorP = new Vector2d(x, y);
        tsP = drive.trajectorySequenceBuilder(ts2.end())
                .lineToConstantHeading(vectorP)
                .build();

        drive.followTrajectorySequence(tsP);

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
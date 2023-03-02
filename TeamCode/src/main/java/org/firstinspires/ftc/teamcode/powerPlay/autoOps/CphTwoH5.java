package org.firstinspires.ftc.teamcode.powerPlay.autoOps;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

public class CphTwoH5 {
    LinearOpMode autoOpMode;
    FalconBot robot;
    SampleMecanumDrive drive;
    int teamPositionInt;

    double x, y;
    Pose2d startPose, pose1, pose2;
    Trajectory t1, tEnd;

    public CphTwoH5(LinearOpMode autoOpMode, FalconBot robot,
                    SampleMecanumDrive roadRunnerDrive) {
        this.autoOpMode = autoOpMode;
        this.robot = robot;
        this.drive = roadRunnerDrive;
    }

    public CphTwoH5 init() {
        // All trajectories developed using RIGHT location

        // Starting position
        startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        return this;
    }

    public CphTwoH5 execute() {
        FalconLogger.enter();
        Boolean parkRobot = true;

        if (parkRobot) {
            x = 50;
            // Robot is facing 35 deg towards the junction
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
}
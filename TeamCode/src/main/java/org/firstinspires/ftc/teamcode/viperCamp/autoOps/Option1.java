package org.firstinspires.ftc.teamcode.viperCamp.autoOps;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.viperCamp.core.ViperBot;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLift;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLogger;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

public class Option1 {
    LinearOpMode autoOpMode;
    ViperBot robot;
    SampleMecanumDrive drive;

    double x, y;
    Pose2d startPose, pose1, pose2;
    Trajectory t1, tEnd;

    public Option1(LinearOpMode autoOpMode, ViperBot robot,
                   SampleMecanumDrive roadRunnerDrive) {
        this.autoOpMode = autoOpMode;
        this.robot = robot;
        this.drive = roadRunnerDrive;
    }

    public Option1 init() {
        // All trajectories developed using RIGHT location

        // Starting position
        startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        return this;
    }

    public Option1 execute() {
        ViperLogger.enter();
        Boolean parkRobot = false;

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

        ViperLogger.exit();
        return this;
    }
}
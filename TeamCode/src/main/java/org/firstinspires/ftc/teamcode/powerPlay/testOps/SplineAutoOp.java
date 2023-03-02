package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

@Disabled
@Autonomous(group = "TestOp")
public class SplineAutoOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Pose2d startPose = new Pose2d(0, 0, 0);
        drive.setPoseEstimate(startPose);
        waitForStart();

        if (isStopRequested()) return;

        Trajectory t = drive.trajectoryBuilder(startPose)
                .splineToConstantHeading(new Vector2d(48, 0), 0)
                .build();

        drive.followTrajectory(t);

        sleep(100);
        Pose2d pose1 = new Pose2d(0, 0, Math.toRadians(180));
        drive.followTrajectory(
                drive.trajectoryBuilder(t.end(), true)
                        .splineToLinearHeading(pose1, Math.toRadians(180))
                        //.splineToConstantHeading(new Vector2d(0, 0), Math.toRadians(180))
                        .build()
        );
    }
}

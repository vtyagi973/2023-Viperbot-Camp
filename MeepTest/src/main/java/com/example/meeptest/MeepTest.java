package com.example.meeptest;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import com.noahbres.meepmeep.roadrunner.Constraints;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequence;

public class MeepTest {
    public static void main(String[] args) {
        Constraints constraints = new Constraints(60, 60, Math.toRadians(180), Math.toRadians(180), 11.50);

        MeepMeep meepMeep = new MeepMeep(800)
                .setBackground(MeepMeep.Background.FIELD_POWERPLAY_KAI_LIGHT)
                .setTheme(new ColorSchemeRedLight())
                .setBackgroundAlpha(0.9f)
                .setDarkMode(true);

        Pose2d startPose = new Pose2d(36, -62, Math.toRadians(90));
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(constraints)
                .setDriveTrainType(DriveTrainType.MECANUM)
                .setDimensions(13, 17)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                                .splineToLinearHeading(new Pose2d(30, -6, Math.toRadians(135)), Math.toRadians(135))
                                .lineToLinearHeading(new Pose2d(60, -12, Math.toRadians(0)))
                                .splineToLinearHeading(new Pose2d(30, -6, Math.toRadians(135)), Math.toRadians(135))
                                .lineToLinearHeading(new Pose2d(12, -12, Math.toRadians(90)))
                                .build()
                );

        meepMeep.addEntity(myBot)
                .start();
    }
}

/*
Square
Pose2d startPose = new Pose2d(36, -62, Math.toRadians(90));
drive.trajectorySequenceBuilder(startPose)
    .forward(30)
    .turn(Math.toRadians(90))
    .forward(30)
    .turn(Math.toRadians(90))
    .forward(30)
    .turn(Math.toRadians(90))
    .forward(30)
    .turn(Math.toRadians(90))
    .build()
 */
package org.firstinspires.ftc.teamcode.viperCamp.autoOps;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.viperCamp.core.ViperBot;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperGyro;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLift;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLogger;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperUtils;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

public class AutoOpMain {
    private static final String TAG = "AutoOp";
    ViperBot robot = null;
    int handlerId = 3;

    public void execute(LinearOpMode autoOpMode) {
        ViperLogger.enter();
        autoOpMode.telemetry.addData("Status", "Initializing. Please wait...");
        autoOpMode.telemetry.update();

        // Initialize robot and disable telemetry to speed things up.
        robot = new ViperBot();
        robot.init(autoOpMode.hardwareMap, autoOpMode.telemetry, true);
        robot.disableTelemetry();

        // Initialize roadrunner for robot paths and trajectories
        SampleMecanumDrive roadRunnerDrive = new SampleMecanumDrive(autoOpMode.hardwareMap);

        do {
            // Evaluate parking position at least once
            autoOpMode.telemetry.addData("Status", "Waiting for driver to press start.");
            autoOpMode.telemetry.update();
        } while (autoOpMode.opModeInInit());

        if (handlerId == 1) {
        } else if (handlerId == 2) {
        } else if (handlerId == 3) {
            new Option1(autoOpMode, robot, roadRunnerDrive)
                    .init().execute();
        } else if (handlerId == 4) {
        }

        do {
            autoOpMode.telemetry.addData("Status", "Waiting for auto Op to end.");
            autoOpMode.telemetry.update();
            ViperUtils.sleep(50);
        } while (autoOpMode.opModeIsActive());

        ViperLogger.exit();
    }
}

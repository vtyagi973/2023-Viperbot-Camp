package org.firstinspires.ftc.teamcode.powerPlay.autoOps;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconGyro;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

public class AutoOpMain {
    private static final String TAG = "AutoOp";
    FalconBot robot = null;
    int handlerId = 3;

    public void execute(LinearOpMode autoOpMode) {
        FalconLogger.enter();
        autoOpMode.telemetry.addData("Status", "Initializing. Please wait...");
        autoOpMode.telemetry.update();

        // Initialize robot and disable telemetry to speed things up.
        robot = new FalconBot();
        robot.init(autoOpMode.hardwareMap, autoOpMode.telemetry, true);
        robot.disableTelemetry();
        FalconGyro.endAutoOpHeading = 0;
        FalconLift.endAutoOpLiftPosition = FalconLift.LIFT_POSITION_ABSOLUTE_MINIMUM;
        //robot.falconHand.close(false);
        robot.falconHand.open(true, false);

        // Initialize roadrunner for robot paths and trajectories
        SampleMecanumDrive roadRunnerDrive = new SampleMecanumDrive(autoOpMode.hardwareMap);

        do {
            // Evaluate parking position at least once
            autoOpMode.telemetry.addData("Status", "Waiting for driver to press start.");
            autoOpMode.telemetry.update();
        } while (autoOpMode.opModeInInit());

        robot.falconHand.close(true);

        // Moving lift during init violates the robot size constraint.
        // Must create trajectories AFTER parking position has been determined.
        // Create all trajectories up front for smoother robot motion.
        if (handlerId == 1) {
        } else if (handlerId == 2) {
        } else if (handlerId == 3) {
            new CphTwoH5(autoOpMode, robot, roadRunnerDrive)
                    .init().execute();
        } else if (handlerId == 4) {
        }

        do {
            // Save settings for use by TeleOp
            robot.gyro.read();
            FalconGyro.endAutoOpHeading = FalconGyro.Heading;
            FalconLift.endAutoOpLiftPosition = robot.falconLift.getPosition();
            autoOpMode.telemetry.addData("Settings", "endLiftPosition=%d, endGyroHeading=%.1f",
                    FalconLift.endAutoOpLiftPosition, FalconGyro.endAutoOpHeading);
            autoOpMode.telemetry.addData("Status", "Parking complete. Waiting for auto Op to end.");
            autoOpMode.telemetry.update();
            FalconUtils.sleep(50);
        } while (autoOpMode.opModeIsActive());

        FalconLogger.exit();
    }
}

package org.firstinspires.ftc.teamcode.powerPlay.autoOps;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.ParkingPositionEnum;
import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.TeamPositionEnum;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconGyro;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.roadRunner.drive.SampleMecanumDrive;

public class AutoOpMain {
    private static final String TAG = "AutoOp";
    FalconBot robot = null;
    ParkingPositionEnum parkingPosition;
    int handlerId = 3;

    public void execute(LinearOpMode autoOpMode, TeamPositionEnum teamPosition) {
        FalconLogger.enter();
        autoOpMode.telemetry.addData("Status", getAllianceInfo(teamPosition));
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

        // In case camera fails, a default signal sleeve position of 2
        // gives us a 33% chance of getting it right.
        parkingPosition = ParkingPositionEnum.TWO;

        // Process images while waiting for driver to hit start
        do {
            // Evaluate parking position at least once
            autoOpMode.telemetry.addData("Status", getAllianceInfo(teamPosition));
            ParkingPositionEnum tempParkingPosition = robot.falconOpenCvCam.getParkingPosition();
            if (tempParkingPosition != ParkingPositionEnum.UNKNOWN) {
                parkingPosition = tempParkingPosition;
            }

            robot.falconOpenCvCam.showCameraFailure();
            autoOpMode.telemetry.addData("Signal", "%s", parkingPosition);
            autoOpMode.telemetry.addData("Status", "Waiting for driver to press start.");
            autoOpMode.telemetry.update();
        } while (autoOpMode.opModeInInit());

        robot.falconHand.close(true);

        // Moving lift during init violates the robot size constraint.
        // Must create trajectories AFTER parking position has been determined.
        // Create all trajectories up front for smoother robot motion.
        if (handlerId == 1) {
            new CphRueM1(autoOpMode, robot, roadRunnerDrive, teamPosition, parkingPosition)
                    .init().execute();
        } else if (handlerId == 2) {
            new CphRueM5(autoOpMode, robot, roadRunnerDrive, teamPosition, parkingPosition)
                    .init().execute();
        } else if (handlerId == 3) {
            new CphTwoH5(autoOpMode, robot, roadRunnerDrive, teamPosition, parkingPosition)
                    .init().execute();
        } else if (handlerId == 4) {
            new CphTwoM1L4(autoOpMode, robot, roadRunnerDrive, teamPosition, parkingPosition)
                    .init().execute();
        }

        do {
            // Save settings for use by TeleOp
            robot.gyro.read();
            FalconGyro.endAutoOpHeading = FalconGyro.Heading;
            FalconLift.endAutoOpLiftPosition = robot.falconLift.getPosition();
            autoOpMode.telemetry.addData("Status", getAllianceInfo(teamPosition));
            autoOpMode.telemetry.addData("Settings", "endLiftPosition=%d, endGyroHeading=%.1f",
                    FalconLift.endAutoOpLiftPosition, FalconGyro.endAutoOpHeading);
            autoOpMode.telemetry.addData("Status", "Parking complete. Waiting for auto Op to end.");
            autoOpMode.telemetry.update();
            FalconUtils.sleep(50);
        } while (autoOpMode.opModeIsActive());

        FalconLogger.exit();
    }

    private String getAllianceInfo(TeamPositionEnum teamPosition) {
        return String.format("%s position", teamPosition);
    }
}

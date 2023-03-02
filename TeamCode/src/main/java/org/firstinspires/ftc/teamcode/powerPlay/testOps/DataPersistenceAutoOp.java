package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconGyro;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconSettings;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@Disabled
@Autonomous(group = "TestOp", preselectTeleOp = "DataPersistenceTeleOp")
public class DataPersistenceAutoOp extends LinearOpMode {
    FalconSettings settings = null;

    @Override
    public void runOpMode() {
        FalconLogger.enter();
        telemetry.addData("Status", "Initializing. Please wait...");
        telemetry.update();

        // Wait for driver to hit start
        while (opModeInInit()) {
            telemetry.addData("Status", "Initialization complete. Waiting for start");
            telemetry.update();
            FalconUtils.sleep(10);
        }

        telemetry.addData("Status", "Updating settings. Please wait...");
        telemetry.update();
        FalconLift.endAutoOpLiftPosition = 43;
        FalconGyro.endAutoOpHeading = 47;
        telemetry.addData("settings", "endLiftAutoOpPosition=%d",
                FalconLift.endAutoOpLiftPosition);
        telemetry.addData("settings", "endAutoOpHeading=%.1f",
                FalconGyro.endAutoOpHeading);
        telemetry.addData("settings", "Update successful");
        telemetry.update();
        while (opModeIsActive()) {
            FalconUtils.sleep(500);
        }

        FalconLogger.exit();
    }
}

package org.firstinspires.ftc.teamcode.powerPlay.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@TeleOp(group = "PowerPlay")
public class DriverTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    FalconBot robot = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        FalconLogger.enter();
        telemetry.addData("Status", "INITIALIZING. Please wait...");
        telemetry.update();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        robot = new FalconBot();
        robot.init(hardwareMap, telemetry, false);
        robot.driveTrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        telemetry.addData("Status", "READY. Waiting for driver to press start");
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        FalconUtils.sleep(10);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        FalconLogger.enter();
        if (BuildConfig.DEBUG) {
            robot.enableTelemetry();
        } else {
            robot.disableTelemetry();
        }

        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        loopTime.reset();
        robot.gyro.read();
        robot.bulkRead.clearBulkCache();
        robot.operateRobot(gamepad1, gamepad2, loopTime);

        // Show the elapsed game time.
        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        FalconLogger.enter();
        if (robot != null) {
            robot.stopEverything();
        }

        FalconLogger.exit();
    }
}

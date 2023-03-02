package org.firstinspires.ftc.teamcode.viperCamp.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.viperCamp.core.ViperBot;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLogger;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperUtils;

@Disabled
@TeleOp(group = "TestOp")
public class FodTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    ViperBot robot = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        ViperLogger.enter();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        robot = new ViperBot();
        robot.init(hardwareMap, telemetry, false);
        robot.driveTrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Inform the driver that initialization is complete.
        telemetry.addData("Usage", "Use gamepad1 for FOD");
        robot.showGamePadTelemetry(gamepad1);
        telemetry.update();
        ViperLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData("Driver Op", "Ready");
        telemetry.update();
        ViperUtils.sleep(10);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        ViperLogger.enter();
        robot.enableTelemetry();
        ViperLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        ViperLogger.enter();
        // Show the elapsed game time and wheel power.
        loopTime.reset();

        robot.gyro.read();
        robot.bulkRead.clearBulkCache();
        robot.driveTrain.fieldOrientedDrive(gamepad1, gamepad2, loopTime);
        robot.driveTrain.showTelemetry();
        robot.showGamePadTelemetry(gamepad1);
        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
        ViperLogger.exit();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        ViperLogger.enter();
        robot.stopEverything();
        ViperLogger.exit();
    }
}

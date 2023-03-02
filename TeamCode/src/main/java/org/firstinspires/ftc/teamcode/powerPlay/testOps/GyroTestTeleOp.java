package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconBot;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;

@Disabled
@TeleOp(group = "TestOp")
public class GyroTestTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    FalconBot robot = null;
    double targetHeading = 0;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        FalconLogger.enter();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        robot = new FalconBot();
        robot.init(hardwareMap, telemetry, false);
        robot.driveTrain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Inform the driver that initialization is complete.
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        FalconLogger.enter();
        telemetry.addData("Driver Op", "Ready");
        telemetry.update();
        robot.enableTelemetry();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        // Show the elapsed game time and wheel power.
        loopTime.reset();

        robot.gyro.read();
        robot.gyro.showTelemetry();
        robot.bulkRead.clearBulkCache();
        if (gamepad1.y)
            targetHeading = 0;
        else if (gamepad1.b)
            targetHeading = -90;
        else if (gamepad1.x)
            targetHeading = 90;
        else if (gamepad1.a)
            targetHeading = -180;
        telemetry.addData("Gyro", "Target %.1f, Heading %.1f, Offset %.1f",
                targetHeading, robot.gyro.Heading, robot.gyro.getHeadingOffset(targetHeading));
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
        robot.stopEverything();
        FalconLogger.exit();
    }
}

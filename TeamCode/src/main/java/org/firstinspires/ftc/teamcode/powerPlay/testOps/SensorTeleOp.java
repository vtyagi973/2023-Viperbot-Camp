package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconSensors;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@Disabled
@TeleOp(group = "TestOp")
public class SensorTeleOp extends OpMode {
    private static final String TAG = "Sensors";

    // Declare OpMode members
    FalconSensors sensors = null;
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        FalconLogger.enter();
        telemetry.addData(">", "Initializing, please wait...");
        telemetry.update();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData(">", "Initialization complete, Waiting for start.");
        telemetry.update();
        FalconUtils.sleep(100);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        sensors.showTelemetry = true;
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        // Show the elapsed game time and wheel power.
        loopTime.reset();
        telemetry.addData(">", "Move junction in front of the robot");
        sensors.showTelemetry();
        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        FalconLogger.enter();
        telemetry.addData(">", "Stopping Driver Op");
        telemetry.update();
        FalconLogger.exit();
    }
}

package org.firstinspires.ftc.teamcode.viperCamp.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.viperCamp.core.ViperHand;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLogger;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperUtils;

@Disabled
@TeleOp(group = "TestOp")
public class HandTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;

    ViperHand viperHand;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        ViperLogger.enter();
        telemetry.addData(">", "Initializing, please wait...");
        telemetry.update();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        viperHand = new ViperHand();
        viperHand.init(hardwareMap, telemetry, null);
        ViperLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData(">", "Initialization complete, Waiting for start.");
        telemetry.update();
        ViperUtils.sleep(250);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        ViperLogger.enter();
        telemetry.addData(">", "Starting Driver Op");
        telemetry.update();
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
        telemetry.addData(">", "Use left stick to small adjust servo position");
        if (gamepad1.right_trigger >= 0.5 || gamepad2.right_trigger >= 0.5) {
            viperHand.close(true);
        } else if (gamepad1.right_bumper || gamepad2.right_bumper) {
            viperHand.open(false, true);
        }

        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        ViperLogger.enter();
        telemetry.addData(">", "Stopping Driver Op");
        telemetry.update();
        ViperLogger.exit();
    }
}

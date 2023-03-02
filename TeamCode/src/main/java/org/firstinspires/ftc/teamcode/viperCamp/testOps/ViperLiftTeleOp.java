package org.firstinspires.ftc.teamcode.viperCamp.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLift;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperLogger;
import org.firstinspires.ftc.teamcode.viperCamp.core.ViperUtils;

@Disabled
@TeleOp(group = "TestOp")
public class ViperLiftTeleOp extends OpMode {
    private static final String TAG = "Lift";
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    public ViperLift viperLift = null;

    // Start point for the lift
    int currentPosition = ViperLift.LIFT_POSITION_SUB_STATION;
    int targetLiftPosition;

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
        viperLift = new ViperLift();
        viperLift.init(hardwareMap, telemetry);
        ViperLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData(">", "Initialization complete, Waiting for start.");
        telemetry.update();
        ViperUtils.sleep(100);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        ViperLogger.enter();
        // Show the elapsed game time and wheel power.
        loopTime.reset();
        telemetry.addData(">", "Use gamepad to move lift");
        targetLiftPosition = ViperLift.LIFT_POSITION_INVALID;

        if (gamepad1.a || gamepad2.a) {
            targetLiftPosition = ViperLift.LIFT_POSITION_SUB_STATION;
        } else if (gamepad1.x || gamepad2.x) {
            targetLiftPosition = ViperLift.LIFT_POSITION_LOW_JUNCTION;
        } else if (gamepad1.b || gamepad2.b) {
            targetLiftPosition = ViperLift.LIFT_POSITION_MEDIUM_JUNCTION;
        } else if (gamepad1.y || gamepad2.y) {
            targetLiftPosition = ViperLift.LIFT_POSITION_HIGH_JUNCTION;
        }

        if (targetLiftPosition != ViperLift.LIFT_POSITION_INVALID) {
            viperLift.moveLift(targetLiftPosition, true);
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

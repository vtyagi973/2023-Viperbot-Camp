package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@Disabled
@TeleOp(group = "TestOp")
public class FalconLiftTeleOp extends OpMode {
    private static final String TAG = "Lift";
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    public FalconLift falconLift = null;

    // Start point for the lift
    int currentPosition = FalconLift.LIFT_POSITION_SUB_STATION;
    int targetLiftPosition;

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
        falconLift = new FalconLift();
        falconLift.init(hardwareMap, telemetry);
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
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        // Show the elapsed game time and wheel power.
        loopTime.reset();
        telemetry.addData(">", "Use gamepad to move lift");
        targetLiftPosition = FalconLift.LIFT_POSITION_INVALID;

        if (gamepad1.a || gamepad2.a) {
            targetLiftPosition = FalconLift.LIFT_POSITION_SUB_STATION;
        } else if (gamepad1.x || gamepad2.x) {
            targetLiftPosition = FalconLift.LIFT_POSITION_LOW_JUNCTION;
        } else if (gamepad1.b || gamepad2.b) {
            targetLiftPosition = FalconLift.LIFT_POSITION_MEDIUM_JUNCTION;
        } else if (gamepad1.y || gamepad2.y) {
            targetLiftPosition = FalconLift.LIFT_POSITION_HIGH_JUNCTION;
        }

        if (targetLiftPosition != FalconLift.LIFT_POSITION_INVALID) {
            falconLift.moveLift(targetLiftPosition, true);
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
        FalconLogger.enter();
        telemetry.addData(">", "Stopping Driver Op");
        telemetry.update();
        FalconLogger.exit();
    }
}

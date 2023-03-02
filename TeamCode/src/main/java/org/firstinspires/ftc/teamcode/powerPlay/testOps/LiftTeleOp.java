package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLift;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

import java.util.Locale;

@Disabled
@TeleOp(group = "TestOp")
public class LiftTeleOp extends OpMode {
    private static final String TAG = "Lift";
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    public DcMotor liftMotor = null;

    // Start point for the lift
    int currentPosition = FalconLift.LIFT_POSITION_SUB_STATION;
    int targetPosition = FalconLift.LIFT_POSITION_SUB_STATION;

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
        liftMotor = hardwareMap.get(DcMotor.class, "liftMotor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
        telemetry.addData(">", "Use DPad up/down to move lift");

        // Lift operation
        currentPosition = liftMotor.getCurrentPosition();
        if (gamepad1.dpad_up) {
            targetPosition++;
        } else if (gamepad1.dpad_down) {
            targetPosition--;
        }

        targetPosition = Range.clip(targetPosition, FalconLift.LIFT_POSITION_SUB_STATION, FalconLift.LIFT_POSITION_HIGH_JUNCTION);
        telemetry.addData("targetPosition", "%d", targetPosition);

        if (targetPosition != currentPosition) {
            // Must set motor position before setting motor mode.
            liftMotor.setTargetPosition(targetPosition);
            liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        double liftPower = 0;
        if (targetPosition > currentPosition) {
            liftPower = FalconLift.LIFT_UP_POWER;
        } else if (targetPosition < currentPosition) {
            liftPower = FalconLift.LIFT_DOWN_POWER;
        }

        liftMotor.setPower(liftPower);

        telemetry.addData(TAG, String.format(Locale.US, "Power %.2f, distance %d",
                liftPower, currentPosition));
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

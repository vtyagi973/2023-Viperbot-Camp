package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconConePicker;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconHand;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@Disabled
@TeleOp(group = "TestOp")
public class ServoTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;

    static final double LARGE_INCREMENT = 0.005;     // amount to slew servo each CYCLE_MS cycle
    static final double SMALL_INCREMENT = 0.001;     // amount to slew servo each CYCLE_MS cycle
    static final int CYCLE_MS = 50;           // period of each cycle
    static final double MAX_POS = 1.0;        // Maximum rotational position
    static final double MIN_POS = 0.0;        // Minimum rotational position
    static final String SERVO_NAME = FalconHand.RIGHT_PINCER_SERVO_NAME;

    Servo servo;
    double position;

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
        servo = hardwareMap.get(Servo.class, SERVO_NAME);
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData(">", "Initialization complete, Waiting for start.");
        telemetry.addData(">", "Manufacturer: %s, DeviceName: %s",
                servo.getManufacturer(), servo.getDeviceName());
        telemetry.addData(">", "%s, Port: %d",
                SERVO_NAME, servo.getPortNumber());
        telemetry.addData(">", "Direction: %s, Position: %.4f",
                servo.getDirection() == Servo.Direction.FORWARD ? "Forward" : "Reverse",
                servo.getPosition());
        telemetry.update();
        FalconUtils.sleep(250);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        FalconLogger.enter();
        telemetry.addData(">", "Starting Driver Op");
        telemetry.update();
        position = (MIN_POS + MAX_POS) / 2.0;
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
        telemetry.addData(">", "Use left stick to small adjust servo position");

        if (gamepad1.left_stick_x > 0.5) {
            // Step up until we hit the max value.
            position += SMALL_INCREMENT;
        } else if (gamepad1.left_stick_x < -0.5) {
            // Step down until we hit the min value.
            position -= SMALL_INCREMENT;
        }

        position = Range.clip(position, MIN_POS, MAX_POS);

        // Set the servo to the new position and pause;
        servo.setPosition(position);

        telemetry.addData("Servo Position", "%5.4f", position);
        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
        FalconUtils.sleep(CYCLE_MS);
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

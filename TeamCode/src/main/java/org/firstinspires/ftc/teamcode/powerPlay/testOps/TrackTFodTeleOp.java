package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconTFod;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconVuforia;

import java.util.List;

@SuppressLint("DefaultLocale")
@Disabled
@TeleOp(group = "TestOp")
public class TrackTFodTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;

    FalconVuforia vuforia;
    FalconTFod falconTfod;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        FalconLogger.enter();
        runtime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        vuforia = new FalconVuforia(hardwareMap, telemetry);
        vuforia.init();

        falconTfod = new FalconTFod(hardwareMap, telemetry, vuforia.vuforiaLocalizer);
        falconTfod.init();

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
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        loopTime.reset();
        if (falconTfod.tFod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = falconTfod.tFod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                // step through the list of recognitions and display boundary info.
                int i = 0;
                for (Recognition recognition : updatedRecognitions) {
                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel(),
                            String.format("%.0f, %.0f (%.0f)",
                                    (recognition.getLeft() + recognition.getRight()) / 2.0,
                                    (recognition.getTop() + recognition.getBottom()) / 2.0,
                                    recognition.getConfidence()));
                    i++;
                }
            }
        }

        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
        FalconUtils.sleep(1000);
        FalconLogger.exit();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        FalconLogger.enter();
        if (vuforia != null) {
            vuforia.close();
        }

        FalconLogger.exit();
    }
}

package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.FalconOpenCvCam;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.powerPlay.core.GameElement;
import org.opencv.core.Point;

@Disabled
@Autonomous(group = "TestOp")
public class MultipleObjectDetectionAutoOp extends LinearOpMode {
    FalconOpenCvCam falconOpenCvCam = null;

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing. Please wait...");
        telemetry.update();

        falconOpenCvCam = new FalconOpenCvCam();
        falconOpenCvCam.init(hardwareMap, telemetry);

        telemetry.addData("Status", "Initialization complete. Waiting for start");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (falconOpenCvCam.modPipeline.error) {
                telemetry.addData("Exception: ", falconOpenCvCam.modPipeline.lastException);
            } else {
                for (GameElement ge : falconOpenCvCam.modPipeline.gameElements) {
                    String data = String.format("%d ", ge.attendanceCount());
                    Point midPoint = FalconUtils.getMidpoint(ge.boundingRect);
                    synchronized (ge) {
                        if (ge.elementFound() && ge.elementConsistentlyPresent()) {
                            data += String.format("Mid (%.0f, %.0f) A %.0f AR %.2f",
                                    midPoint.x, midPoint.y,
                                    ge.area, ge.aspectRatio);
                        }
                    }

                    telemetry.addData(ge.tag, data);
                }
            }

            telemetry.update();
            FalconUtils.sleep(100);
        }
    }
}
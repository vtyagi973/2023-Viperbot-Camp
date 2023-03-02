package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconOpenCvCam;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconWebcam;
import org.firstinspires.ftc.teamcode.powerPlay.core.ManualObjectDetectionPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Disabled
@Autonomous(group = "TestOp")
public class ManualObjectDetectionAutoOp extends LinearOpMode {
    private OpenCvCamera webcam;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing. Please wait...");
        telemetry.update();

        // OpenCV webcam
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance()
                .createWebcam(hardwareMap.get(WebcamName.class, FalconOpenCvCam.FRONT_WEBCAM_NAME), cameraMonitorViewId);

        //OpenCV Pipeline
        ManualObjectDetectionPipeline openCvPipeline;
        webcam.setPipeline(openCvPipeline = new ManualObjectDetectionPipeline());

        // Webcam Streaming
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam.startStreaming(FalconWebcam.CAMERA_WIDTH, FalconWebcam.CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                /*
                 * This will be called if the webcam could not be opened
                 */
            }
        });

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        dashboard.startCameraStream(webcam, 0);

        telemetry.addData("Status", "Initialization complete. Waiting for start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (openCvPipeline.error) {
                telemetry.addData("Exception: ", openCvPipeline.lastException);
            } else {
                if (gamepad1.left_stick_x > 0) {
                    openCvPipeline.IncreaseLowerHue();
                } else if (gamepad1.left_stick_x < 0) {
                    openCvPipeline.DecreaseLowerHue();
                }

                if (gamepad1.right_stick_x > 0) {
                    openCvPipeline.IncreaseUpperHue();
                } else if (gamepad1.right_stick_x < 0) {
                    openCvPipeline.DecreaseUpperHue();
                }
            }

            telemetry.addData("Usage", "Use left and right joysticks to adjust Hue constraints");
            telemetry.addData("HSV limits", "[%.0f, %.0f]",
                    openCvPipeline.gameElement.lowerColorThreshold.val[0],
                    openCvPipeline.gameElement.upperColorThreshold.val[0]);
            telemetry.update();
            FalconUtils.sleep(100);
        }
    }
}
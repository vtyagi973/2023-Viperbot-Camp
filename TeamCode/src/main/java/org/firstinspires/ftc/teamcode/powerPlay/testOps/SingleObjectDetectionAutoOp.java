package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconWebcam;
import org.firstinspires.ftc.teamcode.powerPlay.core.SingleObjectDetectionPipeline;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

//Disable if not using FTC Dashboard https://github.com/PinkToTheFuture/OpenCV_FreightFrenzy_2021-2022#opencv_freightfrenzy_2021-2022
@Disabled
@Autonomous(group = "OpenCV")
public class SingleObjectDetectionAutoOp extends LinearOpMode {
    private OpenCvCamera webcam;

    private double CrLowerUpdate;
    private double CbLowerUpdate;
    private double CrUpperUpdate;
    private double CbUpperUpdate;

    private double lowerRuntime = 0;
    private double upperRuntime = 0;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing. Please wait...");
        telemetry.update();

        CrLowerUpdate = SingleObjectDetectionPipeline.scalarLowerYCrCb.val[1];
        CbLowerUpdate = SingleObjectDetectionPipeline.scalarLowerYCrCb.val[2];
        CrUpperUpdate = SingleObjectDetectionPipeline.scalarUpperYCrCb.val[1];
        CbUpperUpdate = SingleObjectDetectionPipeline.scalarUpperYCrCb.val[2];

        // OpenCV webcam
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        //OpenCV Pipeline
        SingleObjectDetectionPipeline openCvPipeline;
        webcam.setPipeline(openCvPipeline = new SingleObjectDetectionPipeline(
                FalconWebcam.borderLeftX, FalconWebcam.borderRightX, FalconWebcam.borderTopY, FalconWebcam.borderBottomY));

        // Configuration of Pipeline
        openCvPipeline.configureScalarLower(SingleObjectDetectionPipeline.scalarLowerYCrCb.val[0], SingleObjectDetectionPipeline.scalarLowerYCrCb.val[1], SingleObjectDetectionPipeline.scalarLowerYCrCb.val[2]);
        openCvPipeline.configureScalarUpper(SingleObjectDetectionPipeline.scalarUpperYCrCb.val[0], SingleObjectDetectionPipeline.scalarUpperYCrCb.val[1], SingleObjectDetectionPipeline.scalarUpperYCrCb.val[2]);

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
            openCvPipeline.configureBorders(
                    FalconWebcam.borderLeftX, FalconWebcam.borderRightX, FalconWebcam.borderTopY, FalconWebcam.borderBottomY);
            if (openCvPipeline.error) {
                telemetry.addData("Exception: ", openCvPipeline.debug);
            } else {
                telemetry.addData("Frame Count", webcam.getFrameCount());
                telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
                telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
                telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
                telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
                telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
            }

            // Only use this line of the code when you want to find the lower and upper values
            testing(openCvPipeline);

            telemetry.addData("RectArea: ", openCvPipeline.getRectArea());
            telemetry.update();

            if (openCvPipeline.getRectArea() > 2000) {
                if (openCvPipeline.getRectMidpointX() > 400) {
                    Barcode_C();
                } else if (openCvPipeline.getRectMidpointX() > 200) {
                    Barcode_B();
                } else {
                    Barcode_A();
                }
            }
        }
    }

    public void testing(SingleObjectDetectionPipeline myPipeline) {
        if (lowerRuntime + 0.05 < getRuntime()) {
            CrLowerUpdate += -gamepad1.left_stick_y;
            CbLowerUpdate += gamepad1.left_stick_x;
            lowerRuntime = getRuntime();
        }
        if (upperRuntime + 0.05 < getRuntime()) {
            CrUpperUpdate += -gamepad1.right_stick_y;
            CbUpperUpdate += gamepad1.right_stick_x;
            upperRuntime = getRuntime();
        }

        CrLowerUpdate = Range.clip(CrLowerUpdate, 16, 240);
        CrUpperUpdate = Range.clip(CrUpperUpdate, 16, 240);
        CbLowerUpdate = Range.clip(CbLowerUpdate, 16, 240);
        CbUpperUpdate = Range.clip(CbUpperUpdate, 16, 240);

        myPipeline.configureScalarLower(16.0, CrLowerUpdate, CbLowerUpdate);
        myPipeline.configureScalarUpper(240.0, CrUpperUpdate, CbUpperUpdate);

        telemetry.addData("lowerCr ", (int) CrLowerUpdate);
        telemetry.addData("lowerCb ", (int) CbLowerUpdate);
        telemetry.addData("UpperCr ", (int) CrUpperUpdate);
        telemetry.addData("UpperCb ", (int) CbUpperUpdate);
    }


    public void Barcode_A() {
        telemetry.addLine("Barcode A");
    }

    public void Barcode_B() {
        telemetry.addLine("Barcode B");
    }

    public void Barcode_C() {
        telemetry.addLine("Barcode C");
    }
}
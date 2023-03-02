package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.ParkingPositionEnum;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

public class FalconOpenCvCam {
    static final String TAG = "FalconOpenCvCam";
    public static final String FRONT_WEBCAM_NAME = "Webcam 1";

    public static final int CAMERA_WIDTH = 640; // width  of wanted camera resolution
    public static final int CAMERA_HEIGHT = 480; // height of wanted camera resolution
    public static final double CAMERA_ASPECT_RATIO = (double) CAMERA_HEIGHT / (double) CAMERA_WIDTH;

    HardwareMap hardwareMap;
    Telemetry telemetry;
    OpenCvCamera frontWebcam = null;
    Boolean initializationIsSuccessful;
    public boolean showTelemetry = true;
    AprilTagDetectionPipeline atdPipeline = null;
    public MultipleObjectDetectionPipeline modPipeline = null;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        FalconLogger.enter();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        initializationIsSuccessful = true;
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        if (cameraMonitorViewId == 0) {
            initializationIsSuccessful = false;
            showCameraFailure();
        }

        // Initialize frontWebcam
        frontWebcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class,
                FRONT_WEBCAM_NAME), cameraMonitorViewId);

        if (frontWebcam == null) {
            initializationIsSuccessful = false;
            showCameraFailure();
        } else {
            // Initialize OpenCV pipeline
            atdPipeline = new AprilTagDetectionPipeline();
            frontWebcam.setPipeline(atdPipeline);

            // Start frontWebcam streaming
            frontWebcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    frontWebcam.startStreaming(FalconWebcam.CAMERA_WIDTH, FalconWebcam.CAMERA_HEIGHT, OpenCvCameraRotation.SIDEWAYS_RIGHT);
                }

                @Override
                public void onError(int errorCode) {
                    /*
                     * This will be called if the camera could not be opened
                     */
                }
            });
        }

        if (BuildConfig.DEBUG) {
            FtcDashboard dashboard = FtcDashboard.getInstance();
            if (dashboard != null) {
                telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
                dashboard.startCameraStream(frontWebcam, 0);
            }
        }

        telemetry.addData(TAG, "Camera initialization process is complete. Check logs for success/failure.");
        FalconLogger.exit();
    }

    public ParkingPositionEnum getParkingPosition() {
        ParkingPositionEnum parkingPosition = ParkingPositionEnum.UNKNOWN;
        try {
            if (webcamIsWorking(frontWebcam) && atdPipeline != null) {
                ArrayList<AprilTagDetection> latestDetections = atdPipeline.getLatestDetections();
                if (latestDetections != null && latestDetections.size() > 0) {
                    for (AprilTagDetection latestDetection : latestDetections) {
                        if (latestDetection.id == AprilTagDetectionPipeline.TAG_ID_ONE) {
                            parkingPosition = ParkingPositionEnum.ONE;
                            break;
                        } else if (latestDetection.id == AprilTagDetectionPipeline.TAG_ID_TWO) {
                            parkingPosition = ParkingPositionEnum.TWO;
                            break;
                        } else if (latestDetection.id == AprilTagDetectionPipeline.TAG_ID_THREE) {
                            parkingPosition = ParkingPositionEnum.THREE;
                            break;
                        }
                    }
                }

                initializationIsSuccessful = true;
            } else {
                initializationIsSuccessful = false;
            }
        } catch (Exception exception) {
            initializationIsSuccessful = false;
        }

        return parkingPosition;
    }

    public void showCameraFailure() {
        if (!initializationIsSuccessful) {
            telemetry.addLine();
            telemetry.addData(TAG, "Webcam failed to start. STOP and RE-INITIALIZE.");
            telemetry.addLine();
        }
    }

    private Boolean webcamIsWorking(OpenCvCamera webcam) {
        return webcam != null && webcam.getFrameCount() > 0 && webcam.getFps() > 0;
    }
}

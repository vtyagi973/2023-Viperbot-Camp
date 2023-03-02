package org.firstinspires.ftc.teamcode.powerPlay.core;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSequenceId;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
import org.firstinspires.ftc.robotcore.internal.system.ContinuationSynchronizer;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FalconWebcam {
    private static final String TAG = "FalconWebcam";
    public static final int CAMERA_WIDTH = 640; // width  of wanted camera resolution
    public static final int CAMERA_HEIGHT = 480; // height of wanted camera resolution
    public static final double CAMERA_ASPECT_RATIO = (double) CAMERA_HEIGHT / (double) CAMERA_WIDTH;

    public static double borderLeftX = 0.0;   //fraction of pixels from the left side of the cam to skip
    public static double borderRightX = 0.0;   //fraction of pixels from the right of the cam to skip
    public static double borderTopY = 0.0;   //fraction of pixels from the top of the cam to skip
    public static double borderBottomY = 0.0;   //fraction of pixels from the bottom of the cam to skip


    /**
     * How long we are to wait to be granted permission to use the camera before giving up. Here,
     * we wait indefinitely
     */
    private static final int secondsPermissionTimeout = Integer.MAX_VALUE;

    /**
     * State regarding our interaction with the camera
     */
    private CameraManager cameraManager;
    private WebcamName cameraName;
    private Camera camera;
    private CameraCaptureSession cameraCaptureSession;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    /**
     * The queue into which all frames from the camera are placed as they become available.
     * Frames which are not processed by the OpMode are automatically discarded.
     */
    private EvictingBlockingQueue<Bitmap> frameQueue;

    /**
     * A utility object that indicates where the asynchronous callbacks from the camera
     * infrastructure are to run. In this OpMode, that's all hidden from you (but see {@link #startCamera}
     * if you're curious): no knowledge of multi-threading is needed here.
     */
    private Handler callbackHandler;

    public void closeCamera() {
        FalconLogger.enter();
        stopCamera();
        if (camera != null) {
            camera.close();
            camera = null;
        }

        FalconLogger.exit();
    }

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        FalconLogger.enter();
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        callbackHandler = CallbackLooper.getDefault().getHandler();
        cameraManager = ClassFactory.getInstance().getCameraManager();
        cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");
        initializeFrameQueue(2);
        telemetry.addData(TAG, "Camera initialized.");
        FalconLogger.exit();
    }

    //----------------------------------------------------------------------------------------------
    // Camera operations
    //----------------------------------------------------------------------------------------------

    private void initializeFrameQueue(int capacity) {
        FalconLogger.enter();
        /* The frame queue will automatically throw away bitmap frames if they are not processed
         * quickly by the OpMode. This avoids a buildup of frames in memory
         */
        frameQueue = new EvictingBlockingQueue<Bitmap>(new ArrayBlockingQueue<Bitmap>(capacity));
        frameQueue.setEvictAction(new Consumer<Bitmap>() {
            @Override
            public void accept(Bitmap frame) {
                frame.recycle(); // not strictly necessary, but helpful
            }
        });

        telemetry.addData(TAG, "FrameQueue capacity: %d", capacity);
        FalconLogger.exit();
    }

    public void openCamera() {
        FalconLogger.enter();
        if (camera != null) return; // be idempotent

        Deadline deadline = new Deadline(secondsPermissionTimeout, TimeUnit.SECONDS);
        camera = cameraManager.requestPermissionAndOpenCamera(deadline, cameraName, null);
        if (camera == null) {
            telemetry.addData(TAG, "camera not found or permission to use not granted: %s", cameraName);
        }

        FalconLogger.exit();
    }

    /**
     * Bitmap size is 640x480
     * Buffer size is 1228800 bytes = 640*480*4
     * Bitmap Resolution is 96 DPI
     * Bitmap format is aRGB (32 bit int per pixel)
     */
    public void saveBitmap() {
        FalconLogger.enter();
        Bitmap bmp = frameQueue.poll();
        if (bmp != null) {
            BitmapUtils bmpUtils = new BitmapUtils(telemetry);
            bmpUtils.saveBitmap(bmp);
        } else {
            telemetry.addData(TAG, "No bitmap in frameQueue!");
        }

        FalconLogger.exit();
    }

    public void startCamera() {
        FalconLogger.enter();
        if (cameraCaptureSession != null) return; // be idempotent

        /* YUY2 is supported by all Webcams, per the USB Webcam standard: See "USB Device Class Definition
         * for Video Devices: Uncompressed Payload, Table 2-1". Further
         * This is the *only* image format supported by the Logitech camera!!
         * Do not change it. */
        final int imageFormat = ImageFormat.YUY2;

        /* Verify that the image is supported, and fetch size and desired frame rate if so */
        CameraCharacteristics cameraCharacteristics = cameraName.getCameraCharacteristics();
        if (!FalconUtils.contains(cameraCharacteristics.getAndroidFormats(), imageFormat)) {
            telemetry.addData(TAG, "image format not supported: %d", imageFormat);
            return;
        }

        final Size size = cameraCharacteristics.getDefaultSize(imageFormat);
        final int fps = cameraCharacteristics.getMaxFramesPerSecond(imageFormat, size);

        /* Some of the logic below runs asynchronously on other threads. Use of the synchronizer
         * here allows us to wait in this method until all that asynchrony completes before returning. */
        final ContinuationSynchronizer<CameraCaptureSession> synchronizer = new ContinuationSynchronizer<>();
        try {
            /* Create a session in which requests to capture frames can be made */
            camera.createCaptureSession(Continuation.create(callbackHandler, new CameraCaptureSession.StateCallbackDefault() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        /* The session is ready to go. Start requesting frames */
                        final CameraCaptureRequest captureRequest = camera.createCaptureRequest(imageFormat, size, fps);
                        session.startCapture(captureRequest,
                                new CameraCaptureSession.CaptureCallback() {
                                    @Override
                                    public void onNewFrame(@NonNull CameraCaptureSession session, @NonNull CameraCaptureRequest request, @NonNull CameraFrame cameraFrame) {
                                        /* A new frame is available. The frame data has <em>not</em> been copied for us, and we can only access it
                                         * for the duration of the callback. So we copy here manually. */
                                        Bitmap bmp = captureRequest.createEmptyBitmap();
                                        cameraFrame.copyToBitmap(bmp);
                                        frameQueue.offer(bmp);
                                    }
                                },
                                Continuation.create(callbackHandler, new CameraCaptureSession.StatusCallback() {
                                    @Override
                                    public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, CameraCaptureSequenceId cameraCaptureSequenceId, long lastFrameNumber) {
                                        RobotLog.ii(TAG, "capture sequence %s reports completed: lastFrame=%d", cameraCaptureSequenceId, lastFrameNumber);
                                    }
                                })
                        );
                        synchronizer.finish(session);
                    } catch (CameraException | RuntimeException e) {
                        telemetry.addData(TAG, "exception starting capture");
                        session.close();
                        synchronizer.finish(null);
                    }
                }
            }));
        } catch (CameraException | RuntimeException e) {
            String message = "xception starting camera";
            RobotLog.ee(TAG, e, message);
            telemetry.addData(TAG, message);
            synchronizer.finish(null);
        }

        /* Wait for all the asynchrony to complete */
        try {
            synchronizer.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        /* Retrieve the created session. This will be null on error. */
        cameraCaptureSession = synchronizer.getValue();
        FalconLogger.exit();
    }

    private void stopCamera() {
        FalconLogger.enter();
        if (cameraCaptureSession != null) {
            cameraCaptureSession.stopCapture();
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        FalconLogger.exit();
    }
}

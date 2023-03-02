package org.firstinspires.ftc.teamcode.powerPlay.core;

import android.graphics.Bitmap;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.system.Assert;
import org.firstinspires.ftc.teamcode.VuforiaImage;

public class FalconVuforia {
    private static final String TAG = "FalconVuforia";
    public static double MM_PER_INCH = 25.40;
    public static final String IMAGE_BLUE_REAR_WALL_NAME = "Blue Rear Wall";
    public static final String IMAGE_BLUE_ALLIANCE_WALL_NAME = "Blue Alliance Wall";
    public static final String IMAGE_RED_REAR_WALL_NAME = "Red Rear Wall";
    public static final String IMAGE_RED_ALLIANCE_WALL_NAME = "Red Alliance Wall";
    private static final String VUFORIA_KEY =
            "AcRuZsf/////AAABmS9lhPP8OEViszn4ZqQHBd6JuDSpkonhwBTo+fou6npEXikvqQNj7GH05YHbFS8OUCHojw7MEYMg9zvDm4oOdqMFwGbULTqwPDpUYCcqXpvhuttkyNdhP7O/3luilJ9v4qyYmVG9kycjQoR8cikGL0x8LoIi2y6V0Kiz8goR7A7thJWllJj678jL4+A29vjVCinQW2gyt+vFohjZU+kaH849m9rpIJvg8pCtF3yaVwkMqp2MWJJWbOay6Q/uKKK0ehcEraYTW0QdnHgGVNVPLCstaonAPALWLEqrTolTD0fl3CqT8kDYcmVoXnUEzJ42E6cP+AZLuJ72C/5pXvUvVJFYvAw9yaAiGPCYeNZArnPR";

    HardwareMap hwMap;
    Telemetry telemetry;
    OpenGLMatrix targetPose = null;
    public VuforiaLocalizer vuforiaLocalizer = null;
    VuforiaTrackables vuforiaTrackables = null;
    VuforiaTrackable vuforiaTrackable = null;

    /* Constructor */
    public FalconVuforia(HardwareMap hardwareMap, Telemetry telemetry) {
        hwMap = hardwareMap;
        this.telemetry = telemetry;
    }

    public void close() {
        FalconLogger.enter();
        if (vuforiaTrackables != null) {
            vuforiaTrackables.deactivate();
            vuforiaTrackables = null;
        }

        if (vuforiaLocalizer != null) {
            vuforiaLocalizer.close();
            vuforiaLocalizer = null;
        }

        FalconLogger.exit();
    }

    public VuforiaImage getConcordantVuMarkLocation(LinearOpMode opMode) {
        FalconLogger.enter();
        VuforiaImage image = new VuforiaImage();
        int count = 0;

        // Take first 3 consistent readings out of first 50
        for (int i = 0; i < 50 && opMode.opModeIsActive(); i++) {
            image = locateVuMark();
            if (image != null && image.Found) {
                count++;
                if (count >= 3) {
                    break;
                }
            } else {
                count = 0;
            }

            // Try for a max of 50 * endAutoHeading milliseconds
            FalconUtils.sleep(50);
        }

        FalconLogger.exit();
        return image;
    }

    public void init() {
        FalconLogger.enter();
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        // Turn off Extended tracking.  Set this true if you want Vuforia to track beyond the target.
        parameters.useExtendedTracking = false;

        // Connect to the camera we are to use.  This name must match what is set up in Robot Configuration
        parameters.cameraName = hwMap.get(WebcamName.class, "Webcam 1");
        parameters.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;
        vuforiaLocalizer = ClassFactory.getInstance().createVuforia(parameters);
        if (vuforiaLocalizer == null) {
            telemetry.addData(TAG, "Not initialized");
        } else {
            Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
            vuforiaLocalizer.setFrameQueueCapacity(4);
            telemetry.addData(TAG, "Initialized");
        }

        FalconLogger.exit();
    }

    /**
     * Image heading is positive when image is to the right of robot.
     *
     * @return
     */
    public VuforiaImage locateNavigationImage() {
        FalconLogger.enter();
        VuforiaImage image = new VuforiaImage();
        double MM_PER_INCH = 25.40;

        if (vuforiaTrackables != null) {
            for (VuforiaTrackable trackable : vuforiaTrackables) {
                if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                    targetPose = ((VuforiaTrackableDefaultListener) trackable.getListener()).getVuforiaCameraFromTarget();

                    // if we have a target, process the "pose" to determine the position of the target relative to the robot.
                    if (targetPose != null) {
                        String imageName = trackable.getName();
                        if (imageName.equals(IMAGE_BLUE_REAR_WALL_NAME) || imageName.equals(IMAGE_RED_REAR_WALL_NAME) ||
                                imageName.equals(IMAGE_BLUE_ALLIANCE_WALL_NAME) || imageName.equals(IMAGE_RED_ALLIANCE_WALL_NAME)) {
                            image.Found = true;
                            image.Name = imageName;
                            VectorF trans = targetPose.getTranslation();

                            // Extract the Heading & Distance components of the offset of the target relative to the robot
                            //  Metric conversion
                            double targetX = trans.get(0) / MM_PER_INCH; // Image Heading axis
                            double targetY = trans.get(2) / MM_PER_INCH; // Image Z axis

                            // target range is based on distance from robot position to origin (right triangle).
                            image.Distance = Math.hypot(targetX, targetY);

                            // target bearing is based on angle formed between the Heading axis and the target range line
                            // Return opposite to be in sync with Gyro functionality.
                            image.Heading = -Math.toDegrees(Math.asin(targetX / image.Distance));

                            break;  // jump out of target tracking loop if we find a target.
                        }
                    }
                }
            }

            // Inform the driver what we see, and what to do.
            if (image.Found) {
                telemetry.addData(TAG, "Image %s", image.Name);
                telemetry.addData(TAG, "Distance %5.1f inches", image.Distance);
                telemetry.addData(TAG, "Heading%3.0f degrees", image.Heading);
            } else {
                telemetry.addData(TAG, "No image found");
            }
        } else {
            telemetry.addData(TAG, "Vuforia trackable not set");
        }

        FalconLogger.exit();
        return image;
    }

    public VuforiaImage locateVuMark() {
        FalconLogger.enter();
        VuforiaImage image = new VuforiaImage();
        if (vuforiaTrackables != null) {
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(vuforiaTrackable);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                image.Found = true;
                image.VuMark = vuMark;
                telemetry.addData("VuMark", "%s visible", vuMark);
            } else {
                telemetry.addData(TAG, "VuMark Relic not found");
            }

            // Inform the driver what we see, and what to do.
            if (image.Found) {
                telemetry.addData(TAG, "Image %s", image.Name);
            } else {
                telemetry.addData(TAG, "VuMark not detected");
            }
        } else {
            telemetry.addData(TAG, "VuMark trackable not set");
        }

        FalconLogger.exit();
        return image;
    }

    public void saveImage() {
        FalconLogger.enter();
        try {
            if (vuforiaLocalizer != null) {
                VuforiaLocalizer.CloseableFrame frame = vuforiaLocalizer.getFrameQueue().take();
                Assert.assertNotNull(frame, "saveImage>frame");
                long numImages = frame.getNumImages();
                Assert.assertTrue(numImages > 0, "saveImage>numImages");
                for (int i = 0; i < numImages; i++) {
                    Image image = frame.getImage(i);
                    telemetry.addData(TAG, "Size %dx%d format %d",
                            image.getWidth(), image.getHeight(), image.getFormat());
                    if (image.getFormat() == PIXEL_FORMAT.RGB565) {
                        Bitmap bmp = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
                        bmp.copyPixelsFromBuffer(image.getPixels());
                        BitmapUtils bmpUtils = new BitmapUtils(telemetry);
                        bmpUtils.saveBitmap(bmp);
                    }
                }

                frame.close();
            } else {
                telemetry.addData(TAG, "VuforiaLocalizer is not set, not saving bitmap.");
            }
        } catch (InterruptedException e) {
            RobotLog.ee(TAG, e, e.getMessage());
        }

        FalconLogger.exit();
    }

    /**
     * Loads trackable assets (images) and starts tracking them.
     */
    public void trackNavigationImages() {
        FalconLogger.enter();
        if (vuforiaLocalizer != null) {
            // Load the trackable objects from the Assets file, and give them meaningful names
            vuforiaTrackables = vuforiaLocalizer.loadTrackablesFromAsset("PowerPlay");
            if (vuforiaTrackables != null) {
                vuforiaTrackables.get(0).setName(IMAGE_BLUE_REAR_WALL_NAME);
                vuforiaTrackables.get(1).setName(IMAGE_RED_ALLIANCE_WALL_NAME);
                vuforiaTrackables.get(2).setName(IMAGE_RED_REAR_WALL_NAME);
                vuforiaTrackables.get(3).setName(IMAGE_RED_ALLIANCE_WALL_NAME);

                // Start tracking targets in the background
                vuforiaTrackables.activate();
                telemetry.addData(TAG, "Tracking freight frenzy images");
            } else {
                telemetry.addData(TAG, "Trackables not set");
            }
        } else {
            telemetry.addData(TAG, "VuforiaLocalizer not set");
        }

        FalconLogger.exit();
    }

    /**
     * Loads trackable assets (images) and starts tracking them.
     */
    public void trackVuMarks() {
        FalconLogger.enter();
        if (vuforiaLocalizer != null) {
            // Load the trackable objects from the Assets file, and give them meaningful names
            vuforiaTrackables = vuforiaLocalizer.loadTrackablesFromAsset("RelicVuMark");
            if (vuforiaTrackables != null) {
                vuforiaTrackable = vuforiaTrackables.get(0);

                // Start tracking targets in the background
                vuforiaTrackables.activate();
                telemetry.addData(TAG, "Tracking VuMarks");
            } else {
                telemetry.addData(TAG, "Trackables not set");
            }
        } else {
            telemetry.addData(TAG, "VuforiaLocalizer not set");
        }

        FalconLogger.exit();
    }
}

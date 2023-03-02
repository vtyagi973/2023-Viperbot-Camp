package org.firstinspires.ftc.teamcode.powerPlay.core;

import android.annotation.SuppressLint;

import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.GameElementTypeEnum;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

public class MultipleObjectDetectionPipeline extends OpenCvPipeline {
    private static final String TAG = "ModPipeline";

    // Volatile because accessed by OpMode without syncObject
    public volatile boolean error = false;
    public volatile Exception lastException = null;

    private final Mat alternativeColorMat = new Mat();
    private final Mat processedMat = new Mat();

    public final GameElement[] gameElements = new GameElement[]{
            GameElement.getGE(GameElementTypeEnum.YELLOW)
    };

    public MultipleObjectDetectionPipeline() {
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Mat processFrame(Mat input) {
        try {
            error = false;
            lastException = null;
            for (GameElement gameElement : gameElements) {
                // Process Image
                Imgproc.cvtColor(input, alternativeColorMat, gameElement.colorConversionCode);
                Core.inRange(alternativeColorMat, gameElement.lowerColorThreshold, gameElement.upperColorThreshold, processedMat);
                // Remove Noise
                Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_OPEN, new Mat());
                Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_CLOSE, new Mat());
                // GaussianBlur
                Imgproc.GaussianBlur(processedMat, processedMat, new Size(5.0, 15.0), 0.00);
                // Find Contours within the color thresholds
                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(processedMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                if (BuildConfig.DEBUG) {
                    for (int i = 0; i < contours.size(); i++) {
                        double contourArea = Imgproc.contourArea(contours.get(i));
                        if (contourArea >= gameElement.minSize.area()
                                && contourArea <= gameElement.maxSize.area()) {
                            Imgproc.drawContours(input, contours, i, gameElement.elementColor, gameElement.borderSize);
                        }
                    }
                }

                synchronized (gameElement) {
                    gameElement.invalidate();

                    // Loop Through Contours
                    for (MatOfPoint contour : contours) {
                        double contourArea = Imgproc.contourArea(contour);
                        if (contourArea >= gameElement.minSize.area()
                                && contourArea > gameElement.area
                                && contourArea <= gameElement.maxSize.area()) {
                            // Found contour within the area thresholds
                            Point[] contourArray = contour.toArray();
                            MatOfPoint2f areaPoints = new MatOfPoint2f(contourArray);
                            RotatedRect tempRect = Imgproc.minAreaRect(areaPoints);
                            areaPoints.release();
                            double aspectRatio = FalconUtils.getAspectRatio(tempRect);
                            if (aspectRatio >= gameElement.minAspectRatio && aspectRatio <= gameElement.maxAspectRatio) {
                                // Found contour within the aspect ratio thresholds
                                gameElement.validate(tempRect, aspectRatio, contourArea);
                            }
                        }

                        contour.release();
                    }
                }

                if (gameElement.elementFound()) {
                    gameElement.markPresent();
                    if (BuildConfig.DEBUG) {
                        Imgproc.ellipse(input, gameElement.rotatedRect, gameElement.elementColor, gameElement.borderSize);

                        // Display Data
                        Imgproc.putText(input, String.format("%s: A%.0f AR%.2f",
                                        gameElement.tag, gameElement.area, gameElement.aspectRatio),
                                new Point(gameElement.boundingRect.x, gameElement.boundingRect.y + 20),
                                0, 0.6, gameElement.elementColor, gameElement.borderSize);
                    }
                } else {
                    gameElement.markAbsent();
                }
            }
        } catch (Exception exception) {
            lastException = exception;
            error = true;
        }

        return input;
    }

    @Override
    public void onViewportTapped() {
        /*
         * The viewport (if one was specified in the constructor) can also be dynamically "paused"
         * and "resumed". The primary use case of this is to reduce CPU, memory, and power load
         * when you need your vision pipeline running, but do not require a live preview on the
         * robot controller screen. For instance, this could be useful if you wish to see the live
         * camera preview as you are initializing your robot, but you no longer require the live
         * preview after you have finished your initialization process; pausing the viewport does
         * not stop running your pipeline.
         *
         */

        // Execute tap action.

    }

    public void draw(Mat input, RotatedRect rotatedRect, Scalar color, int thickness) {
        Point[] points = new Point[4];
        try {
            rotatedRect.points(points);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(input, points[i], points[(i + 1) % 4], color, thickness);
            }
        } catch (Exception exception) {
        }
    }
}
package org.firstinspires.ftc.teamcode.powerPlay.core;

import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.GameElementTypeEnum;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

// Credits to team 7303 RoboAvatars, adjusted by team 3954 Pink to the Future

public class SingleObjectDetectionPipeline extends OpenCvPipeline {
    private static final String TAG = "SodPipeline";

    // Pink, the default color                         Y      Cr     Cb    (Do not change Y)
    public static Scalar scalarLowerYCrCb = new Scalar(0, 160.0, 100.0);
    public static Scalar scalarUpperYCrCb = new Scalar(255.0, 255.0, 255.0);

    // Use this picture for you own color https://github.com/PinkToTheFuture/OpenCV_FreightFrenzy_2021-2022/blob/main/YCbCr.jpeg
    // Note that the Cr and Cb values range between 0-255. this means that the origin of the coordinate system is (128,128)

    // Volatile because accessed by OpMode without syncObject
    public volatile boolean error = false;
    public volatile Exception debug;

    private double borderLeftX;     //fraction of pixels from the left side of the cam to skip
    private double borderRightX;    //fraction of pixels from the right of the cam to skip
    private double borderTopY;      //fraction of pixels from the top of the cam to skip
    private double borderBottomY;   //fraction of pixels from the bottom of the cam to skip

    private int loopCounter = 0;
    private int pLoopCounter = 0;

    private final Mat yCrCbMat = new Mat();
    private final Mat processedMat = new Mat();

    private final Rect maxRect = new Rect(1, 1, 1, 1);
    private double maxArea = maxRect.area();
    private boolean first = false;

    private final GameElement[] gameElements = new GameElement[]{
            GameElement.getGE(GameElementTypeEnum.PINK)
    };

    public SingleObjectDetectionPipeline(double borderLeftX, double borderRightX, double borderTopY, double borderBottomY) {
        configureBorders(borderLeftX, borderRightX, borderTopY, borderBottomY);
    }

    public void configureScalarLower(double y, double cr, double cb) {
        scalarLowerYCrCb = new Scalar(y, cr, cb);
    }

    public void configureScalarUpper(double y, double cr, double cb) {
        scalarUpperYCrCb = new Scalar(y, cr, cb);
    }

    public void configureScalarLower(int y, int cr, int cb) {
        scalarLowerYCrCb = new Scalar(y, cr, cb);
    }

    public void configureScalarUpper(int y, int cr, int cb) {
        scalarUpperYCrCb = new Scalar(y, cr, cb);
    }

    public void configureBorders(double borderLeftX, double borderRightX, double borderTopY, double borderBottomY) {
        this.borderLeftX = borderLeftX;
        this.borderRightX = borderRightX;
        this.borderTopY = borderTopY;
        this.borderBottomY = borderBottomY;
    }

    @Override
    public Mat processFrame(Mat input) {
        try {
            int imageWidth = input.width();
            int imageHeight = input.height();

            // Process Image
            Imgproc.cvtColor(input, yCrCbMat, Imgproc.COLOR_RGB2YCrCb);
            Core.inRange(yCrCbMat, scalarLowerYCrCb, scalarUpperYCrCb, processedMat);
            // Remove Noise
            Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_OPEN, new Mat());
            Imgproc.morphologyEx(processedMat, processedMat, Imgproc.MORPH_CLOSE, new Mat());
            // GaussianBlur
            Imgproc.GaussianBlur(processedMat, processedMat, new Size(5.0, 15.0), 0.00);
            // Find Contours
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(processedMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            // Draw Contours
            Imgproc.drawContours(input, contours, -1, FalconColorUtils.RGB_BLACK);

            // Loop Through Contours
            for (MatOfPoint contour : contours) {
                Point[] contourArray = contour.toArray();

                // Bound Rectangle if Contour is Large Enough
                if (contourArray.length >= 15) {
                    MatOfPoint2f areaPoints = new MatOfPoint2f(contourArray);
                    Rect rect = Imgproc.boundingRect(areaPoints);

                    if (rect.area() > maxArea
                            && rect.x + (rect.width / 2.0) > (borderLeftX * imageWidth)
                            && rect.x + (rect.width / 2.0) < imageWidth - (borderRightX * imageWidth)
                            && rect.y + (rect.height / 2.0) > (borderTopY * imageHeight)
                            && rect.y + (rect.height / 2.0) < imageHeight - (borderBottomY * imageHeight)

                            || loopCounter - pLoopCounter > 6
                            && rect.x + (rect.width / 2.0) > (borderLeftX * imageWidth)
                            && rect.x + (rect.width / 2.0) < imageWidth - (borderRightX * imageWidth)
                            && rect.y + (rect.height / 2.0) > (borderTopY * imageHeight)
                            && rect.y + (rect.height / 2.0) < imageHeight - (borderBottomY * imageHeight)
                    ) {
                        maxArea = rect.area();
                        FalconUtils.copyRect(rect, maxRect);
                        pLoopCounter++;
                        loopCounter = pLoopCounter;
                        first = true;
                    } else if (loopCounter - pLoopCounter > 10) {
                        maxArea = 0;
                        FalconUtils.zeroRect(maxRect);
                    }

                    areaPoints.release();
                }
                contour.release();
            }
            if (contours.isEmpty()) {
                FalconUtils.zeroRect(maxRect);
            }

            // Draw Rectangles If Area Is At Least the desired area size
            if (first && getRectArea() >= 625) {
                Imgproc.rectangle(input, maxRect, FalconColorUtils.RGB_GREEN, 2);
            }
            // Draw Borders
            Imgproc.rectangle(input, new Rect(
                    (int) (borderLeftX * imageWidth),
                    (int) (borderTopY * imageHeight),
                    (int) (imageWidth - (borderRightX * imageWidth) - (borderLeftX * imageWidth)),
                    (int) (imageHeight - (borderBottomY * imageHeight) - (borderTopY * imageHeight))
            ), FalconColorUtils.RGB_PINK, 2);

            // Display Data
            Imgproc.putText(input, "Area: " + getRectArea() + " Midpoint: " + getRectMidpointXY().x + " , " + getRectMidpointXY().y,
                    new Point(5, imageHeight - 5), 0, 0.6, FalconColorUtils.RGB_WHITE, 2);

            loopCounter++;
        } catch (Exception e) {
            debug = e;
            error = true;
        }
        return input;
    }

    /*
    Synchronize these operations as the user code could be incorrect otherwise, i.e a property is read
    while the same rectangle is being processedMat in the pipeline, leading to some values being not
    synced.
     */
    public int getRectHeight() {
        synchronized (maxRect) {
            return maxRect.height;
        }
    }

    public int getRectWidth() {
        synchronized (maxRect) {
            return maxRect.width;
        }
    }

    public int getRectX() {
        synchronized (maxRect) {
            return maxRect.x;
        }
    }

    public int getRectY() {
        synchronized (maxRect) {
            return maxRect.y;
        }
    }

    public double getRectMidpointX() {
        synchronized (maxRect) {
            return maxRect.x + (maxRect.width / 2.0);
        }
    }

    public double getRectMidpointY() {
        synchronized (maxRect) {
            return maxRect.y + (maxRect.height / 2.0);
        }
    }

    public Point getRectMidpointXY() {
        synchronized (maxRect) {
            return new Point(getRectMidpointX(), getRectMidpointY());
        }
    }

    public double getRectArea() {
        synchronized (maxRect) {
            return maxRect.area();
        }
    }
}
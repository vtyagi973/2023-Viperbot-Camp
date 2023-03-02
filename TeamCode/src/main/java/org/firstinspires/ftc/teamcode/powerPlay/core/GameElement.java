package org.firstinspires.ftc.teamcode.powerPlay.core;

import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.GameElementTypeEnum;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class GameElement {
    private static final String TAG = "GameElement";
    public static final int ATTENDANCE_THRESHOLD = 10;
    public double minAspectRatio, maxAspectRatio;
    public double area;
    public double aspectRatio;
    public Scalar elementColor;
    public Scalar lowerColorThreshold;
    public Scalar upperColorThreshold;
    public Rect minSize, maxSize;
    public final int borderSize;
    public int colorConversionCode;
    public String tag;
    public boolean found;
    public Rect boundingRect;
    public RotatedRect rotatedRect;
    private int[] attendanceRegiter;
    private int attendanceIndex;

    public GameElement() {
        // OpenCV processes about 10 FPS
        // Look back 2 seconds of processed frames
        attendanceRegiter = new int[20];
        attendanceIndex = 0;
        aspectRatio = 0;
        boundingRect = new Rect();
        rotatedRect = new RotatedRect();
        borderSize = 2;
        colorConversionCode = Imgproc.COLOR_RGB2YCrCb;
        elementColor = FalconColorUtils.RGB_WHITE;
        found = false;
        lowerColorThreshold = new Scalar(0.0, 0.0, 0.0);
        upperColorThreshold = new Scalar(255.0, 255.0, 255.0);
        tag = "Change this text";
        minSize = new Rect(0, 0, 90, 90);
        maxSize = new Rect(0, 0, 160, 160);
        minAspectRatio = 1.0;
        maxAspectRatio = 2.2;
    }

    public int attendanceCount() {
        int sum = 0;
        synchronized (attendanceRegiter) {
            for (int i = 0; i < attendanceRegiter.length; i++) {
                sum += attendanceRegiter[i];
            }
        }

        return sum;
    }

    public boolean elementConsistentlyPresent() {
        return attendanceCount() >= ATTENDANCE_THRESHOLD;
    }

    public boolean elementFound() {
        synchronized (this) {
            return found;
        }
    }

    public static GameElement getGE(GameElementTypeEnum gameElementType) {
        GameElement gameElement = new GameElement();
        switch (gameElementType) {
            case BLUE:
                gameElement.elementColor = FalconColorUtils.RGB_BLUE;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(100.0, 20.0, 50.0);
                gameElement.upperColorThreshold = new Scalar(140.0, 255.0, 255.0);
                gameElement.tag = "Blue";
                break;
            case BLACK:
                gameElement.elementColor = FalconColorUtils.RGB_BLACK;
                gameElement.lowerColorThreshold = new Scalar(0.0, 63.0, 63.0);
                gameElement.upperColorThreshold = new Scalar(31.0, 191.0, 191.0);
                gameElement.tag = "Black";
                break;
            case GRAY:
                gameElement.elementColor = FalconColorUtils.RGB_GRAY;
                gameElement.lowerColorThreshold = new Scalar(63.0, 63.0, 63.0);
                gameElement.upperColorThreshold = new Scalar(191.0, 191.0, 191.0);
                gameElement.tag = "Gray";
                break;
            case GREEN:
                gameElement.elementColor = FalconColorUtils.RGB_GREEN;
                /* YCbCr
                gameElement.lowerColorThreshold = new Scalar(63.0, 0.0, 0.0);
                gameElement.upperColorThreshold = new Scalar(255.0, 120.0, 120.0);
                */

                //* HSV
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(45.0, 20.0, 50.0);
                gameElement.upperColorThreshold = new Scalar(86.0, 255.0, 255.0);
                //*/
                gameElement.tag = "Green";
                break;
            case ORANGE:
                gameElement.elementColor = FalconColorUtils.RGB_ORANGE;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(11.0, 10.0, 10.0);
                gameElement.upperColorThreshold = new Scalar(24.0, 255.0, 255.0);
                gameElement.tag = "Orange";
                break;
            case PINK:
                gameElement.elementColor = FalconColorUtils.RGB_PINK;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(150.0, 10.0, 10.0);
                gameElement.upperColorThreshold = new Scalar(170.0, 255.0, 255.0);
                gameElement.tag = "Pink";
                break;
            case PURPLE:
                gameElement.elementColor = FalconColorUtils.RGB_PURPLE;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(140.0, 10.0, 10.0);
                gameElement.upperColorThreshold = new Scalar(160.0, 255.0, 255.0);
                gameElement.tag = "Purple";
                break;
            case RED1:
                gameElement.elementColor = FalconColorUtils.RGB_RED;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(0.0, 20.0, 50.0);
                gameElement.upperColorThreshold = new Scalar(25.0, 255.0, 255.0);
                gameElement.tag = "Red1";
                break;
            case RED2:
                gameElement.elementColor = FalconColorUtils.RGB_RED;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(145.0, 20.0, 50.0);
                gameElement.upperColorThreshold = new Scalar(180.0, 255.0, 255.0);
                gameElement.tag = "Red2";
                break;
            case YELLOW:
                // Yellow Junctions
                gameElement.elementColor = FalconColorUtils.RGB_YELLOW;
                gameElement.colorConversionCode = Imgproc.COLOR_RGB2HSV;
                gameElement.lowerColorThreshold = new Scalar(20.0, 100.0, 100.0);
                gameElement.upperColorThreshold = new Scalar(40.0, 255.0, 255.0);

                gameElement.minSize = new Rect(0, 0, 175, 175);
                gameElement.maxSize = new Rect(0, 0, 350, 350);
                gameElement.minAspectRatio = 0.05;
                gameElement.maxAspectRatio = 0.50;
                gameElement.tag = "Yellow";
                break;
            case WHITE:
                gameElement.elementColor = FalconColorUtils.RGB_WHITE;
                gameElement.lowerColorThreshold = new Scalar(191.0, 63.0, 63.0);
                gameElement.upperColorThreshold = new Scalar(255.0, 191.0, 191.0);
                gameElement.tag = "White";
                break;
        }

        return gameElement;
    }

    public Point getMidPoint() {
        synchronized (this) {
            return FalconUtils.getMidpoint(boundingRect);
        }
    }

    public void invalidate() {
        synchronized (this) {
            area = 0;
            boundingRect = new Rect();
            rotatedRect = new RotatedRect();
            found = false;
        }
    }

    public void markAbsent() {
        synchronized (attendanceRegiter) {
            attendanceRegiter[attendanceIndex] = 0;
            attendanceIndex = (attendanceIndex + 1) % attendanceRegiter.length;
        }
    }

    public void markPresent() {
        synchronized (attendanceRegiter) {
            attendanceRegiter[attendanceIndex] = 1;
            attendanceIndex = (attendanceIndex + 1) % attendanceRegiter.length;
        }
    }


    public void validate(RotatedRect rRect, double aspectRatio, double area) {
        synchronized (this) {
            FalconUtils.copyRect(rRect, rotatedRect);
            FalconUtils.copyRect(rotatedRect.boundingRect(), boundingRect);
            this.area = area;
            this.aspectRatio = aspectRatio;
            found = true;
        }
    }
}
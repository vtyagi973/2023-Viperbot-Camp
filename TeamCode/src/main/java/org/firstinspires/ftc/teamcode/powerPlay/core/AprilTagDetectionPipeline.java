/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.powerPlay.core;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.apriltag.AprilTagDetectorJNI;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;

public class AprilTagDetectionPipeline extends OpenCvPipeline {
    private static final String TAG = "AtdPipeline";

    // Volatile because accessed by OpMode without syncObject
    public volatile boolean error = false;
    public volatile Exception lastException = null;

    public static final int TAG_ID_ONE = 1;
    public static final int TAG_ID_TWO = 2;
    public static final int TAG_ID_THREE = 3;
    private long nativeAprilTagPtr = 0;
    private Mat greyScaleMat = new Mat();
    private ArrayList<AprilTagDetection> latestDetections = null;

    private final Object detectionsLock = new Object();

    private double fx = 578.272;
    private double fy = 578.272;
    private double cx = 402.145;
    private double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    public AprilTagDetectionPipeline() {
        // Allocate a native context object. See the corresponding deletion in the finalizer
        nativeAprilTagPtr = AprilTagDetectorJNI.createApriltagDetector(AprilTagDetectorJNI.TagFamily.TAG_36h11.string, 3, 3);
    }

    @Override
    public void finalize() {
        // Might be null if createApriltagDetector() threw an exception
        if (nativeAprilTagPtr != 0) {
            // Delete the native context we created in the constructor
            AprilTagDetectorJNI.releaseApriltagDetector(nativeAprilTagPtr);
            nativeAprilTagPtr = 0;
        }
    }

    public ArrayList<AprilTagDetection> getLatestDetections() {
        ArrayList<AprilTagDetection> tempDetections;
        synchronized (detectionsLock) {
            tempDetections = latestDetections;
        }

        return tempDetections;
    }

    @Override
    public Mat processFrame(Mat input) {
        try {
            error = false;
            lastException = null;

            // Convert to greyscale
            Imgproc.cvtColor(input, greyScaleMat, Imgproc.COLOR_RGBA2GRAY);

            // Run AprilTag
            ArrayList<AprilTagDetection> tempDetections = AprilTagDetectorJNI
                    .runAprilTagDetectorSimple(nativeAprilTagPtr, greyScaleMat, tagsize, fx, fy, cx, cy);
            synchronized (detectionsLock) {
                latestDetections = tempDetections;
            }
        } catch (Exception exception) {
            lastException = exception;
            error = true;
        }

        return input;
    }
}
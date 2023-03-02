package org.firstinspires.ftc.teamcode.powerPlay.core;
/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

public class FalconTFod {
    private static final String TAG = "FalconTFod";
    private final HardwareMap hwMap;
    private final Telemetry telemetry;
    private final VuforiaLocalizer vuforiaLocalizer;
    public TFObjectDetector tFod;

    /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
     * the following 4 detectable objects
     *  0: Ball,
     *  1: Cube,
     *  2: Duck,
     *  3: Marker (duck location tape marker)
     *
     *  Two additional model assets are available which only contain a subset of the objects:
     *  FreightFrenzy_BC.tflite  0: Ball,  1: Cube
     *  FreightFrenzy_DM.tflite  0: Duck,  1: Marker
     */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    public FalconTFod(HardwareMap hardwareMap, Telemetry telemetry, VuforiaLocalizer vuforiaLocalizer) {
        hwMap = hardwareMap;
        this.telemetry = telemetry;
        this.vuforiaLocalizer = vuforiaLocalizer;
    }

    public void init() {
        int tFodMonitorViewId = hwMap.appContext.getResources().getIdentifier(
                "tFodMonitorViewId", "id", hwMap.appContext.getPackageName());
        TFObjectDetector.Parameters tFodParameters = new TFObjectDetector.Parameters(tFodMonitorViewId);
        tFodParameters.minResultConfidence = 0.8f;
        tFodParameters.isModelTensorFlow2 = true;
        tFodParameters.inputSize = 320;
        tFod = ClassFactory.getInstance().createTFObjectDetector(tFodParameters, vuforiaLocalizer);

        if (tFod != null) {
            tFod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
            tFod.activate();
            tFod.setZoom(1.0, 16.0 / 9.0);
            telemetry.addData(TAG, "Initialized");
        } else {
            telemetry.addData(TAG, "Not initialized");
        }
    }
}

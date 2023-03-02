package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@Disabled
@TeleOp(group = "TestOp")
public class ColorSensorTeleOp extends OpMode {

    /**
     * The colorSensor field will contain a reference to our color sensor hardware object
     */
    NormalizedColorSensor colorSensor;
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    private boolean switchableLight = false;
    float gain;
    float[] hsvValues;

    @Override
    public void init() {
        FalconLogger.enter();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        // You can give the sensor a gain value, will be multiplied by the sensor's raw value before the
        // normalized color values are calculated. Color sensors (especially the REV Color Sensor V3)
        // can give very low values (depending on the lighting conditions), which only use a small part
        // of the 0-1 range that is available for the red, green, and blue values. In brighter conditions,
        // you should use a smaller gain than in dark conditions. If your gain is too high, all of the
        // colors will report at or near 1, and you won't be able to determine what color you are
        // actually looking at. For this reason, it's better to err on the side of a lower gain
        // (but always greater than  or equal to 1).
        gain = 1;

        // Once per loop, we will update this hsvValues array. The first element (0) will contain the
        // hue, the second element (1) will contain the saturation, and the third element (2) will
        // contain the value. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
        // for an explanation of HSV color.
        hsvValues = new float[3];

        // Get a reference to our sensor object. It's recommended to use NormalizedColorSensor over
        // ColorSensor, because NormalizedColorSensor consistently gives values between 0 and 1, while
        // the values you get from ColorSensor are dependent on the specific sensor you're using.
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");

        // If possible, turn the light on in the beginning (it might already be on anyway,
        // we just make sure it is if we can).
        if (colorSensor instanceof SwitchableLight) {
            switchableLight = true;
            ((SwitchableLight) colorSensor).enableLight(true);
        } else {
            switchableLight = false;
        }

        // Inform the driver that initialization is complete.
        telemetry.addData("TeleOp", "Initialized");
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        // Adjust gain so that RGB values are in [0.2, 0.8] range
        float highThreshold = (float) 0.80;
        float lowThreshold = (float) 0.20;
        gain = 1;
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        float max = Math.max(colors.red, colors.green);
        max = Math.max(max, colors.blue);
        if (max > highThreshold) {
            gain = highThreshold / max;
        } else if (max < lowThreshold) {
            gain = lowThreshold / max;
        }

        colorSensor.setGain(gain);
        FalconUtils.sleep(100);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        FalconLogger.enter();
        telemetry.addData("TeleOp", "Ready");
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        // Show the elapsed game time and wheel power.
        loopTime.reset();
        telemetry.addData("Usage", "a: +gain, b: -gain, x: lightOff, y: lightOn");

        // Update the gain value if either of the A or B gamepad buttons is pressed
        if (gamepad1.a && gain < 49.995) {
            // Only increase the gain by a small amount, since this loop will occur multiple times per second.
            gain += 0.005;
        } else if (gamepad1.b && gain > 1.005) {
            // A gain of less than 1 will make the values smaller, which is not helpful.
            gain -= 0.005;
        } else if (gamepad1.x && switchableLight) {
            ((SwitchableLight) colorSensor).enableLight(false);
        } else if (gamepad1.y && switchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }

        // Show the gain value via telemetry
        telemetry.addData("Gain", gain);

        // Tell the sensor our desired gain value (normally you would do this during initialization,
        // not during the loop)
        colorSensor.setGain(gain);

        // Get the normalized colors from the sensor
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
         * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
         * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
         * for an explanation of HSV color. */

        // Update the hsvValues array by passing it to Color.colorToHSV()
        Color.colorToHSV(colors.toColor(), hsvValues);

        telemetry.addLine()
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);

        /* If this color sensor also has a distance sensor, display the measured distance.
         * Note that the reported distance is only useful at very close range, and is impacted by
         * ambient light and surface reflectivity. */
        if (colorSensor instanceof DistanceSensor) {
            telemetry.addData("Distance (cm)", "%.3f",
                    ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
        }

        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        FalconLogger.enter();
        FalconLogger.exit();
    }
}

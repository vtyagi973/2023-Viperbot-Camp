package org.firstinspires.ftc.teamcode.powerPlay.testOps;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.Assert;
import org.firstinspires.ftc.teamcode.powerPlay.core.CsvWriter;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconLogger;
import org.firstinspires.ftc.teamcode.powerPlay.core.FalconUtils;

@Disabled
@TeleOp(group = "TestOp")
public class CsvWriterTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    CsvWriter csvWriter;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        FalconLogger.enter();
        runtime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        runtime.reset();
        csvWriter = new CsvWriter("csvWriter.csv");
        Assert.assertNotNull(csvWriter, "init>csvWriter");
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        FalconLogger.enter();
        telemetry.addData("Driver Op", "Ready");
        telemetry.update();
        FalconLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FalconLogger.enter();
        loopTime.reset();
        if (gamepad1.a) {
            csvWriter.append(runtime.seconds());
            csvWriter.append(loopTime.milliseconds());
            csvWriter.flush();
            telemetry.addData("CsvWriter", "Appended data");
            FalconUtils.sleep(1000);
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
        if (csvWriter != null) {
            csvWriter.close();
        }

        FalconLogger.exit();
    }
}

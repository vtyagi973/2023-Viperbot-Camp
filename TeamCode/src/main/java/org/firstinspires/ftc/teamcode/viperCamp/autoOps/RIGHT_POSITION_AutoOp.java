package org.firstinspires.ftc.teamcode.viperCamp.autoOps;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(group = "PowerPlay", preselectTeleOp = "DriverTeleOp")
public class RIGHT_POSITION_AutoOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        new AutoOpMain().execute(this);
    }
}

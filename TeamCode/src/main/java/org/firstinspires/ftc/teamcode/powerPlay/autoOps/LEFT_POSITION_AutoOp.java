package org.firstinspires.ftc.teamcode.powerPlay.autoOps;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.powerPlay.core.Enumerations.TeamPositionEnum;

@Autonomous(group = "PowerPlay", preselectTeleOp = "DriverTeleOp")
public class LEFT_POSITION_AutoOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        new AutoOpMain().execute(this, TeamPositionEnum.LEFT);
    }
}

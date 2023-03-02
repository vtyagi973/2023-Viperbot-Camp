package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Default is not found and level 3
 */
public class VuforiaImage {
    public boolean Found;  // Set to true when a target is detected by Vuforia
    public String Name;
    public RelicRecoveryVuMark VuMark;
    public double Distance;
    public double Heading;

    public VuforiaImage() {
        Found = false;
        Name = "";
        VuMark = RelicRecoveryVuMark.UNKNOWN;
        Distance = 0;
        Heading = 0;
    }
}

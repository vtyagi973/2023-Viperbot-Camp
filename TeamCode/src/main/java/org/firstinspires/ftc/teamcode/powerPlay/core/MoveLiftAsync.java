package org.firstinspires.ftc.teamcode.powerPlay.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class MoveLiftAsync implements Runnable {
    private final LinearOpMode opMode;
    private final FalconBot robot;
    private int targetPosition;
    private boolean stopRequested;

    public MoveLiftAsync(LinearOpMode opMode, FalconBot robot) {
        this.opMode = opMode;
        this.robot = robot;
        targetPosition = FalconLift.LIFT_POSITION_SUB_STATION;
        stopRequested = false;
    }

    private synchronized int getLiftPosition() {
        return targetPosition;
    }

    public synchronized void setLIftPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }

    private synchronized boolean keepRunning() {
        return !stopRequested;
    }

    public synchronized void stopOperation() {
        stopRequested = true;
    }

    public void run() {
        while (opMode.opModeIsActive() && keepRunning()) {
            robot.falconLift.moveLift(getLiftPosition(), false);
            FalconUtils.sleep(100L);
        }
    }
}

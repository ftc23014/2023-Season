package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

public class SensorConeHuskyLensSubsystem extends Subsystem {

    private HuskyLens huskyLens;
    private final int READ_TIME = 200;

    private Deadline rateLimit;

    private int lastDetection = 0;
    private boolean hasDetected = false;

    private boolean detecting = false;

    @Override
    public void init() {
        // init huskyLens
        huskyLens = getHardwareMap().get(HuskyLens.class, "huskylens");

        // make sure huskylens doesnt die
        Deadline rateLimit = new Deadline(READ_TIME, TimeUnit.SECONDS);
        rateLimit.expire();

        if (!huskyLens.knock()) {
            telemetry().addData(">>", "Problem communicating with " + huskyLens.getDeviceName());
        } else {
            telemetry().addData(">>", "Husky Lens Ready");
        }

        // set algorithm to funny color detection yay
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
    }

    @Override
    public void periodic() {
        if (!detecting) return;
//        if (rateLimit == null) {
//            rateLimit = new Deadline(READ_TIME, TimeUnit.MILLISECONDS);
//        }

        // do some magic to make sure huskylens is not dead
//        if (!rateLimit.hasExpired()) {
//            return;
//        }
//        rateLimit.reset();

        // init list of blocks that huskylens detects
        HuskyLens.Block[] blocks = huskyLens.blocks();

        // shows number of blocks detected
        //telemetry().addData("Block count:", blocks.length);

        // show list of blocks detected
//        for (int i = 0; i < blocks.length; i++) {
//            //telemetry().addData("Block", blocks[i].toString());
//        }

        // -1 for left tape, 0 for middle tape, 1 for right tape
        if (blocks.length > 0) { // check that it detects one cone
            //telemetry().addData("Tape:", locateBlockPlacement(blocks[blocks.length - 1].x));
            lastDetection = locateBlockPlacement(blocks[blocks.length - 1].x);

            if (lastDetection == -1) {
                double ratio = ((double) blocks[blocks.length - 1].width / (double) blocks[blocks.length - 1].height);

                if (ratio > 1.5) {
                    lastDetection = 1;
                }
            }
        } else {
            lastDetection = 1;
        }

        hasDetected = true;

        telemetry().addLine("HuskyLens: " + lastDetection);
        telemetry().update();
    }

    public void setDetecting(boolean detecting) {
        this.detecting = detecting;
    }

    public int getLastDetection() {
        return lastDetection;
    }

    public boolean hasDetected() {
        return true;
    }

    private static int locateBlockPlacement(int blockX) {
        if (blockX <= 105) { // if block is between 0-100 pixels
            return -1;
        } else if (blockX > 105 && blockX <= 210) { // if block is between 100-250 pixels
            return 0;
        } else { // else
            return 1;
    }
}
}
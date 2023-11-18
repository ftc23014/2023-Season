package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

public class SensorConeHuskyLensSubsystem extends Subsystem {

    private HuskyLens huskyLens;
    private final int READ_TIME = 1;

    private Deadline rateLimit;


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
            telemetry().addData(">>", "Press start to continue");
        }

        // set algorithm to funny color detection yay
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        telemetry().update();
    }

    @Override
    public void periodic() {
        // do some magic to make sure huskylens is not dead
        if (!rateLimit.hasExpired()) {
            return;
        }
        rateLimit.reset();

        // init list of blocks that huskylens detects
        HuskyLens.Block[] blocks = huskyLens.blocks();

        // shows number of blocks detected
        telemetry().addData("Block count:", blocks.length);

        // show list of blocks detected
        for (int i = 0; i < blocks.length; i++) {
            telemetry().addData("Block", blocks[i].toString());
        }

        // -1 for left tape, 0 for middle tape, 1 for right tape
        if (blocks.length > 0) { // check that it detects one cone
            telemetry().addData("Tape:", locateBlockPlacement(blocks[-1].x));
        }

        telemetry().update();
    }

    private static int locateBlockPlacement(int blockX) {
        if (blockX < 100) { // if block is between 0-100 pixels
            return -1;
        } else if (blockX > 100 && blockX < 250) { // if block is between 100-250 pixels
            return 0;
        } else { // else
            return 1;
        }
    }
}
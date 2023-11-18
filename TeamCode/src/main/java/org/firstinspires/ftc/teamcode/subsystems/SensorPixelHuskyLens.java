package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@TeleOp(name = "Sensor: Pixel Detection (HL)", group = "Sensor")
public class SensorPixelHuskyLens extends LinearOpMode {

    private HuskyLens huskyLens;
    private final int READ_TIME = 1;

    @Override
    public void runOpMode() {

        // init huskyLens
        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");

        // make sure huskylens does die
        Deadline rateLimit = new Deadline(READ_TIME, TimeUnit.SECONDS);
        rateLimit.expire();

        if (!huskyLens.knock()) {
            telemetry.addData(">>", "Problem communicating with " + huskyLens.getDeviceName());
        } else {
            telemetry.addData(">>", "Press start to continue");
        }

        // set algorithm to funny color detection yay
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // do some magic to make sure huskylens is not dead
            if (!rateLimit.hasExpired()) {
                continue;
            }
            rateLimit.reset();

            // init list of blocks that huskylens detects
            HuskyLens.Block[] blocks = huskyLens.blocks();

            // shows number of blocks detected
            telemetry.addData("Block count:", blocks.length);

            // show list of blocks detected
            for (int i = 0; i < blocks.length; i++) {
                telemetry.addData("Block", blocks[i].toString());
            }

            // -1 for left tape, 0 for middle tape, 1 for right tape
            if (blocks.length > 0) { // check that it detects one cone
                telemetry.addData("Tape:", locateBlockPlacement(blocks[0].x));
            }

            telemetry.update();
        }
    }

    private static int locateBlockPlacement(int blockX) {
        if (blockX < 100) {
            return -1;
        } else if (blockX > 100 && blockX < 250) {
            return 0;
        } else {
            return 1;
        }
    }


}

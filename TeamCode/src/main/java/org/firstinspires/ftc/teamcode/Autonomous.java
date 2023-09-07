package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous
public class Autonomous extends LinearOpMode {

    private boolean autonomousEnabled = false;

    @Override
    public void runOpMode() throws InterruptedException {
        // -- SETUP --

        // Wait for autonomous to be enabled.
        waitForStart();

        // -- ENABLE --
        autonomousEnabled = true;
        autonomousEnable();

        // -- MAIN LOOP --

        while (opModeIsActive()) {
            autonomousLoop();
        }

        // -- DISABLE --
        autonomousEnabled = false;
    }

    /**
     * Return whether the autonomous is enabled or not.
     * @return Whether the autonomous is enabled or not.
     * */
    public boolean isAutonomousEnabled() {
        return autonomousEnabled;
    }

    public void autonomousEnable() {
        //This is called when the autonomous is enabled.
    }

    public void autonomousLoop() {
        //This is called every loop of the autonomous.
    }
}

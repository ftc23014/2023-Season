package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="backdrop side auto")
public class BackdropAutonomous extends OpMode {

    /**
     * General idea of backdrop autonomous:
     *
     * 1. Place pixel on the spike with the team object on it.
     * 2. Go to backdrop and place the pixel on the backdrop.
     *
     * */

    @Override
    public void init() {
        Autonomous.setAutonomous(Autonomous.AutonomousMode.BACKDROP_AUTONOMOUS, this).init();
    }

    @Override
    public void start() {
        Autonomous.getInstance().start();
    }

    @Override
    public void loop() {
        Autonomous.getInstance().loop();
    }

    @Override
    public void stop() {
        Autonomous.getInstance().stop();
    }
}

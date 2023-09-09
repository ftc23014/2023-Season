package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="backdrop-side-auto")
public class BackdropAutonomous extends OpMode {

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

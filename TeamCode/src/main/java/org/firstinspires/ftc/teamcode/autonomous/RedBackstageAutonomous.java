package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="pickup autonomous")
public class RedBackstageAutonomous extends OpMode  {
    @Override
    public void init() {
        Autonomous.setAutonomous(Autonomous.AutonomousMode.PICKUP_AUTONOMOUS, Autonomous.StartingSide.RED, this).init();
    }

    @Override
    public void init_loop() {
        Autonomous.getInstance().init_loop();
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

package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="pickup autonomous")
public class PickupAutonomous extends OpMode  {
    @Override
    public void init() {
        Autonomous.setAutonomous(Autonomous.AutonomousMode.PICKUP_AUTONOMOUS, this).init();
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

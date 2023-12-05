package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Red Full Backstage Auto", group="Full", preselectTeleOp="Main TeleOp")
public class RedFullBackstageAuto extends OpMode {
    @Override
    public void init() {
        Autonomous.setAutonomous(Autonomous.AutonomousMode.FULL_AUTO, Autonomous.StartingSide.RED, this).init();
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

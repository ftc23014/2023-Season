package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.teamcode.subsystems.SensorConeHuskyLensSubsystem;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Red Basic Park Backstage Autonomous", group="Simple", preselectTeleOp="Main TeleOp")
public class RedBackstageAutonomous extends OpMode  {
    private DistanceSensor distanceSensor;

    @Override
    public void init() {
        Autonomous.setAutonomous(Autonomous.AutonomousMode.BASIC_AUTO, Autonomous.StartingSide.RED, this).init();
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
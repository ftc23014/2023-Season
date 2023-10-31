package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.teamcode.subsystems.MotorTestSubsystem;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name= "Motor Test", group="Motor")
public class LinearSlideOpmode extends OpMode {

    private MotorTestSubsystem motorTestSubsystem;
    @Override
    public void init() {
        motorTestSubsystem = new MotorTestSubsystem();
        motorTestSubsystem.setGamepad(gamepad1);
        Subsystems.onInit(hardwareMap);
    }

    @Override
    public void loop() {
        Subsystems.periodic();


    }

    @Override
    public void stop() {
        Subsystems.onDisable();
    }
}
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LinearSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.MotorTestSubsystem;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name= "Linear Slide", group="Testing")
public class LinearSlideOpmode extends OpMode {

    private MotorTestSubsystem motorTestSubsystem;
    private LinearSlideSubsystem linearSlideSubsystem;

    private IntakeSubsystem intakeSubsystem;
    @Override
    public void init() {
        /*motorTestSubsystem = new MotorTestSubsystem();
        motorTestSubsystem.setGamepad(gamepad1);
        Subsystems.onInit(this);
        */
         linearSlideSubsystem = new LinearSlideSubsystem();
         linearSlideSubsystem.setGamepad(gamepad1);
         intakeSubsystem = new IntakeSubsystem();
         intakeSubsystem.setGamepad(gamepad1);

         Subsystems.onInit(this);
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
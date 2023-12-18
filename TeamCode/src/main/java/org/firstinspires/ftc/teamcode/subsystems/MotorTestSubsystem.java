package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */


public class MotorTestSubsystem extends Subsystem {
    DcMotor motor;
    Gamepad gamepad;
    Servo servo;
    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }


    @Override
    public void init() {


        motor = getHardwareMap().dcMotor.get("intake_motor");
        servo = getHardwareMap().servo.get("Lock");



        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {

        if (gamepad.x && servo.getPosition() >= 0.399 && servo.getPosition() <= 0.401) {
            servo.setPosition(0.6);
        } else if (gamepad.x && servo.getPosition() >= 0.599 && servo.getPosition() <= 0.601) {
            servo.setPosition(0.4);
        }

        telemetry().addData("Servo pos:", servo.getPosition());

        if (gamepad.a) {
            motor.setPower(1);
        }
        else if (gamepad.b) {
            motor.setPower(-1);
        }
        else {
            motor.setPower(0);

        }



    }

    @Override
    public void onDisable() {
        motor.setPower(0);
    }
}

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
    Gamepad gamepad;
    Servo servo;
    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }


    @Override
    public void init() {


        servo = getHardwareMap().servo.get("Lock");



        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {


        if (gamepad.left_stick_y > 0.02) {
            servo.setPosition(gamepad.left_stick_y);
        } else if (gamepad.left_stick_y < -0.02) {
            servo.setPosition(-gamepad.left_stick_y);
        }

        telemetry().addData("Servo pos:", servo.getPosition());



    }

    @Override
    public void onDisable() {
        servo.setPosition(0.0);
    }
}

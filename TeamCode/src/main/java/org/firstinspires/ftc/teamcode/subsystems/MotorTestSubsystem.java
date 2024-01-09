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

    Servo servo1;
    Servo servo2;
    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }


    @Override
    public void init() {


        servo1 = getHardwareMap().servo.get("bucket_pusher");
        servo2 = getHardwareMap().servo.get("bucket_flipper");



        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {


        if (gamepad.left_stick_y > 0.02) {
            servo1.setPosition(gamepad.left_stick_y);
        }

        if (gamepad.right_stick_y > 0.02) {
            servo2.setPosition(gamepad.right_stick_y);
        }


        telemetry().addData("Servo1 pos:", servo1.getPosition());
        telemetry().addData("Servo2 pos:", servo2.getPosition());



    }

    @Override
    public void onDisable() {
        servo1.setPosition(0.0);
        servo2.setPosition(0.0);
    }
}

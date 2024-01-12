package org.firstinspires.ftc.teamcode.subsystems;

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

    private double currentPosition1 = 0.5;
    private double currentPosition2 = 0.0;
    private boolean firstRun = true;

    private boolean lastAState = false;
    private boolean lastYState = false;

    @Override
    public void periodic() {
        if (firstRun) {
            try {
                currentPosition1 = servo1.getPosition();
            } catch (Exception ignored) {}
            try {
                currentPosition2 = servo2.getPosition();
            } catch (Exception ignored) {}

            firstRun = false;
        }

        if (gamepad.y && !lastYState) {
            currentPosition1 += gamepad.left_bumper ? 0.01 : (gamepad.right_bumper ? 0.001 : 0.05);
            servo1.setPosition(currentPosition1);
        }

        if (gamepad.a && !lastAState) {
            currentPosition1 -= gamepad.left_bumper ? 0.01 : (gamepad.right_bumper ? 0.001 : 0.05);
            servo1.setPosition(currentPosition1);
        }

        telemetry().addData("Servo1 pos:", servo1.getPosition());
        telemetry().addData("Servo1 set to: ", currentPosition1);
        telemetry().addData("Servo2 pos:", servo2.getPosition());
        telemetry().addData("Servo2 set to: ", currentPosition2);

        lastAState = gamepad.a;
        lastYState = gamepad.y;
    }

    @Override
    public void onDisable() {
        servo1.setPosition(0.0);
        servo2.setPosition(0.0);
    }
}

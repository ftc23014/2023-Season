package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class MotorTestSubsystem extends Subsystem {
    DcMotor motor;
    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }


    @Override
    public void init() {


        motor = getHardwareMap().dcMotor.get("intake_motor");



        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {

        if (gamepad.a) {
            motor.setPower(0.5);
        }
        else if (gamepad.b) {
            motor.setPower(-0.5);
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

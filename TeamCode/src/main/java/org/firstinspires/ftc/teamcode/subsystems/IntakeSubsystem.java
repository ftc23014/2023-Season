package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class IntakeSubsystem extends Subsystem {
    DcMotor motor;

    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public void init() {
        motor = getHardwareMap().get(DcMotor.class, "intake_motor");
    }

    @Override
    public void periodic() {
//        if (gamepad.dpad_up) {
//            motor.setPower(0.5);
//
//        }
//        else if (gamepad.dpad_down) {
//            motor.setPower(-0.5);
//
//
//        }
//        else {
//            motor.setPower(0);
//
//
//
//        }
    }

    @Override
    public void onDisable() {

        motor.setPower(0);
    }
}

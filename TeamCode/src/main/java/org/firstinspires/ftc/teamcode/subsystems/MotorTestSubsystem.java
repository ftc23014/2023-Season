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

    @Override
    public void init() {

        motor = getHardwareMap().get(DcMotor.class, "motor");
        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {
        motor.setPower(1);
    }

    @Override
    public void onDisable() {
        motor.setPower(0);
    }
}

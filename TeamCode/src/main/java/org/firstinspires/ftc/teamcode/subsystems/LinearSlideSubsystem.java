package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class LinearSlideSubsystem extends Subsystem {
    DcMotor motor1;
    DcMotor motor2;

    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }


    @Override
    public void init() {


        motor1 = getHardwareMap().dcMotor.get("Linear_Motor1");
        motor2 = getHardwareMap().dcMotor.get("Linear_Motor2");



        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {

        if (gamepad.a) {
            motor1.setPower(0.5);
            motor2.setPower(0.5);

        }
        else if (gamepad.b) {
            motor1.setPower(-0.5);
            motor2.setPower(-0.5);

        }
        else {
            motor1.setPower(0);
            motor2.setPower(0);


        }

    }

    @Override
    public void onDisable() {
        motor1.setPower(0);
        motor2.setPower(0);

    }
}

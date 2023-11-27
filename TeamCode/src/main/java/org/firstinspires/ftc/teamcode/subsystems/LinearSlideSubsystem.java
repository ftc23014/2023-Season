package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcontroller.external.samples.SensorMRGyro;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class LinearSlideSubsystem extends Subsystem {
    DcMotor motor1;
    DcMotor motor2;
    Servo spatula;

    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }


    @Override
    public void init() {


        motor1 = getHardwareMap().dcMotor.get("Linear_Motor1");
        motor2 = getHardwareMap().dcMotor.get("Linear_Motor2");
        spatula = getHardwareMap().servo.get("Spatula");



        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void periodic() {

//        if (gamepad.a) {
//            motor1.setPower(0.5);
//            motor2.setPower(-0.5);
//
//        }
//        else if (gamepad.b) {
//            motor1.setPower(-0.5);
//            motor2.setPower(0.5);
//
//        }
//        else if (gamepad.y) {
//            spatula.setPosition(0.6);
//
//        }
//        else if (gamepad.x) {
//            spatula.setPosition(0);
//        }
//        else {
//            motor1.setPower(0);
//            motor2.setPower(0);
//
//        }

        //telemetry().addData("Spatula servo pos: ", String.format("%.3f", spatula.getPosition()));

    }

    @Override
    public void onDisable() {
        motor1.setPower(0);
        motor2.setPower(0);

    }
}

package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcontroller.external.samples.SensorMRGyro;

/**
 * Code for moving a linear slide on our FTC robot with a and b buttons and flipping the spatula with x and y
 */
public class LinearSlideSubsystem extends Subsystem {
    DcMotor motor1;  // Declaration of the first DcMotor
    DcMotor motor2;  // Declaration of the second DcMotor
    Servo spatula;   // Declaration of the Servo

    Gamepad gamepad;  // Declaration of the Gamepad

    // Method to set the Gamepad for the subsystem
    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    // Method called when the subsystem is initialized
    @Override
    public void init() {
        // Initialize the first motor from the hardware map
        motor1 = getHardwareMap().dcMotor.get("Linear_Motor1");
        // Initialize the second motor from the hardware map
        motor2 = getHardwareMap().dcMotor.get("Linear_Motor2");
        // Initialize the servo from the hardware map
        spatula = getHardwareMap().servo.get("Spatula");

        //motor.setDirection(DcMotor.Direction.REVERSE); for direction
        //motor.setDirection(DcMotor.Direction.FORWARD);
    }

    // Method called periodically to perform actions
    @Override
    public void periodic() {
        // Check if the 'A' button is pressed on the gamepad
        if (gamepad.a) {
            // Set power to the first motor
            motor1.setPower(0.5);
            // Set power to the second motor
            motor2.setPower(-0.5);
        }
        // Check if the 'B' button is pressed on the gamepad
        else if (gamepad.b) {
            // Set power to the first motor
            motor1.setPower(-0.5);
            // Set power to the second motor
            motor2.setPower(0.5);
        }
        // Check if the 'Y' button is pressed on the gamepad
        else if (gamepad.y) {
            // Set the position of the servo
            spatula.setPosition(1);
        }
        // Check if the 'X' button is pressed on the gamepad
        else if (gamepad.x) {
            // Set the position of the servo
            spatula.setPosition(0.2);
        }
        // If none of the buttons are pressed
        else {
            // Stop the motors
            motor1.setPower(0);
            motor2.setPower(0);
        }

        // Add telemetry data for the position of the servo
        telemetry().addData("Spatula servo pos: ", String.format("%.3f", spatula.getPosition()));
    }

    // Method called when the subsystem is disabled
    @Override
    public void onDisable() {
        // Stop the motors
        motor1.setPower(0);
        motor2.setPower(0);
    }
}
package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Hang extends Subsystem {
    private DcMotor hangMotor;
    private Servo hangServo;

    public Hang() {
        super();
    }

    @Override
    public void init() {
        hangMotor = getHardwareMap().dcMotor.get("hang_motor");
        hangServo = getHardwareMap().servo.get("hang_servo");
    }

    public Command setDeploy() {
        return new InstantCommand(this::hangUp);
    }

    public void hangUp() {
        hangServo.setPosition(0.4);
    }

    public void pushMotorUp() {
        hangMotor.setPower(1);
    }

    public void retractMotor() {
        hangMotor.setPower(-1);
    }

    public void setMotorSpeed(double n) {
        hangMotor.setPower(n);
    }

    public Command setRetract() {
        return new InstantCommand(this::hangDown);
    }

    public void hangDown() {
        hangServo.setPosition(0.63);
    }

    public Command stopCommand() {
        return new InstantCommand(this::stop);
    }

    public void stop() {
        hangMotor.setPower(0);
        hangServo.setPosition(0);
    }

    @Override
    public void periodic() {
    }
}

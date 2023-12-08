package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Hang extends Subsystem { // test
    private DcMotor hangMotor;

    public Hang() {
        super();
    }

    @Override
    public void init() {
        hangMotor = getHardwareMap().dcMotor.get("hangMotor");
    }

    public Command hangUpCommand(double power) {
        return new InstantCommand(() -> hangUp(power));
    }

    public void hangUp(double power) {
        hangMotor.setPower(-power);
    }

    public Command hangDownCommand(double power) {
        return new InstantCommand(() -> hangDown(power));
    }

    public void hangDown(double power) {
        hangMotor.setPower(-power);
    }

    public Command stopCommand() {
        return new InstantCommand(this::stop);
    }

    public void stop() {
        hangMotor.setPower(0);
    }

    @Override
    public void periodic() {
    }
}

package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.lib.systems.Subsystem;

public class IntakeSubsystem extends Subsystem {
    DcMotor motor;

    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public void init() {
    }

    @Override
    public void periodic() {

    }

    @Override
    public void onDisable() {

        motor.setPower(0);
    }
}

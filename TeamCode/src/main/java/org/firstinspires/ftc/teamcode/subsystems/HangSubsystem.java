package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class HangSubsystem extends Subsystem {
    DcMotor hangMotor;

    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public void init() {
        hangMotor = getHardwareMap().get(DcMotor.class, "hangMotor");
    }

    @Override
    public void periodic() {
        if (gamepad.right_trigger > 0.05) {
            hangMotor.setPower(1);

        }
        else if (gamepad.left_trigger > 0.05) {
            hangMotor.setPower(-1);
        }
        else {
            hangMotor.setPower(0);
        }
    }

    @Override
    public void onDisable() {
        hangMotor.setPower(0);
    }
}

package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class IntakeSubsystem extends Subsystem {
    DcMotor motor;

    @Override
    public void init() {
        //motor = getHardwareMap().get(DcMotor.class, "motor");
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

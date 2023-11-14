package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.teamcode.TeleOp;

public class IntakeMechanism extends Subsystem {
    /**
     * TODO:
     * intake
     * move motor
     * yeah idk
     */

    private DcMotor intakeMotor;

    public IntakeMechanism() {
        super();
    }

    @Override
    public void init() {
        intakeMotor = getHardwareMap().get(DcMotor.class, "intake_motor");
    }

    public void intake(double power) {
        intakeMotor.setPower(power);
    }

    public void outtake(double power) {
        intake(-power);
    }

    public void stop() {
        intake(0);
    }

    @Override
    public void periodic() {
        if (TeleOp.hasInstance()) {
            if (gamepad().right_bumper) {
                intake(1);
            } else if (gamepad().left_bumper) {
                intake(-1);
            } else {
                stop();
            }
        }
    }
}

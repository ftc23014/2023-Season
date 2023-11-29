package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;
import org.firstinspires.ftc.teamcode.TeleOp;

public class Intake extends Subsystem {
    /**
     * TODO:
     * intake
     * move motor
     * yeah idk
     */

    private DcMotor intakeMotor;

    public Intake() {
        super();
    }

    @Override
    public void init() {
        intakeMotor = getHardwareMap().get(DcMotor.class, "intake_motor");
    }

    public Command intake_cmd(double power) {
        return new InstantCommand(() -> intake(power));
    }

    public void intake(double power) {
        intakeMotor.setPower(-power);
    }

    public Command outtake_cmd(final double power) {
        return new InstantCommand(() -> outtake(power));
    }

    public void outtake(double power) {
        intake(-power);
    }

    public Command stop_cmd() {
        return new InstantCommand(this::stop);
    }

    public void stop() {
        intake(0);
    }

    @Override
    public void periodic() {
//        if (TeleOp.hasInstance()) {
//            if (gamepad().right_bumper) {
//                intake(1);
//            } else if (gamepad().left_bumper) {
//                intake(-1);
//            } else {
//                stop();
//            }
//        }
    }
}

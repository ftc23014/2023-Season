package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;
import org.firstinspires.ftc.teamcode.TeleOp;

public class Intake extends Subsystem {

    private CRServo bootKickerServo;
    private Servo deployKickerServo;
    private CRServo compliantWheelsServo;


    public Intake() {
        super();
    }

    @Override
    public void init() {
        bootKickerServo = getHardwareMap().get(CRServo.class, "boot_kicker");
        deployKickerServo = getHardwareMap().get(Servo.class, "deploy_kicker");
        compliantWheelsServo = getHardwareMap().get(CRServo.class, "compliant_wheels");
    }

    public Command deploy_kicker() {
        return new InstantCommand(this::deploy_kicker_func);
    }

    public void deploy_kicker_func() {
        deployKickerServo.setPosition(1/* deploy position */);
    }

    public Command retract_kicker() {
        return new InstantCommand(this::retract_kicker_func);
    }

    public void retract_kicker_func() {
        deployKickerServo.setPosition(0.575/* retract position */);
    }

    public Command intake_boot_kicker() {
        return new InstantCommand(this::intake_boot_kicker_func);
    }

    public void intake_boot_kicker_func() {
        bootKickerServo.setPower(0.8);
        compliantWheelsServo.setPower(1);
    }

    public Command outtake_boot_kicker() {
        return new InstantCommand(this::outtake_boot_kicker_func);
    }

    public void outtake_boot_kicker_func() {
        bootKickerServo.setPower(-0.8);
        compliantWheelsServo.setPower(-1);
    }

    public Command stop_cmd() {
        return new InstantCommand(this::stop);
    }

    public void stop() {
        bootKickerServo.setPower(0);
        compliantWheelsServo.setPower(0);
    }

    @Override
    public void periodic() {
    }
}

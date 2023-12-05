package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LinearSlideSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name= "MecanumDriveOpMode", group="TeleOp")
public class MecanumDriveOpMode extends OpMode {

    private MecanumDriveSubsystem mecanumDriveSubsystem;
    //private LinearSlideSubsystem linearSlideSubsystem;

    //private IntakeSubsystem intakeSubsystem;

    @Override
    public void init() {
        mecanumDriveSubsystem = new MecanumDriveSubsystem();
//        intakeSubsystem = new IntakeSubsystem();
//        intakeSubsystem.setGamepad(gamepad1);
//        linearSlideSubsystem = new LinearSlideSubsystem();
//        linearSlideSubsystem.setGamepad(gamepad1);

        Robot.init();

        Subsystems.onInit(this);
    }

    @Override
    public void loop() {
        Subsystems.periodic();
        // Capture the gamepad input
        double drive = -gamepad1.left_stick_y; // Negated because up is usually negative
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        // Drive the robot using the gamepad inputs
        mecanumDriveSubsystem.driveMotors(new Translation2d(strafe, drive), rotate);

    }
}

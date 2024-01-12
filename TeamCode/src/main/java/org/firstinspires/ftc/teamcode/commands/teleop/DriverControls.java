package org.firstinspires.ftc.teamcode.commands.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.odometry.MecanumOdometry;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.commands.AprilTagAutoMove;
import org.firstinspires.ftc.teamcode.commands.TurnToCommand;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.vision.VisionSubsystem;

import java.util.Arrays;

public class DriverControls extends Command {
    private MecanumDriveSubsystem m_mecanumDriveSubsystem;
    private Gamepad gamepad1;
    private Intake m_intakeSubsystem;
    private TurnToCommand m_turnToCommand;
    private AprilTagAutoMove m_aprilTagAutoMove;

    public DriverControls(Gamepad gamepad1, MecanumDriveSubsystem driveSubsystem, Intake intake) {
        super();

        this.gamepad1 = gamepad1;

        m_mecanumDriveSubsystem = driveSubsystem;
        m_intakeSubsystem = intake;

        m_turnToCommand = new TurnToCommand(Rotation2d.fromDegrees(-90), m_mecanumDriveSubsystem);
    }

    @Override
    public void init() {
        m_turnToCommand.init();
    }

    @Override
    public void execute() {


        if (gamepad1.x) {
            m_intakeSubsystem.deploy_kicker_func();
        } else if (gamepad1.y) {
            m_intakeSubsystem.retract_kicker_func();
        }

        if (gamepad1.right_bumper) {
            m_intakeSubsystem.intake_boot_kicker_func();
        } else if (gamepad1.left_bumper) {
            m_intakeSubsystem.outtake_boot_kicker_func();
        } else {
            m_intakeSubsystem.stop();
        }

        if (!gamepad1.b && !gamepad1.a) {
            if (Math.abs(gamepad1.left_stick_x) > 0.05 || Math.abs(gamepad1.left_stick_y) > 0.05 || Math.abs(gamepad1.right_stick_x) > 0.05) {
                m_mecanumDriveSubsystem.drive(
                        new Translation2d(
                                gamepad1.left_stick_x,
                                gamepad1.left_stick_y
                        ).scalar(m_mecanumDriveSubsystem.getVelocityLimit().get(Unit.Type.Meters)),
                        Rotation2d.fromDegrees(-Math.pow(gamepad1.right_stick_x, 9)),
                        true,
                        true
                );
            } else {
                m_mecanumDriveSubsystem.stop_motors();
            }

            m_turnToCommand = null;
        } else if (gamepad1.b) {
            if (m_turnToCommand == null) {
                m_turnToCommand = new TurnToCommand(Rotation2d.fromDegrees(-90), m_mecanumDriveSubsystem);
                m_turnToCommand.init();
            }
            if (!m_turnToCommand.hasFinished()) {
                m_turnToCommand.execute();
            }
        } else if (gamepad1.a) {
            if (m_aprilTagAutoMove == null) {
                m_aprilTagAutoMove = new AprilTagAutoMove(VisionSubsystem.getInstance(), AprilTagAutoMove.Side.Blue, AprilTagAutoMove.Position.Center);
                m_aprilTagAutoMove.init();
            } else if (!m_aprilTagAutoMove.hasFinished()) {
                m_aprilTagAutoMove.execute();
            }
        }

        MecanumOdometry odometry = m_mecanumDriveSubsystem.getOdometry();

//        telemetry().addLine("odo X:" + odometry.getPosition().getX());
//        telemetry().addLine("odo Y:" + odometry.getPosition().getY());
//
//        telemetry().addLine("odo rotation:" + odometry.getRotation().getDegrees());
//        telemetry().addLine("left: " +
//                odometry.convertFromEncoderTicks(m_mecanumDriveSubsystem.getOdoPositions()[0]));
//        telemetry().addLine("right: " +
//                odometry.convertFromEncoderTicks(m_mecanumDriveSubsystem.getOdoPositions()[1]));
//        telemetry().addLine("center: " +
//                odometry.convertFromEncoderTicks(m_mecanumDriveSubsystem.getOdoPositions()[2]));
//        telemetry().addData("front left pos: ", frontLeft.getCurrentPosition());
//        telemetry().addData("back right pos: ", backRight.getCurrentPosition());
//        telemetry().addData("front right pos: ", frontRight.getCurrentPosition());

        //telemetry().update();

    }

    @Override
    public boolean hasFinished() {
        return false;
    }
}

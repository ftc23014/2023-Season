package org.firstinspires.ftc.teamcode.commands.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.odometry.MecanumOdometry;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.commands.TurnToCommand;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

import java.util.Arrays;

public class DriverControls extends Command {
    private MecanumDriveSubsystem m_mecanumDriveSubsystem;
    private Gamepad gamepad1;

    private TurnToCommand m_turnToCommand;

    public DriverControls(Gamepad gamepad1, MecanumDriveSubsystem driveSubsystem) {
        super();

        this.gamepad1 = gamepad1;

        m_mecanumDriveSubsystem = driveSubsystem;

        m_turnToCommand = new TurnToCommand(Rotation2d.fromDegrees(90), m_mecanumDriveSubsystem);
    }

    @Override
    public void init() {
        m_turnToCommand.init();
    }

    @Override
    public void execute() {
        if (!gamepad1.b) {
            if (Math.abs(gamepad1.left_stick_x) > 0.05 || Math.abs(gamepad1.left_stick_y) > 0.05 || Math.abs(gamepad1.right_stick_x) > 0.05) {
                m_mecanumDriveSubsystem.drive(
                        new Translation2d(
                                gamepad1.left_stick_x,
                                gamepad1.left_stick_y
                        ).scalar(m_mecanumDriveSubsystem.getVelocityLimit().get(Unit.Type.Meters)),
                        Rotation2d.fromDegrees(-Math.pow(gamepad1.right_stick_x, 9) * 20),
                        true,
                        true
                );
            } else {
                m_mecanumDriveSubsystem.stop_motors();
            }
        } else {
            m_turnToCommand.execute();
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

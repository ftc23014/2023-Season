package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.WPIPIDController;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveSubsystem;

public class TurnToCommand extends Command {
    private Rotation2d m_rotationGoal;
    private WPIPIDController m_rotationController;

    private MecanumDriveSubsystem m_driveSubsystem;

    public TurnToCommand(Rotation2d rotationGoal) {
        m_rotationGoal = rotationGoal;
        m_rotationController = new WPIPIDController(0.2, 0, 0);

        m_rotationController.enableContinuousInput(0, 360);

        m_driveSubsystem = MecanumDriveSubsystem.instance();
    }

    @Override
    public void execute() {
        double power = m_rotationController.calculate(m_driveSubsystem.getAngle().getDegrees(), m_rotationGoal.getDegrees());
        m_driveSubsystem.drive(
                new Translation2d(0,0),
                Rotation2d.fromDegrees(power),
                true,
                true
        );
    }
}

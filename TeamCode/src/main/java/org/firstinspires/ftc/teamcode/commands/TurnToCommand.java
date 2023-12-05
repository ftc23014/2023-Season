package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.WPIPIDController;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

public class TurnToCommand extends Command {
    private Rotation2d m_rotationGoal;
    private WPIPIDController m_rotationController;

    private MecanumDriveSubsystem m_driveSubsystem;

    private final double maxPower = 1;

    public TurnToCommand(Rotation2d rotationGoal, MecanumDriveSubsystem driveSubsystem) {
        m_rotationGoal = rotationGoal;
        m_rotationController = new WPIPIDController(0.005, 0, 0);
        m_rotationController.setTolerance(2);

        m_rotationController.enableContinuousInput(0, 360);

        m_driveSubsystem = driveSubsystem;
    }

    @Override
    public void execute() {
        double power = m_rotationController.calculate(m_driveSubsystem.getAngle().getAbsoluteDegrees(), m_rotationGoal.getAbsoluteDegrees());

//        if (Math.abs(power) > maxPower) {
//            power = Math.signum(power) * maxPower;
//        }

        telemetry().addLine("TTPower: " + power + ", CURRENT: " + m_driveSubsystem.getAngle().getAbsoluteDegrees() + ", GOAL: " + m_rotationGoal.getAbsoluteDegrees() + ", ERROR: " + m_rotationController.getPositionError());

        m_driveSubsystem.drive(
                new Translation2d(0,0),
                Rotation2d.fromDegrees(power),
                true,
                true
        );

        //telemetry().update();
    }

    @Override
    public boolean hasFinished() {
        if (m_rotationController.atSetpoint()) {
            m_driveSubsystem.stop_motors();
        }

        return m_rotationController.atSetpoint();
    }
}

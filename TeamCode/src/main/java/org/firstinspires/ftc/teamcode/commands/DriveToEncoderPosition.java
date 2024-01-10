package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

public class DriveToEncoderPosition extends Command {
    private Translation2d position;
    private PIDController xController;
    private PIDController yController;

    private AutonomousConstants constants;
    private MecanumDriveSubsystem driveSubsystem;

    private Unit distanceThreshold;

    public DriveToEncoderPosition(Translation2d position, PIDController xcontroller, PIDController ycontroller, Unit distanceThreshold) {
        this.position = position;
        this.xController = xcontroller;
        this.yController = ycontroller;
        this.constants = Constants.Autonomous.autonomousConstants;
        this.driveSubsystem = MecanumDriveSubsystem.instance();
        this.distanceThreshold = distanceThreshold;
    }

    @Override
    public void init() {
        xController.reset();
        yController.reset();
    }

    @Override
    public void execute() {
        Pose2d currentPose = driveSubsystem.getPosition();
        Unit maxSpeed = constants.getMaxSpeed();

        double xSpeed = xController.calculate(currentPose.getX(), position.getX());
        double ySpeed = yController.calculate(currentPose.getY(), position.getY());

        xSpeed = Math.abs(xSpeed) > maxSpeed.get(Unit.Type.Meters) ? maxSpeed.get(Unit.Type.Meters) * Math.signum(maxSpeed.get(Unit.Type.Meters)) : xSpeed;
        ySpeed = Math.abs(ySpeed) > maxSpeed.get(Unit.Type.Meters) ? maxSpeed.get(Unit.Type.Meters) * Math.signum(maxSpeed.get(Unit.Type.Meters)) : ySpeed;

        driveSubsystem.drive(new Translation2d(
            xSpeed,
            ySpeed
        ), Rotation2d.zero(), true, constants.getOpenLoop());
    }

    @Override
    public boolean hasFinished() {
        boolean atPoint = driveSubsystem.getPosition().getPosition().distance(position) < distanceThreshold.get(Unit.Type.Meters);

        if (atPoint) {
            driveSubsystem.stop_motors();
        }

        return atPoint;
    }
}

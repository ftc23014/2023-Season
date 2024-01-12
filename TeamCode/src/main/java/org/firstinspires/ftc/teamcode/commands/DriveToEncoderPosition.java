package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

import static org.firstinspires.ftc.teamcode.Constants.Side.LEFT_BLUE;
import static org.firstinspires.ftc.teamcode.Constants.currentSide;

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
//        xController.reset();
//        yController.reset();
    }

    @Override
    public void execute() {
        double dist = driveSubsystem.getPosition().getPosition().distance(position);

        if (dist < distanceThreshold.get(Unit.Type.Meters)) {
            //telemetry().addLine("at point!");
            return;
        }

        Pose2d currentPose = driveSubsystem.getPosition();
        Unit maxSpeed = constants.getMaxSpeed();

        double xSpeed = xController.calculate(
                currentPose.getPosition().getX(),
                position.getX()
        );

        double ySpeed = yController.calculate(
                currentPose.getPosition().getY(),
                position.getY()
        );

        if (Math.abs(position.getX() - currentPose.getPosition().getX()) > distanceThreshold.get(Unit.Type.Meters)) {
            xSpeed = constants.getMinSpeed().get(Unit.Type.Meters) * Math.signum(xSpeed);
        }

        if (Math.abs(position.getY() - currentPose.getPosition().getY()) > distanceThreshold.get(Unit.Type.Meters)) {
            ySpeed = constants.getMinSpeed().get(Unit.Type.Meters) * Math.signum(ySpeed);
        }

        //figure out the velocities

        Translation2d velocity = new Translation2d(
                Math.abs(xSpeed) > maxSpeed.get(Unit.Type.Meters) ? maxSpeed.get(Unit.Type.Meters) * Math.signum(xSpeed) : xSpeed,
                Math.abs(ySpeed) > maxSpeed.get(Unit.Type.Meters) ? maxSpeed.get(Unit.Type.Meters) * Math.signum(ySpeed) : ySpeed
        ).rotateBy(Rotation2d.fromDegrees(currentSide == LEFT_BLUE ? -90 : 90));

        driveSubsystem.drive(
                velocity,
                Rotation2d.zero(),
                true,
                true
        );

//        telemetry().addLine("<" + xSpeed + ", " + ySpeed + ">: dist away: " + dist);
//
//        System.out.println("<" + xSpeed + ", " + ySpeed + ">, dist " + dist);
        //System.out.println("To: ( " + position.getX() + " , " + position.getY() + " ) from ( " + currentPose.getPosition().getX() + " , " + currentPose.getPosition().getY() + " )");
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

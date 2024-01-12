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

    private double minSpeed = 0.2;

    public DriveToEncoderPosition(Translation2d position, PIDController xcontroller, PIDController ycontroller, Unit distanceThreshold) {
        this.position = position;
        this.xController = xcontroller;
        this.yController = ycontroller;
        this.constants = Constants.Autonomous.autonomousConstants;
        this.driveSubsystem = MecanumDriveSubsystem.instance();
        this.distanceThreshold = distanceThreshold;
    }

    public DriveToEncoderPosition setmin(double min) {
        minSpeed = min;
        return this;
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

        Rotation2d angleTo = Rotation2d.fromRadians(
                Math.atan2(
                        position.getY() - currentPose.getPosition().getY(),
                        position.getX() - currentPose.getPosition().getX()
                )
        );

        Translation2d velocities = new Translation2d(
                dist * angleTo.getCos(),
                dist * angleTo.getSin()
        );

        if (Math.abs(velocities.getX()) > maxSpeed.get(Unit.Type.Meters)) {
            velocities = new Translation2d(
                    maxSpeed.get(Unit.Type.Meters) * Math.signum(velocities.getX()),
                    velocities.getY()
            );
        }

        if (Math.abs(velocities.getY()) > maxSpeed.get(Unit.Type.Meters)) {
            velocities = new Translation2d(
                    velocities.getX(),
                    maxSpeed.get(Unit.Type.Meters) * Math.signum(velocities.getY())
            );
        }

        double minSpeed = this.minSpeed;

        if (Math.abs(velocities.getX()) < minSpeed) {
            velocities = new Translation2d(
                    minSpeed * Math.signum(velocities.getX()),
                    velocities.getY()
            );
        }

        if (Math.abs(velocities.getY()) < minSpeed) {
            velocities = new Translation2d(
                    velocities.getX(),
                    minSpeed * Math.signum(velocities.getY())
            );
        }

        driveSubsystem.drive(
                velocities.rotateBy(Rotation2d.fromDegrees(currentSide == LEFT_BLUE ? -90 : 90)),
                Rotation2d.zero(),
                true,
                true
        );

//        double xSpeed = xController.calculate(
//                currentPose.getPosition().getX(),
//                position.getX()
//        );
//
//        double ySpeed = yController.calculate(
//                currentPose.getPosition().getY(),
//                position.getY()
//        );
//
//        if (Math.abs(position.getX() - currentPose.getPosition().getX()) > distanceThreshold.get(Unit.Type.Meters)) {
//            if (Math.abs(xSpeed) < constants.getMinSpeed().get(Unit.Type.Meters)) {
//                xSpeed = 0.3 * Math.signum(xSpeed);
//            }
//        }
//
//        if (Math.abs(position.getY() - currentPose.getPosition().getY()) > distanceThreshold.get(Unit.Type.Meters)) {
//            if (Math.abs(ySpeed) < constants.getMinSpeed().get(Unit.Type.Meters)) {
//                ySpeed = 0.3 * Math.signum(ySpeed);
//            }
//        }
//
//        //figure out the velocities
//
//        Translation2d velocity = new Translation2d(
//                Math.abs(xSpeed) > maxSpeed.get(Unit.Type.Meters) ? maxSpeed.get(Unit.Type.Meters) * Math.signum(xSpeed) : xSpeed,
//                Math.abs(ySpeed) > maxSpeed.get(Unit.Type.Meters) ? maxSpeed.get(Unit.Type.Meters) * Math.signum(ySpeed) : ySpeed
//        ).rotateBy(Rotation2d.fromDegrees(currentSide == LEFT_BLUE ? -90 : 90));
//
//        driveSubsystem.drive(
//                velocity,
//                Rotation2d.zero(),
//                true,
//                true
//        );

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

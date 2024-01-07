package org.firstinspires.ftc.lib.math;

public class Physics {
    /***
     * Calculates the motion of the robot given the parameters accounting for centripetal force
     * @param centripetalForce The centripetal force of the robot (in N)
     * @param desiredVelocity The desired velocity of the robot (in m/s)
     * @return The motion of the robot as a {@link Cartesian2d} acting as a vector.
     */
    public static Translation2d calculateRobotMotion(double centripetalForce, Translation2d desiredVelocity) {
        Cartesian2d desiredVelocityVector = desiredVelocity.toCartesian2d();
        Cartesian2d centripetalForceVector = new Cartesian2d(
            Rotation2d.fromRadians(desiredVelocityVector.getRadius() + (centripetalForce > 0 ? Math.PI / 2 : -Math.PI / 2)),
            Math.abs(centripetalForce)
        );

        Translation2d newNormForce = new Translation2d(
            centripetalForceVector.getRadius(),
            desiredVelocityVector.getRadius() * Math.sin(desiredVelocityVector.getRotation().getRadians() - centripetalForceVector.getRotation().getRadians())
        );

        double magnitude = newNormForce.norm();
        double angle = Math.atan2(newNormForce.getY(), newNormForce.getX());

        return new Translation2d(
            magnitude * Math.cos(angle + centripetalForceVector.getRotation().getRadians()),
            magnitude * Math.sin(angle + centripetalForceVector.getRotation().getRadians())
        );
    }
}

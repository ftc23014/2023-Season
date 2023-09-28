package org.firstinspires.ftc.lib.math;

public class Physics {
    /***
     * Calculates the motion of the robot given the parameters accounting for centripetal force
     * @param centripetalForce The centripetal force of the robot (in N)
     * @param velocity The velocity of the robot (in m/s)
     * @param desiredDirection The desired direction of the robot
     * @param mass The mass of the robot (in kg)
     * @param deltaTime The change in time
     * @return The motion of the robot as a {@link Cartesian2d} acting as a vector.
     */
    public static Cartesian2d calculateRobotMotion(double centripetalForce, double velocity, Rotation2d desiredDirection, double mass, double deltaTime) {
        // Calculate acceleration
        double accelerationMagnitude = centripetalForce / mass;

        // Decompose velocity into components
        double vx = velocity * Math.cos(desiredDirection.getRadians());
        double vy = velocity * Math.sin(desiredDirection.getRadians());

        // Calculate change in velocity
        double deltaVx = accelerationMagnitude * deltaTime;
        double deltaVy = 0.0; // Assuming centripetal force does not affect vertical velocity

        // Calculate new velocity components
        double vxNew = vx + deltaVx;
        double vyNew = vy + deltaVy;

        // Calculate new direction
        double thetaNew = Math.atan2(vyNew, vxNew);

        // Calculate new magnitude
        double velocityNew = Math.sqrt(vxNew * vxNew + vyNew * vyNew);

        return new Cartesian2d(Rotation2d.fromRadians(thetaNew), velocityNew);
    }
}

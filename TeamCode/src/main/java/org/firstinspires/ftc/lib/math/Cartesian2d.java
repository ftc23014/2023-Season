package org.firstinspires.ftc.lib.math;

import androidx.annotation.NonNull;

public class Cartesian2d {
    private Rotation2d m_rotation;
    private double m_r;

    /**
     * Returns an empty Cartesian coordinate (zeroed values).
     */
    public static Cartesian2d zero() {
        return new Cartesian2d(new Rotation2d(0), 0);
    }

    /**
     * Creates a new Cartesian coordinate using (x,y) coordinates, relative to (0,0).
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public Cartesian2d(double x, double y) {
        m_rotation = Rotation2d.fromDegrees(Math.atan2(y, x));
        m_r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    /**
     * Creates a new Cartesian coordinate using a Translation2d, relative to Translation2d.zero()
     * @param translation2d The 2d translation.
     */
    public Cartesian2d(Translation2d translation2d) {
        this(translation2d.getX(), translation2d.getY());
    }

    /**
     * Creates a new Cartesian coordinate using a rotation and radius/magnitude
     * @param rotation The rotation of the Cartesian coordinate.
     * @param r The radius/magnitude of the coordinate.
     */
    public Cartesian2d(Rotation2d rotation, double r) {
        m_rotation = rotation;
        m_r = r;
    }

    /**
     * Returns the x coordinate of the Cartesian coordinate relative to (0,0)
     * @return The x coordinate.
     */
    public double getX() {
        return m_r * Math.cos(m_rotation.getRadians());
    }

    /**
     * Returns the y coordinate of the Cartesian coordinate relative to (0,0)
     * @return The y coordinate.
     */
    public double getY() {
        return m_r * Math.sin(m_rotation.getRadians());
    }

    /**
     * Returns the rotation of the Cartesian coordinate
     * @return The rotation
     */
    public Rotation2d getRotation() {
        return this.m_rotation;
    }

    /**
     * Returns the radius/magnitude of the Cartesian coordinate
     * @return The radius/magnitude.
     */
    public double getRadius() {
        return m_r;
    }

    /**
     * Converts the Cartesian coordinate to a Translation2d relative to Translation2d.zero()
     * @return A converted to translation2d coordinate.
     */
    public Translation2d toTranslation2d() {
        return new Translation2d(getX(), getY());
    }


    @Override
    public String toString() {
        return "Cartesian2d(" + m_rotation.getDegrees() + "°, " + m_r + ")";
    }
}

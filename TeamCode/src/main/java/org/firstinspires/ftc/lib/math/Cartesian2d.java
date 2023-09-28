package org.firstinspires.ftc.lib.math;

public class Cartesian2d {
    private Rotation2d m_rotation;
    private double m_r;

    public Cartesian2d(double x, double y) {
        m_rotation = Rotation2d.fromDegrees(Math.atan2(y, x));
        m_r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Cartesian2d(Rotation2d rotation, double r) {
        m_rotation = rotation;
        m_r = r;
    }

    public double getX() {
        return m_r * Math.cos(m_rotation.getRadians());
    }

    public double getY() {
        return m_r * Math.sin(m_rotation.getRadians());
    }

    public Translation2d toTranslation2d() {
        return new Translation2d(getX(), getY());
    }
}

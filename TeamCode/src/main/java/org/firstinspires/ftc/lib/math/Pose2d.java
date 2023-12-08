package org.firstinspires.ftc.lib.math;

public class Pose2d {
    private Translation2d m_position;
    private Rotation2d m_rotation;

    public static Pose2d zero() {
        return new Pose2d(0, 0, 0);
    }

    public Pose2d(Translation2d position, Rotation2d rotation) {
        m_position = position;
        m_rotation = rotation;
    }

    public Pose2d(double x, double y, Rotation2d rotation) {
        m_position = new Translation2d(x, y);
        m_rotation = rotation;
    }

    public Pose2d(Translation2d position, double rotation) {
        m_position = position;
        m_rotation = new Rotation2d(rotation);
    }

    public Pose2d(double x, double y, double rotation) {
        m_position = new Translation2d(x, y);
        m_rotation = new Rotation2d(rotation);
    }

    public double getX() {
        return m_position.getX();
    }

    public double getY() {
        return m_position.getY();
    }

    public Rotation2d getRotation() {
        return m_rotation;
    }

    public Translation2d getPosition() {
        return m_position;
    }

    public Pose2d translateBy(Translation2d translation) {
        return new Pose2d(
                m_position.translateBy(translation.rotateBy(m_rotation)),
                m_rotation
        );
    }

    public Pose2d rotateBy(Rotation2d rotation) {
        return new Pose2d(
                m_position,
                m_rotation.rotateBy(rotation)
        );
    }

    public Pose2d transformBy(Pose2d other) {
        return new Pose2d(
                m_position.translateBy(other.m_position.rotateBy(m_rotation)),
                m_rotation.rotateBy(other.m_rotation)
        );
    }

    public Pose2d exp(double dx, double dy, double dw) {
        double sinTheta = Math.sin(dw);
        double cosTheta = Math.cos(dw);

        double s, c;

        if (Math.abs(dw) < 1E-9) {
            s= 1.0 - 1.0 / 6.0 * dw * dw;
            c = 0.5 * dw;
        } else {
            s = sinTheta / dw;
            c = (1.0 - cosTheta) / dw;
        }

        Translation2d transform = new Translation2d(
                dx * s - dy * c,
                dx * c + dy * s
        );

        Rotation2d rotation = new Rotation2d(cosTheta, sinTheta);

        return transformBy(new Pose2d(transform, rotation));
    }
}

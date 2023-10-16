package org.firstinspires.ftc.lib.math;

public class Rotation2d {
    static public Rotation2d fromDegrees(double degrees) {
        return new Rotation2d(Math.toRadians(degrees));
    }

    static public Rotation2d fromRadians(double radians) {
        return new Rotation2d(radians);
    }

    public static Rotation2d zero() {
        return Rotation2d.fromRadians(0);
    }

    private double m_radians;
    public Rotation2d(double radians) {
        this.m_radians = radians;
    }

    public Rotation2d() {
        this(0);
    }

    public Rotation2d(Rotation2d other) {
        this(other.getRadians());
    }

    public double getRadians() {
        return m_radians;
    }

    public double getDegrees() {
        return Math.toDegrees(m_radians);
    }

    public Rotation2d rotateBy(Rotation2d other) {
        return new Rotation2d(m_radians + other.getRadians());
    }

    public Rotation2d inverse() {
        return new Rotation2d(-m_radians);
    }

    public double getCos() {
        return Math.cos(m_radians);
    }

    public double getSin() {
        return Math.sin(m_radians);
    }
}

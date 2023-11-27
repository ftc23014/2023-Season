package org.firstinspires.ftc.lib.math;

import java.util.HashMap;

public class Translation2d {
    private double m_x;
    private double m_y;

    private HashMap<String, Double> attributes = new HashMap<>();

    public static Translation2d zero() {
        return new Translation2d(0,0);
    }

    public Translation2d(double x, double y) {
        this.m_x = x;
        this.m_y = y;
    }

    public Translation2d() {
        this(0, 0);
    }

    public Translation2d(Translation2d other) {
        this(other.getX(), other.getY());
    }

    public double getX() {
        return m_x;
    }

    public double getY() {
        return m_y;
    }

    public Translation2d translateBy(Translation2d other) {
        return new Translation2d(m_x + other.getX(), m_y + other.getY());
    }

    public Translation2d translateBy(double other_x, double other_y) {
        return new Translation2d(m_x + other_x, m_y + other_y);
    }

    public Translation2d rotateBy(Rotation2d rotation) {
        return new Translation2d(m_x * rotation.getCos() - m_y * rotation.getSin(), m_x * rotation.getSin() + m_y * rotation.getCos());
    }

    public Translation2d inverse() {
        return new Translation2d(-m_x, -m_y);
    }

    public double distance(Translation2d other) {
        return inverse().translateBy(other).norm();
    }

    public double norm() {
        return Math.hypot(m_x, m_y);
    }

    public double dot(Translation2d other) {
        return m_x * other.getX() + m_y * other.getY();
    }

    public Translation2d scalar(double scale) {
        return new Translation2d(
                m_x * scale,
                m_y * scale
        );
    }

    public void setX(double x) {
        this.m_x = x;
    }

    public void setY(double y) {
        this.m_y = y;
    }

    public Translation2d withAttribute(String name, double value) {
        addAttribute(name, value);
        return this;
    }

    public void addAttribute(String name, double value) {
        attributes.put(name, value);
    }

    public double getAttribute(String name) {
        return attributes.containsKey(name) ? attributes.get(name) : 0;
    }

    public boolean isZero() {
        return m_x == 0 && m_y == 0;
    }

    public Translation2d copy() {
        return new Translation2d(m_x, m_y);
    }

    @Override
    public String toString() {
        return "(" + m_x + "," + m_y + ")" + (!attributes.keySet().isEmpty() ? " [" + attributes.toString() + "]" : "");
    }

}

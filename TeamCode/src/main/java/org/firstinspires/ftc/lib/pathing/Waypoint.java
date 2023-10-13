package org.firstinspires.ftc.lib.pathing;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;

public class Waypoint {
    public enum Type {
        HARD,
        SOFT
    }

    private Translation2d m_position;
    private Rotation2d m_rotation;

    private Type m_type;

    public Waypoint(Translation2d position, Rotation2d heading, Type type) {
        m_position = position;
        m_rotation = heading;
        this.m_type = type;
    }

    public Translation2d getPosition() {
        return m_position;
    }

    public double getX() {
        return m_position.getX();
    }

    public double getY() {
        return m_position.getY();
    }

    public Rotation2d getHeading() {
        return m_rotation;
    }

    public Type getType() {
        return m_type;
    }

    public boolean equals(Waypoint other) {
        if (other.getType() != m_type) return false;

        //close approximation, doesn't need to be exact, but pretty much equal. avoids double errors
        if (Math.abs(other.getPosition().getX() - m_position.getX()) > 0.00001) return false;
        if (Math.abs(other.getPosition().getY() - m_position.getY()) > 0.00001) return false;
        if (Math.abs(other.getHeading().getDegrees() - m_rotation.getDegrees()) > 0.00001) return false;

        return true;
    }

    public String toString() {
        return "Waypoint(" + m_position.getX() + ", " + m_position.getY() + ", " + m_rotation.getDegrees() + ", " + m_type + ")";
    }
}

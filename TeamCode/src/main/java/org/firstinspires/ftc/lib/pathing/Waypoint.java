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
}

package org.firstinspires.ftc.lib.pathing;

import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Unit;

public class AutonomousConstants {
    private Unit max_speed;
    private Unit max_acceleration;
    private Unit mass;

    private PIDController m_controller;

    public AutonomousConstants(Unit max_speed, Unit max_acceleration, Unit mass, PIDController controller) {
        this.max_speed = max_speed;
        this.max_acceleration = max_acceleration;
        this.mass = mass;
        this.m_controller = controller;
    }

    public Unit getMaxSpeed() {
        return max_speed;
    }

    public Unit getMaxAcceleration() {
        return max_acceleration;
    }

    public Unit getMass() {
        return mass;
    }

    public PIDController getPID() {
        return m_controller;
    }
}

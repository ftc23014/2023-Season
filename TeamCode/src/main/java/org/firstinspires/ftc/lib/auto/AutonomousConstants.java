package org.firstinspires.ftc.lib.auto;

import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Unit;

public class AutonomousConstants {
    private Unit max_speed;
    private Unit max_acceleration;
    private double mass;

    private PIDController m_controller;

    private final static double DELTA_TIME = 1 / 32;

    public AutonomousConstants(Unit max_speed, Unit max_acceleration, double mass_in_kg, PIDController controller) {
        this.max_speed = max_speed;
        this.max_acceleration = max_acceleration;
        this.mass = mass_in_kg;
        this.m_controller = controller;
    }

    public Unit getMaxSpeed() {
        return max_speed;
    }

    public Unit getMaxAcceleration() {
        return max_acceleration;
    }

    public double getMass() {
        return mass;
    }

    public PIDController getPID() {
        return m_controller;
    }

    public static double getDeltaTime() {
        return DELTA_TIME;
    }
}

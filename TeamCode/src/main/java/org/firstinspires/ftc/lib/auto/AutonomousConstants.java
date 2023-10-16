package org.firstinspires.ftc.lib.auto;

import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Unit;

public class AutonomousConstants {
    private Unit max_speed;
    private Unit max_acceleration;
    private double mass;

    private PIDController m_controller;

    private double DELTA_TIME = 1d / 32d;

    private boolean open_loop_drive = true;

    /**
     * Create a new constant profile for autonomous.
     * @param max_speed The max speed of the robot, in [unit] per second.
     * @param max_acceleration The max speed of the robot, in [unit] per second per second.
     * @param mass_in_kg The mass of the robot (in kilograms).
     * @param controller The PID controller to use for path generation.
     * @param delta_time How often the robot should update its motion (in seconds).
     *                   This can potentially lead to unwanted motion from the PID, so make sure that you accordingly tune the PID.
     */
    public AutonomousConstants(Unit max_speed, Unit max_acceleration, double mass_in_kg, PIDController controller, double delta_time) {
        this.max_speed = max_speed;
        this.max_acceleration = max_acceleration;
        this.mass = mass_in_kg;
        this.m_controller = controller;
        this.DELTA_TIME = delta_time;
    }

    /**
     * Returns the max speed.
     * @return Max speed.
     */
    public Unit getMaxSpeed() {
        return max_speed;
    }

    /**
     * Sets the settings to use open loop in driving (default set to true)
     * @param openLoop Use open loop control.
     */
    public void useOpenLoop(boolean openLoop) {
        this.open_loop_drive = openLoop;
    }

    /***
     * Returns whether open loop is enabled/disabled.
     * @return Whether to use open loop or not.
     */
    public boolean getOpenLoop() {
        return this.open_loop_drive;
    }

    /**
     * Returns the max acceleration.
     * @return Max acceleration.
     */
    public Unit getMaxAcceleration() {
        return max_acceleration;
    }

    /**
     * Returns the mass of the robot (in kg).
     * @return Mass of the robot (kg).
     */
    public double getMass() {
        return mass;
    }

    /**
     * Returns the PID controller to use for the trajectory generation.
     * @return PID controller.
     */
    public PIDController getPID() {
        return m_controller;
    }

    /**
     * Returns the delta time.
     * @return Delta time (in seconds).
     */
    public double getDeltaTime() {
        return DELTA_TIME;
    }
}

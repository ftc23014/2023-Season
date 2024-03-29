package org.firstinspires.ftc.lib.math;

import static java.lang.Double.NaN;

public class PIDController {
    private double m_kP;
    private double m_kI;
    private double m_kD;

    private double m_integral;

    private double m_setpoint;

    private double m_lastSetpoint;

    /**
     * Create a new PID controller with the given constants.
     * @param kP The proportional constant
     * @param kI The integral constant
     * @param kD The derivative constant
     */
    public PIDController(double kP, double kI, double kD) {
        m_kP = kP;
        m_kI = kI;
        m_kD = kD;

        m_integral = 0;
        m_lastSetpoint = 0;
    }

    public double[] getPID() {
        return new double[] { m_kP, m_kI, m_kD };
    }

    public void reset() {
        m_integral = 0;
        m_lastSetpoint = 0;
        m_setpoint = 0;
    }

    public boolean initialized() {
        return !Double.isNaN(m_setpoint);
    }

    public void setSetpoint(double setpoint) {
        m_setpoint = setpoint;

        reset();
    }

    public double getSetpoint() {
        return m_setpoint;
    }

    public double calculate(double setpoint, double goal) {
        if (!initialized()) {
            return 0;
        }

        double error = goal - setpoint;

        double integral = m_integral + error;

        double derivative = setpoint - m_lastSetpoint;

        double output = (m_kP * error) + (m_kI * integral) - (m_kD * derivative);

        m_setpoint += output;
        m_lastSetpoint = setpoint;

        m_integral = integral;

        return output;
    }

    public double calculateWithDelta(double setpoint, double goal, double delta) {
        if (!initialized()) {
            return 0;
        }

        double error = goal - setpoint;

        double integral = m_integral + (error * delta);

        double derivative = setpoint - m_lastSetpoint;

        double output = (m_kP * error) + (m_kI * integral) - (m_kD * derivative);

        m_setpoint += output;
        m_lastSetpoint = setpoint;

        m_integral = integral;

        return output;
    }
}

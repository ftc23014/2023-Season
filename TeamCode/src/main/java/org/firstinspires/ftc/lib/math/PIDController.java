package org.firstinspires.ftc.lib.math;

import static java.lang.Double.NaN;

public class PIDController {
    private double m_kP;
    private double m_kI;
    private double m_kD;

    private double m_integral;

    private double m_setpoint;

    private double m_lastError;

    public PIDController(double kP, double kI, double kD) {
        m_kP = kP;
        m_kI = kI;
        m_kD = kD;

        m_integral = 0;
        m_lastError = 0;
    }

    public void reset() {
        m_integral = 0;
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

    public double calculate(double goal) {
        if (!initialized()) {
            return 0;
        }

        double error = goal - m_setpoint;

        m_integral += error;

        double derivative = error - m_lastError;

        m_lastError = error;

        double output = m_kP * error + m_kI * m_integral + m_kD * derivative;

        m_setpoint += output;

        return output;
    }

}

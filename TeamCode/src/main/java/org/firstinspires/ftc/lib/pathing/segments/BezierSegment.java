package org.firstinspires.ftc.lib.pathing.segments;

import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.pathing.AutonomousConstants;
import org.firstinspires.ftc.lib.pathing.FourPointBezier;
import org.firstinspires.ftc.lib.pathing.Waypoint;

import java.util.ArrayList;

public class BezierSegment extends Segment {

    private FourPointBezier m_bezier;
    private PIDController m_controller;
    private AutonomousConstants m_constants;

    public BezierSegment(FourPointBezier bezier) {
        m_bezier = bezier;
    }

    public BezierSegment(FourPointBezier bezier, PIDController controller) {
        m_bezier = bezier;

        m_controller = controller;
    }

    @Override
    public Waypoint[] getWaypoints() {
        return m_bezier.getWaypoints();
    }

    @Override
    public ArrayList<Translation2d> getPoints() {
        return m_bezier.getPoints();
    }

    @Override
    public double getLength() {
        return m_bezier.length();
    }

    @Override
    public void generate() {
        m_bezier.generateByPID(0.001, m_controller, 0.01, m_constants.getMaxSpeed().get(Unit.Type.Centimeters), m_constants.getMaxAcceleration().get(Unit.Type.Centimeters));
    }

    @Override
    public void setConstants(AutonomousConstants constants) {
        m_controller = constants.getPID();
        m_constants = constants;
    }
}

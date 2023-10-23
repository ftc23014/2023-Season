package org.firstinspires.ftc.lib.pathing;

import com.sun.tools.javac.util.Pair;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.pathing.segments.Segment;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;

public class Trajectory extends Command {

    /**
     * General TODO:
     * - Implement Drive Mode
     * - Implement Flipping the path depending on the side of the field
     * - Rotate direction that the robot should take depending on the initial rotation of the robot
     * - Test the pathing system
     * */

    enum DriveMode { //todo: implement these, where instant change is when the robot instantly changes velocity, and smooth is a gradual change.
        SmoothChange,
        InstantChange;
    }

    private DriveMode m_driveMode;

    private int m_onSegment = 0;
    private int m_onPoint = 0;
    private Segment[] m_segments;
    private DriveSubsystem m_driveSubsystem;
    private AutonomousConstants m_constants;

    private double m_startTime = 0;

    private boolean hasBeenExecuted = false;

    private boolean m_usePhysicsCalculations = true;

    public Trajectory(DriveSubsystem driveSubsystem, Segment... segments) {
        super();

        this.m_driveSubsystem = driveSubsystem;
        m_segments = segments;
    }

    public void generate() {
        for (Segment seg : m_segments) {
            seg.generate();
        }

        hasBeenExecuted = false;
    }

    public void setConstants(AutonomousConstants constants) {
        for (Segment seg : m_segments) {
            seg.setConstants(constants);
        }
        m_constants = constants;
    }

    @Override
    public void execute() {
        if (!hasBeenExecuted) {
            hasBeenExecuted = true;
            m_startTime = System.currentTimeMillis();
        }

        double currentTime = System.currentTimeMillis();
        double timeSincePathStarted = currentTime - m_startTime;

        Translation2d velocity = m_segments[m_onSegment].getVelocityAtPoint(m_onPoint, m_constants.getDeltaTime());
        Translation2d nextVelocity;
        if (m_onPoint >= m_segments[m_onSegment].getPoints().size()) {
            if (m_onSegment + 1 < m_segments.length) {
                nextVelocity = m_segments[m_onSegment + 1].getVelocityAtPoint(0, m_constants.getDeltaTime());
            } else {
                nextVelocity = Translation2d.zero();
            }
        } else {
            nextVelocity = m_segments[m_onSegment].getVelocityAtPoint(m_onPoint + 1, m_constants.getDeltaTime());
        }

        Translation2d velocities = new Translation2d(
                nextVelocity.getX() - velocity.getX(),
                nextVelocity.getY() - velocity.getY()
        ).scalar(1 / (timeSincePathStarted % m_constants.getDeltaTime())); //uhh idk what i was doing here, might be wrong. todo: look at this.

        Pair<Rotation2d, Rotation2d> rotations = m_segments[m_onSegment].angles();

        Rotation2d rotation_speed = new Rotation2d(
                (rotations.snd.getRadians() - rotations.fst.getRadians()) / (m_segments[m_onSegment].getPoints().size())
        );

        if (!m_usePhysicsCalculations) {
            m_driveSubsystem.drive(
                    velocities,
                    rotation_speed,
                    true,
                    m_constants.getOpenLoop()
            );
            return;
        }

        double currentPathTValue = velocity.getAttribute("t");

        //making an assumption that it's a four point bezier. it's the only path that exists rn so we don't have to do that much complexities.

        FourPointBezier pathObject = (FourPointBezier) m_segments[m_onSegment].getPathObject();

        //TODO: Use odometry to calculate the real velocity.
        Unit currentRealVelocity = new Unit(0, Unit.Type.Meters);

        double centripetalForce = pathObject.centripetalForce(
                currentPathTValue,
                m_constants.getMass(),
                currentRealVelocity
        );

        Cartesian2d newMotionDirection = Physics.calculateRobotMotion(
                centripetalForce,
                currentRealVelocity.get(Unit.Type.Meters), //m/s
                new Cartesian2d(velocities).getRotation(),
                m_constants.getMass(), //kg
                m_constants.getDeltaTime()
        );

        Translation2d motionValues = newMotionDirection.toTranslation2d();

        m_driveSubsystem.drive(
                motionValues,
                rotation_speed,
                true,
                m_constants.getOpenLoop()
        );
    }
}

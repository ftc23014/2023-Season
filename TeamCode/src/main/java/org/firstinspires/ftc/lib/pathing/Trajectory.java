package org.firstinspires.ftc.lib.pathing;

import com.sun.tools.javac.util.Pair;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.pathing.segments.Segment;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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

    private boolean runFlippedX = false;

    public Trajectory(DriveSubsystem driveSubsystem, Segment... segments) {
        super();

        this.m_driveSubsystem = driveSubsystem;
        m_segments = segments;
        m_driveMode = DriveMode.InstantChange;
    }

    public Trajectory runFlippedX(boolean flipIt) {
        this.runFlippedX = flipIt;
        return this;
    }

    public boolean finishedGenerating() {
        for (Segment seg : m_segments) {
            if (!seg.finishedGeneration()) {
                return false;
            }
        }

        return true;
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

    public void flipX() {
        for (Segment seg : m_segments) {
            seg.flipX();
        }
    }

    @Override
    public boolean hasFinished() {
        return m_onSegment >= m_segments.length;
    }

    private double lastExecute = 0;


    private TimerTask incremental;
    private Timer timer = new Timer();

    @Override
    public void execute() {
        if (!hasBeenExecuted) {
            hasBeenExecuted = true;
            m_startTime = System.currentTimeMillis();
            lastExecute = System.currentTimeMillis();

            timer = new Timer();
            incremental = new TimerTask() {
                @Override
                public void run() {
                    if (m_onSegment >= m_segments.length) {
                        cancel();
                        timer.cancel();
                        return;
                    }

                    m_onPoint++;
                    if (m_onPoint >= m_segments[m_onSegment].getPoints().size()) {
                        m_onPoint = 0;
                        m_onSegment++;
                    }

                    //current velocity
//                    Translation2d velocity = m_segments[m_onSegment]
//                            .getVelocityAtPoint(m_onPoint, m_constants.getDeltaTime())
//                            .rotateBy(Rotation2d.fromDegrees(-90))
//                            .scalar(1 / m_constants.getDeltaTime());
//
//                    Autonomous.getTelemetry().addData("v", m_onPoint + "/" + m_segments[m_onSegment].getPoints().size() + " - v: " + velocity.toString());
//
//                    m_driveSubsystem.drive(
//                            velocity,
//                            Rotation2d.zero(),
//                            true,
//                            m_constants.getOpenLoop()
//                    );
                }
            };

            timer.schedule(incremental, 0, (long) (m_constants.getDeltaTime() * 1000));
        }

        double currentTime = System.currentTimeMillis();
        double timeSincePathStarted = currentTime - m_startTime;
        double deltaExecute = (currentTime - lastExecute) / 1000;

        Translation2d velocity = m_onSegment >= m_segments.length ?
                Translation2d.zero() :
                    m_onPoint >= m_segments[m_onSegment].getPoints().size() ?
                            Translation2d.zero() :
                            m_segments[m_onSegment].getVelocityAtPoint(m_onPoint, m_constants.getDeltaTime());

        Translation2d nextVelocity;

        if (m_onSegment + 1 < m_segments.length) {
            if (m_onPoint >= m_segments[m_onSegment].getPoints().size()) {
                nextVelocity = m_segments[m_onSegment + 1].getVelocityAtPoint(0, m_constants.getDeltaTime());
            } else {
                nextVelocity = m_segments[m_onSegment].getVelocityAtPoint(m_onPoint + 1, m_constants.getDeltaTime());
            }
        } else {
            nextVelocity = Translation2d.zero();
        }

        Translation2d velocities = (
                velocity.isZero() ? nextVelocity : velocity
        );

        if (runFlippedX) {
            velocities = new Translation2d(
                    -velocities.getX(),
                    velocities.getY()
            );
        }

        velocities = velocities.rotateBy(Rotation2d.fromDegrees(-90)).scalar(1 / m_constants.getDeltaTime());

        //Autonomous.getTelemetry().addLine("v: " + velocities.toString());

        lastExecute = currentTime;

        Pair<Rotation2d, Rotation2d> rotations =
                m_onSegment >= m_segments.length ?
                        new Pair<>(Rotation2d.zero(), Rotation2d.zero()) :
                        m_onPoint >= m_segments[m_onSegment].getPoints().size() ?
                                new Pair<>(Rotation2d.zero(), Rotation2d.zero()) :
                                m_segments[m_onSegment].angles();

        Rotation2d rotation_speed
//                = new Rotation2d(
//                (rotations.snd.getRadians() - rotations.fst.getRadians()) / (m_segments[m_onSegment].getPoints().size())
//        );
//
//        rotation_speed
                = Rotation2d.zero();

        double currentPathTValue = velocity.getAttribute("t");

        //making an assumption that it's a four point bezier. it's the only path that exists rn so we don't have to do that much complexities.

        FourPointBezier pathObject = m_onSegment < m_segments.length ? (FourPointBezier) m_segments[m_onSegment].getPathObject() : null;

        if (pathObject == null) {
            m_driveSubsystem.drive(
                    Translation2d.zero(),
                    Rotation2d.zero(),
                    true,
                    m_constants.getOpenLoop()
            );
            return;
        }

        Unit currentRealVelocity = m_driveSubsystem.getVelocity();

        //get the centripetal force at the current point.
        double centripetalForce = pathObject.centripetalForce(
                currentPathTValue,
                m_constants.getMass(),
                currentRealVelocity
        );

        //calculate the new motion direction and magnitude using the centripetal force.
        Cartesian2d newMotionDirection = Physics.calculateRobotMotion(
                centripetalForce,
                currentRealVelocity.get(Unit.Type.Meters), //m/s
                new Cartesian2d(velocities).getRotation(),
                m_constants.getMass(), //kg
                m_constants.getDeltaTime()
        );

        telemetry().addLine("centripetal force: " + centripetalForce);
        telemetry().addLine("current velocity: " + currentRealVelocity.toString());
        telemetry().addLine("new motion direction: " + newMotionDirection.toString());
        telemetry().update();

        if (!m_constants.usePhysicsCalculations()) {
            m_driveSubsystem.drive(
                    velocities,
                    rotation_speed,
                    true,
                    m_constants.getOpenLoop()
            );
            return;
        }

        //convert the new motion direction to a translation2d (velocities that we can use to drive the robot
        Translation2d motionValues = newMotionDirection.toTranslation2d();

        //drive the robot
        m_driveSubsystem.drive(
                motionValues,
                rotation_speed,
                true,
                m_constants.getOpenLoop()
        );
    }

    @Override
    public void cancel() {
        incremental.cancel();
        timer.cancel();
    }
}

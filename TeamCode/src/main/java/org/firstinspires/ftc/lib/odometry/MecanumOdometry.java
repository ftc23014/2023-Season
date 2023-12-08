package org.firstinspires.ftc.lib.odometry;

import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;

public class MecanumOdometry {
    private static MecanumOdometry instance;

    private Pose2d pose;
    
    private double m_leftEncoder;
    private double m_rightEncoder;
    private double m_centerEncoder;

    private boolean firstRun = true;

    public static class MecanumOdometryConfig {
        public int encoderResolution = 2000;
        public Unit wheelDiameter = new Unit(4.8d, Unit.Type.Centimeters);

        public Unit trackwidth = new Unit(34.5d, Unit.Type.Centimeters);
        public Unit forwardOffset = new Unit(10d, Unit.Type.Centimeters);

        public MecanumOdometryConfig() {}

        public MecanumOdometryConfig(int encoderResolution, Unit wheelDiameter, Unit trackwidth, Unit forwardOffset) {
            this.encoderResolution = encoderResolution;
            this.wheelDiameter = wheelDiameter;
            this.trackwidth = trackwidth;
            this.forwardOffset = forwardOffset;
        }
    }

    private int encoderResolution = 2000;
    private Unit m_wheelDiameter = new Unit(4.8d, Unit.Type.Centimeters);

    private Unit m_trackwidth = new Unit(34.5d, Unit.Type.Centimeters);
    private Unit m_forwardOffset = new Unit(10d, Unit.Type.Centimeters);

    /**
     * Creates a new MecanumOdometry instance.
     * If there's already an instance, it will eight return the existing instance or create a new one.
     * @param m_currentPosition The current position of the robot.
     *                          This is used to set the initial position of the robot.
     *                          If there's already an instance, this parameter is ignored.
     * @param preserve Whether to preserve the existing instance.
     * */
    public static MecanumOdometry create(Pose2d m_currentPosition, boolean preserve) {
        if (instance != null && preserve)
            return instance;

        instance = new MecanumOdometry(m_currentPosition);
        return instance;
    }

    public MecanumOdometry(Pose2d m_currentPosition) {
        this.pose = m_currentPosition;
    }

    public MecanumOdometry(MecanumOdometryConfig config, Pose2d m_currentPosition) {
        this.pose = m_currentPosition;

        this.encoderResolution = config.encoderResolution;
        this.m_wheelDiameter = config.wheelDiameter;
        this.m_trackwidth = config.trackwidth;
        this.m_forwardOffset = config.forwardOffset;
    }

    public void updateOdometry(double leftEncoder, double rightEncoder, double centerEncoder) {
        if (firstRun) {
            m_leftEncoder = leftEncoder;
            m_rightEncoder = rightEncoder;
            m_centerEncoder = centerEncoder;

            firstRun = false;
            return;
        }

        double rawDeltaLeft = leftEncoder - m_leftEncoder;
        double rawDeltaRight = rightEncoder - m_rightEncoder;
        double rawDeltaCenter = centerEncoder - m_centerEncoder;

        double deltaLeft = convertFromEncoderTicks(rawDeltaLeft).get(Unit.Type.Meters);
        double deltaRight = convertFromEncoderTicks(rawDeltaRight).get(Unit.Type.Meters);
        double deltaCenter = convertFromEncoderTicks(rawDeltaCenter).get(Unit.Type.Meters);

        Rotation2d rotation = pose.getRotation().rotateBy(
                new Rotation2d(
                        (deltaLeft - deltaRight) / m_trackwidth.get(Unit.Type.Meters)
                )
        );

        m_leftEncoder = leftEncoder;
        m_rightEncoder = rightEncoder;
        m_centerEncoder = centerEncoder;

        double dw = (rotation.getRadians() - pose.getRotation().getRadians());
        double dx = (deltaLeft + deltaRight) / 2;
        double dy = deltaCenter - (m_forwardOffset.get(Unit.Type.Meters) * dw);

        pose = pose.exp(dx, dy, dw);
    }

    public Unit convertFromEncoderTicks(double ticks) {
        return new Unit(ticks / encoderResolution * m_wheelDiameter.get(Unit.Type.Meters) * Math.PI, Unit.Type.Meters);
    }

    public void reset(Pose2d currentPosition) {
        pose = currentPosition;
    }

    public Pose2d getPose() {
        return pose;
    }

    public Translation2d getPosition() {
        return pose.getPosition();
    }

    public Rotation2d getRotation() {
        return pose.getRotation();
    }
}

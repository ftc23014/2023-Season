package org.firstinspires.ftc.lib.odometry;

import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;

public class MecanumOdometry {
    private static MecanumOdometry instance;

    private Pose2d pose;
    
    private double m_leftEncoder;
    private double m_rightEncoder;
    private double m_centerEncoder;

    private double m_trackwidth;
    private double m_forwardOffset;

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

    public void updateOdometry(double leftEncoder, double rightEncoder, double centerEncoder) {
        double deltaLeft = leftEncoder - m_leftEncoder;
        double deltaRight = rightEncoder - m_rightEncoder;
        double deltaCenter = centerEncoder - m_centerEncoder;

        double phi = (deltaLeft - deltaRight) / m_trackwidth;
        double delta_middle_pos = (deltaLeft + deltaRight) / 2;
        double delta_perpendicular_pos = deltaCenter - m_forwardOffset * phi;

        double delta_x = delta_middle_pos * pose.getRotation().getCos() - delta_perpendicular_pos * pose.getRotation().getSin();
        double delta_y = delta_middle_pos * pose.getRotation().getSin() + delta_perpendicular_pos * pose.getRotation().getCos();

        pose = pose.transformBy(new Pose2d(new Translation2d(delta_x, delta_y), new Rotation2d(phi)));

        m_leftEncoder = leftEncoder;
        m_rightEncoder = rightEncoder;
        m_centerEncoder = centerEncoder;
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

package org.firstinspires.ftc.lib.odometry;

import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;

public class MecanumOdometry {
    private Pose2d pose;
    
    private double m_leftEncoder;
    private double m_rightEncoder;
    private double m_centerEncoder;

    private double m_trackwidth;
    private double m_forwardOffset;


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

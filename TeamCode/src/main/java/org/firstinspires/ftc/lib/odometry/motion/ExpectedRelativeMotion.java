package org.firstinspires.ftc.lib.odometry.motion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.firstinspires.ftc.lib.math.Cartesian2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;

public class ExpectedRelativeMotion {

    protected int m_numberOfWheels;

    protected Cartesian2d[] m_wheelMotion;

    protected Translation2d m_expectedVelocities;
    protected Rotation2d m_expectedRotation;

    private String name;

    /**
     * Created an "expected" motion profile for the robot that's used for debugging purposes.
     * @param subsystem The drive subsystem that this motion profile is for.
     * @param numberOfWheels The number of wheels that the robot has.
     */
    public ExpectedRelativeMotion(DriveSubsystem subsystem, int numberOfWheels) {
        m_numberOfWheels = numberOfWheels;
        m_wheelMotion = new Cartesian2d[m_numberOfWheels];

        for (int i = 0; i < m_numberOfWheels; i++) m_wheelMotion[i] = Cartesian2d.zero();

        name = subsystem.getClass().getSuperclass().getName();

        m_expectedVelocities = Translation2d.zero();
        m_expectedRotation = Rotation2d.zero();

        MotionAPI.expectedRelativeMotions.add(this);
    }

    /**
     * Sets the expected motion of the robot.
     * @param movement The expected movement of the robot.
     * @param rotationSpeed The expected rotation speed of the robot.
     */
    public void setExpectedMotion(Translation2d movement, Rotation2d rotationSpeed) {
        this.m_expectedRotation = rotationSpeed;
        this.m_expectedVelocities = movement;
    }

    /**
     * Returns the name of the motion profile.
     * @return The name of the motion profile.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the expected motion of the robot in JSON format.
     * @return The expected motion of the robot in JSON format.
     */
    public JsonObject asJSON() {
        JsonObject obj = new JsonObject();

        JsonObject translation = new JsonObject();

        translation.addProperty(
                "x",
                m_expectedVelocities.getX()
        );

        translation.addProperty(
                "y",
                m_expectedVelocities.getY()
        );

        obj.add("translation", translation);
        obj.addProperty("rotation_speed", m_expectedRotation.getDegrees());

        JsonArray wheelMotions = new JsonArray();

        for (Cartesian2d wheelMotion : m_wheelMotion) {
            JsonObject motionobj = new JsonObject();

            motionobj.addProperty(
                    "direction",
                    wheelMotion.getRotation().getDegrees()
            );

            motionobj.addProperty(
                    "power",
                    wheelMotion.getRadius()
            );

            wheelMotions.add(motionobj);
        }

        obj.add("wheel_motions", wheelMotions);
        obj.addProperty("wheel_number", m_numberOfWheels);

        return obj;
    }

    /**
     * Returns the expected motion of the robot in JSON format as a string.
     * @return The expected motion of the robot in JSON format as a string.
     */
    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(asJSON());
    }
}

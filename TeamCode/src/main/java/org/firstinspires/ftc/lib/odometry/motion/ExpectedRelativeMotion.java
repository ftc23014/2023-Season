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

    public ExpectedRelativeMotion(DriveSubsystem subsystem, int numberOfWheels) {
        m_numberOfWheels = numberOfWheels;
        m_wheelMotion = new Cartesian2d[m_numberOfWheels];

        name = subsystem.getClass().getSuperclass().getName();

        MotionAPI.expectedRelativeMotions.add(this);
    }

    public void setExpectedMotion(Translation2d movement, Rotation2d rotationSpeed) {
        this.m_expectedRotation = rotationSpeed;
        this.m_expectedVelocities = movement;
    }

    public String getName() {
        return this.name;
    }

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

    public String toString() {
        Gson gson = new Gson();

        return gson.toJson(asJSON());
    }
}

package org.firstinspires.ftc.lib.pathing.segments;

import com.sun.tools.javac.util.Pair;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.pathing.Waypoint;

import java.util.ArrayList;

public abstract class Segment {
    public abstract Waypoint[] getWaypoints();

    public abstract ArrayList<Translation2d> getPoints();

    public abstract double getLength();

    public abstract void generate();

    public abstract void setConstants(AutonomousConstants constants);

    public abstract Pair<Rotation2d, Rotation2d> angles();

    public Translation2d getVelocityAtPoint(int pointIndex, double deltaTime) {
        if (pointIndex + 1 >= getPoints().size()) {
            throw new IndexOutOfBoundsException("Too high of a point index!");
        }

        Translation2d point1 = getPoints().get(pointIndex);
        Translation2d point2 = getPoints().get(pointIndex + 1);

        Translation2d difference = new Translation2d(
                point2.getX() - point1.getX(),
                point2.getY() - point1.getY()
        );

        difference = difference.scalar(1d / deltaTime);

        return difference;
    }
}

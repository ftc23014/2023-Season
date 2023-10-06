package org.firstinspires.ftc.lib.pathing.segments;

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
}

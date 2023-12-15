package org.firstinspires.ftc.lib.pathing.segments;

import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.tools.javac.util.Pair;
import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.pathing.FourPointBezier;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.simulation.Simulation;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

public class BezierSegment extends Segment {
    public static BezierSegment[] loadFromResources(int resource) {
        String fileContents = "";

        try {
            //read from "~/res/raw/"
            InputStream stream =
                    Subsystem.getAppContext().getResources().openRawResource(resource);

            fileContents = FileUtils.read(stream);
                    //new File("/Users/jgrimminck2023/Documents/coding/ftc/FtcRobotController/TeamCode/src/main/res/raw/testing.json"));

            return parseFromContents(fileContents);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read file " + resource);
        }

        return new BezierSegment[0];
    }

    public static BezierSegment[] loadFromFile(File file) {
        String fileContents = "";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                fileContents = new String(Files.readAllBytes(file.toPath()));
            } else {
                throw new RuntimeException("Unsupported Android version");
            }

            return parseFromContents(fileContents);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read file " + file.getName());
        }

        return new BezierSegment[0];
    }

    private static BezierSegment[] parseFromContents(String fileContents) {
        try {
            Gson gson = new Gson();

            JsonObject obj = gson.fromJson(fileContents, JsonObject.class);

            JsonArray waypoints = obj.getAsJsonArray("waypoints");

            ArrayList<BezierSegment> segments = new ArrayList<>();

            Waypoint[] construction = new Waypoint[4];
            int constructionIndex = 0;

            for (int i = 0; i < waypoints.size(); i++) {
                JsonObject waypoint = waypoints.get(i).getAsJsonObject();

                double x = waypoint.get("x").getAsDouble();
                double y = waypoint.get("y").getAsDouble();
                double heading = waypoint.get("heading").getAsDouble();
                double type = waypoint.get("type").getAsDouble();

                construction[constructionIndex] = new Waypoint(
                        new Translation2d(
                            new Unit(x, Unit.Type.Centimeters).get(Unit.Type.Meters),
                            new Unit(y, Unit.Type.Centimeters).get(Unit.Type.Meters)
                        ),
                        Rotation2d.fromDegrees(heading),
                        type == 0 ? Waypoint.Type.HARD : Waypoint.Type.SOFT
                );
                constructionIndex++;

                if (constructionIndex == 4) {
                    segments.add(new BezierSegment(new FourPointBezier(
                            construction[0],
                            construction[1],
                            construction[2],
                            construction[3]
                    )));

                    constructionIndex = 0;
                    construction = new Waypoint[4];
                }
            }

            return segments.toArray(new BezierSegment[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read file!");
        }

        return new BezierSegment[0];
    }

    public static void saveSegmentsToFile(File file, BezierSegment ...segments) {
        saveSegmentsToFile(file, false, segments);
    }

    public static void saveSegmentsToFile(File file, boolean overrideFile, BezierSegment ...segments) {
        if (file.exists()) {
            if (!overrideFile) {
                throw new RuntimeException("File already exists for multi segment? If you want to override " + file.getName() + ", then set overrideFile to true");
            } else {
                System.out.println("Note: Overriding file " + file.getName());
            }
        }

        if (!file.getName().endsWith(".json")) {
            throw new RuntimeException("File must be a .json file");
        }

        Gson gson = new Gson();

        JsonObject obj = new JsonObject();

        ArrayList<Waypoint> waypoints = new ArrayList<>();

        BezierSegment lastSeg = null;
        int seg_index = 0;
        for (BezierSegment seg : segments) {
            if (lastSeg != null) {
                if (!lastSeg.connectedTo(seg)) {
                    throw new RuntimeException("Segments are not connected!");
                }
            }

            int w_index = 0;
            for (Waypoint waypoint : seg.getWaypoints()) {
                if (seg_index != 0 && w_index == 0) {
                    w_index++;
                    continue;
                }

                waypoints.add(waypoint);

                w_index++;
            }

            seg_index++;
            lastSeg = seg;
        }

        obj.add("waypoints", waypointsToJSONList(waypoints.toArray(new Waypoint[0])));

        FileUtils.write(file, gson.toJson(obj), false);
    }

    public static JsonArray waypointsToJSONList(Waypoint ...points) {
        Gson gson = new Gson();

        JsonArray json_waypoints = new JsonArray();

        for (Waypoint waypoint : points) {
            JsonObject json_waypoint = new JsonObject();

            json_waypoint.add("x", gson.toJsonTree(waypoint.getPosition().getX()));
            json_waypoint.add("y", gson.toJsonTree(waypoint.getPosition().getY()));
            json_waypoint.add("heading", gson.toJsonTree(waypoint.getHeading().getDegrees()));
            json_waypoint.add("type", gson.toJsonTree(waypoint.getType() == Waypoint.Type.HARD ? 0 : 1));

            json_waypoints.add(json_waypoint);
        }

        return json_waypoints;
    }


    private final FourPointBezier m_bezier;
    private PIDController m_controller;
    private AutonomousConstants m_constants;


    public BezierSegment(FourPointBezier bezier) {
        m_bezier = bezier;
    }

    public BezierSegment(Waypoint start, Waypoint control1, Waypoint control2, Waypoint end) {
        this(new FourPointBezier(start, control1, control2, end));
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
        m_bezier.generateByPID(
                0.001,
                m_controller,
                m_constants.getMinSpeed().get(Unit.Type.Meters),
                m_constants.getMaxSpeed().get(Unit.Type.Meters),
                m_constants.getMaxAcceleration().get(Unit.Type.Meters),
                m_constants.getDeltaTime()
        );
    }

    @Override
    public Pair<Rotation2d, Rotation2d> angles() {
        return new Pair(
                this.m_bezier.getWaypoints()[0].getHeading(),
                this.m_bezier.getWaypoints()[2].getHeading()
        );
    }

    @Override
    public void flipX() {
        m_bezier.flipX();
    }

    public boolean connectedTo(Segment seg) {
        return seg.getWaypoints()[0].equals(getWaypoints()[getWaypoints().length - 1]) || seg.getWaypoints()[seg.getWaypoints().length - 1].equals(getWaypoints()[0]);
    }

    @Override
    public void setConstants(AutonomousConstants constants) {
        m_controller = constants.getPID();
        m_constants = constants;
    }

    @Override
    public FourPointBezier getPathObject() {
        return this.m_bezier;
    }

    @Override
    public boolean finishedGeneration() {
        return !m_bezier.getPoints().isEmpty();
    }

    public void saveToFile(File file, boolean overrideFile) {
        if (file.exists()) {
            if (!overrideFile) {
                throw new RuntimeException("File already exists? If you want to override " + file.getName() + ", then set overrideFile to true");
            } else {
                System.out.println("Note: Overriding file " + file.getName());
            }
        }

        if (!file.getName().endsWith(".json")) {
            throw new RuntimeException("File must be a .json file");
        }

        Gson gson = new Gson();

        JsonObject obj = new JsonObject();

        Waypoint[] waypoints = m_bezier.getWaypoints();

        obj.add("waypoints", waypointsToJSONList(waypoints));

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.write(file.toPath(), gson.toJson(obj).getBytes());
            } else {
                throw new RuntimeException("Unsupported Android version");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "BezierSegment: " + m_bezier.toString();
    }
}

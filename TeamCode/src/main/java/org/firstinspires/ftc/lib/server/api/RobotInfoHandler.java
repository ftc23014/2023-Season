package org.firstinspires.ftc.lib.server.api;

import android.content.res.AssetManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.odometry.MecanumOdometry;
import org.firstinspires.ftc.lib.pathing.FourPointBezier;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.server.util.Route;
import org.firstinspires.ftc.lib.simulation.Simulation;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.utils.FileUtils;
import org.firstinspires.ftc.teamcode.subsystems.vision.VisionSubsystem;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;


public class RobotInfoHandler extends Route {

    @Override
    public String getRoute() {
        return "/api/position";
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        String path = session.getUri().substring(getRoute().length());

        //if (path.equalsIgnoreCase("/position")) {
        Pose2d currentPosition;

        if (MecanumOdometry.hasInstance()) {
            currentPosition = MecanumOdometry.getInstance().getPose();
        } else {
            currentPosition = new Pose2d(0, 0, new Rotation2d(0));
        }

        JsonObject position = new JsonObject();

        position.addProperty("x", currentPosition.getX());
        position.addProperty("y", currentPosition.getY());
        position.addProperty("rotation", currentPosition.getRotation().getDegrees());

        JsonObject visionPos = new JsonObject();

        visionPos.addProperty("x", 0);
        visionPos.addProperty("y", 0);
        visionPos.addProperty("rotation", 0);

        position.add("vision", visionPos);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", position.toString());
        //}

       // return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND,"text/plain","404 NOT FOUND");
    }
}

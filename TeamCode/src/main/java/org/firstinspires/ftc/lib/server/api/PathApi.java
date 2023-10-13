package org.firstinspires.ftc.lib.server.api;

import android.content.res.AssetManager;
import android.util.JsonReader;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.pathing.FourPointBezier;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.pathing.segments.Segment;
import org.firstinspires.ftc.lib.server.util.Route;
import org.firstinspires.ftc.lib.simulation.Simulation;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.lib.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

public class PathApi extends Route {

    @Override
    public String getRoute() {
        return "/api/paths";
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        String path = session.getUri().substring(getRoute().length());

        if (session.getMethod() == NanoHTTPD.Method.POST) {
            Map<String, String> files = new HashMap<>();


            //get body
            try {
                session.parseBody(files);
            } catch (IOException | NanoHTTPD.ResponseException ioe) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }

            if (path.equalsIgnoreCase("/send")) {
                String json = files.get("postData");

                Gson gson = new Gson();

                JsonObject object = gson.fromJson(json, JsonObject.class);

                int count = object.get("waypoint_count").getAsInt();

                /**
                 * {
                 *     "waypoints": [
                 *       {
                 *         x: 0 (cm),
                 *         y: 0 (cm),
                 *         heading: 0 (degrees)
                 *         type: 0 for HARD, 1 for SOFT/CONTROL
                 *       },
                 *     ],
                 *  }
                 *
                 */

                ArrayList<Waypoint> waypoint_list = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    String waypointInfo = object.get("waypoint_" + i).getAsString();

                    String[] split_info = waypointInfo.split(",");

                    double x = Double.parseDouble(split_info[0]);
                    double y = Double.parseDouble(split_info[1]);
                    double heading = Double.parseDouble(split_info[2]);
                    int type = Integer.parseInt(split_info[3]);

                    Waypoint w = new Waypoint(
                            new Translation2d(x, y),
                            Rotation2d.fromDegrees(heading),
                            type == 0 ? Waypoint.Type.HARD : Waypoint.Type.SOFT
                    );

                    waypoint_list.add(w);
                }

                System.out.println("[::/api/paths/send] new path received with " + waypoint_list.size() + " waypoints");

                ArrayList<BezierSegment> seg = new ArrayList<>();

                Waypoint[] construction = new Waypoint[4];
                int constructionIndex = 0;

                for (int i = 0; i < waypoint_list.size(); i++) {
                    construction[constructionIndex] = waypoint_list.get(i);

                    constructionIndex++;

                    if (constructionIndex == 4) {
                        seg.add(new BezierSegment(new FourPointBezier(
                                construction[0],
                                construction[1],
                                construction[2],
                                construction[3]
                        )));

                        constructionIndex = 1;
                        construction = new Waypoint[4];
                        construction[0] = waypoint_list.get(i);
                    }
                }

                String fileName = "new_traj_" + new Date().getTime() + ".json";

                File file = new File(
                        "TeamCode/src/main/res/raw/" + fileName
                );

                System.out.println(file.getAbsolutePath() + " - " + seg.size());

                BezierSegment.saveSegmentsToFile(
                        file, seg.toArray(new BezierSegment[0])
                );

                System.out.println("[::/api/paths/send] new path saved to " + fileName + " under the raw res folder.");

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "200 OK");
            }
        } else if (session.getMethod() == NanoHTTPD.Method.GET) {
            if (path.equalsIgnoreCase("/get")) {
                if (Simulation.inSimulation()) {
                    //readdir the raw res folder
                    File file = new File(
                            "TeamCode/src/main/res/raw/"
                    );

                    File[] files = file.listFiles();

                    Gson gson = new Gson();

                    JsonArray array = new JsonArray();

                    for (File f : files) {
                        if (f.getName().endsWith(".json")) {
                            JsonObject object = new JsonObject();

                            object.addProperty("name", f.getName());
                            object.addProperty("path", f.getName().replace(".json", ""));

                            array.add(object);
                        }
                    }

                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(array));
                } else {
                    AssetManager assets = Subsystem.getAppContext().getResources().getAssets();

                    try {
                        String[] files = assets.list("raw");

                        Gson gson = new Gson();

                        JsonArray array = new JsonArray();

                        for (String f : files) {
                            if (f.endsWith(".json")) {
                                JsonObject object = new JsonObject();

                                object.addProperty("name", f);
                                object.addProperty("path", f.replace(".json", ""));

                                array.add(object);
                            }
                        }

                        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(array));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (path.startsWith("/get/")) {
                String filename = path.substring("/get/".length());
                //file is located in the raw res folder
                if (Simulation.inSimulation()) {
                    //can just read the file from the raw res folder
                    File file = new File(
                            "TeamCode/src/main/res/raw/" + filename + ".json"
                    );

                    if (file.exists()) {
                        try {
                            String content = FileUtils.read(file);

                            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", content);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 NOT FOUND");
                    }
                } else {
                    AssetManager assets = Subsystem.getAppContext().getResources().getAssets();

                    try {
                        InputStream stream = assets.open("raw/" + filename + ".json");

                        String content = FileUtils.read(stream);

                        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 NOT FOUND");
    }
}

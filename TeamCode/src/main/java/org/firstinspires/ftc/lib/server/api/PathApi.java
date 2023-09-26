package org.firstinspires.ftc.lib.server.api;

import android.util.JsonReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.server.util.Route;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "200 OK");
            }
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 NOT FOUND");
    }
}

package org.firstinspires.ftc.lib.odometry.motion;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.server.util.Route;

import java.util.ArrayList;

public class MotionAPI extends Route {
    protected static ArrayList<ExpectedRelativeMotion> expectedRelativeMotions = new ArrayList<>();

    @Override
    public String getRoute() {
        return "/api/motion";
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        String path = session.getUri().replace(getRoute(), "");

        Gson gson = new Gson();

        //If there's a .../get/profiles request, return a list of the expected relative motion profile names.
        if (path.equalsIgnoreCase("get/profiles")) {
            JsonObject obj = new JsonObject();

            JsonArray profileNames = new JsonArray();

            for (ExpectedRelativeMotion prof : expectedRelativeMotions) {
                profileNames.add(prof.getName());
            }

            obj.add("profiles", profileNames);

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(obj));
        } else if (path.equalsIgnoreCase("get")) { //if it's just /get, return the information of the first one
            if (expectedRelativeMotions.isEmpty()) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{ \"msg\": \"404 NOT FOUND, NO INITIALIZATION OF MOTION PROFILES.\" }");
            }

            JsonObject json = expectedRelativeMotions.get(0).asJSON();

            json.addProperty("name", expectedRelativeMotions.get(0).getName());

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(json));
        } else if (path.startsWith("get/")) { //if it's get/[name], then we'll return the information of that specific profile with that name.
            String profileName = path.replace("get/", "");

            for (ExpectedRelativeMotion prof : expectedRelativeMotions) {
                if (!prof.getName().equalsIgnoreCase(profileName)) continue;

                JsonObject json = prof.asJSON();

                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(json));
            }
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 NOT FOUND");
    }
}

package org.firstinspires.ftc.lib.simulation;

import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.server.util.Route;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;

public class SimulationAPI extends Route {
    public SimulationAPI() {
        super();
    }

    @Override
    public String getRoute() {
        return "/api/simulation";
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().replace("/api/simulation", "");

        if (uri.startsWith("/gamemode")) {
            //check if was a POST request
            if (session.getMethod() != NanoHTTPD.Method.POST) {
                if (uri.equalsIgnoreCase("/gamemode")) {
                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", Simulation.getInstance().getGameMode().toString());
                }
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.METHOD_NOT_ALLOWED, "text/plain", "405 Method Not Allowed");
            }

            Map<String, String> files = new HashMap<>();

            //get body
            try {
                session.parseBody(files);
            } catch (IOException | NanoHTTPD.ResponseException ioe) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }

            if (uri.equalsIgnoreCase("/gamemode/enter")) {
                String mode = files.get("postData");

                if (mode.equalsIgnoreCase("autonomous")) {
                    Simulation.getInstance().gameModeChange(Simulation.GameMode.AUTONOMOUS);
                } else if (mode.equalsIgnoreCase("teleop")) {
                    Simulation.getInstance().gameModeChange(Simulation.GameMode.TELEOP);
                } else {
                    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "400 Bad Request");
                }
            } else if (uri.equalsIgnoreCase("/gamemode/exit")) {
                Simulation.getInstance().gameModeChange(Simulation.GameMode.DISABLED);
            } else {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "400 Bad Request");
            }
        } else if (uri.startsWith("/info")) {
            String infoPiece = uri.replace("/info/", "");

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "200 OK " + infoPiece);
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/plain", "200 OK");
    }
}

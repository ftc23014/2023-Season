package org.firstinspires.ftc.lib.server.util;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HTTPServer extends NanoHTTPD {
    private static HTTPServer instance;

    public static HTTPServer getInstance() {
        if (instance == null) {
            try {
                instance = new HTTPServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }


    private ArrayList<Route> m_routes = new ArrayList<>();

    public HTTPServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Web server running at http://localhost:8080/!");
    }

    public void addRoute(Route route) {
        getInstance().m_routes.add(route);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //get the URI requested by the client
        String uri = session.getUri().toLowerCase();

        HashMap<Double, Route> bestMatch = new HashMap<>();

        for (Route route : m_routes) {
            if (route.getRoute().equals(uri) && route.exactMatch()) {
                return route.getResponse(session);
            } else if (!route.exactMatch() && uri.startsWith(route.getRoute())) {
                bestMatch.put(((double) route.getRoute().length()) / uri.length(), route);
            }
        }

        if (!bestMatch.isEmpty()) {
            return bestMatch.get(bestMatch.keySet().stream().max(Double::compare).get()).getResponse(session);
        }

        return newFixedLengthResponse("404");
    }
}

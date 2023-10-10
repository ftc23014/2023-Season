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
        super(8000);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Web server running at http://localhost:8000/!");
    }

    public void addRoute(Route route) {
        getInstance().m_routes.add(route);
    }

    @Override
    public Response serve(IHTTPSession session) {
        //get the URI requested by the client
        String uri = session.getUri().toLowerCase();

        //check if preflight request
        if (session.getMethod() == Method.OPTIONS) {
            Response resp = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{ \"OK\": \"OK\" }");
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Access-Control-Allow-Headers", "*");
            resp.addHeader("Access-Control-Allow-Methods", "*");
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Max-Age", "86400");
            resp.addHeader("Access-Control-Expose-Headers", "*");
            //set status to 200
            return resp;
        }

        HashMap<Double, Route> bestMatch = new HashMap<>();

        for (Route route : m_routes) {
            if (route.getRoute().equals(uri) && route.exactMatch()) {
                NanoHTTPD.Response resp = route.getResponse(session);
                // Allow CORS

                resp.addHeader("Access-Control-Allow-Origin", "*");
                resp.addHeader("Access-Control-Allow-Headers", "*");
                resp.addHeader("Access-Control-Allow-Methods", "*");
                resp.addHeader("Access-Control-Allow-Credentials", "true");
                resp.addHeader("Access-Control-Max-Age", "86400");
                resp.addHeader("Access-Control-Expose-Headers", "*");

                resp.setChunkedTransfer(true);

                return resp;
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

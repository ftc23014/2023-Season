package org.firstinspires.ftc.lib.server.api;

import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.server.util.Route;

public class ApiHandler extends Route {
    @Override
    public String getRoute() {
        return "/api";
    }

    public boolean exactMatch() {
        return false;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        return null;
    }
}

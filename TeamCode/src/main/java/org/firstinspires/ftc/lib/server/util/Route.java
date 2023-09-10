package org.firstinspires.ftc.lib.server.util;

import fi.iki.elonen.NanoHTTPD;

public abstract class Route {
    public Route() {
        HTTPServer.getInstance().addRoute(this);
    }

    public abstract String getRoute();

    public boolean exactMatch() {
        return true;
    }

    public abstract NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session);
}

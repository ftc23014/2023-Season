package org.firstinspires.ftc.lib.server.util;

import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.simulation.Simulation;

import java.util.Objects;

public class StaticHost extends FileHost {
    private String m_file;

    public StaticHost(String file) {
        super(file);

        m_file = file;
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    @Override
    public String getRoute() {
        return "/" + m_file;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        String uri = session.getUri().replace("/" + m_file, "");

        if (!Simulation.inSimulation()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
        }

        if (session.getUri().equals(uri)) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.FORBIDDEN, "text/plain", "403 Forbidden");
        }

        if (uri.contains("..")) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.FORBIDDEN, "text/plain", "403 Forbidden");
        }

        String[] fileContent = this.getFileData(m_file + uri);

        if (fileContent[0].equals("404")) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, fileContent[1], fileContent[0]);
    }
}

package org.firstinspires.ftc.lib.server.util;

import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.simulation.Simulation;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

public class FileHost extends Route {
    private String m_file;

    public FileHost(String resourceRelativePath) {
        super();

        if (resourceRelativePath.contains("..")) {
            throw new IllegalArgumentException("File path cannot contain '..'");
        }

        m_file = resourceRelativePath;
    }

    @Override
    public String getRoute() {
        return "/files";
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    public String[] getFileData() {
        return getFileData(m_file);
    }

    public String[] getFileData(String readFile) {
        //get file in class
        String fileContent = "";

        if (!Simulation.inSimulation()) {
            return new String[] { "404", "text/plain" };
        }

        InputStream stream;

        try {
            URI base = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();

            String addition = "../../../../../src/main/resources/" + readFile;

            File file = new File(base.getPath() + addition);

            //Open file
            stream = file.toURI().toURL().openStream();

            //Read file
            int contentLength = stream.available();

            byte[] buffer = new byte[contentLength];
            stream.read(buffer);
            stream.close();

            fileContent = new String(buffer);

            //Get file extension
            String extension = file.getPath().substring(file.getPath().lastIndexOf('.') + 1);

            //Get MIME type
            String mimeType = "text/plain";

            switch (extension) {
                case "html":
                    mimeType = "text/html";
                    break;
                case "css":
                    mimeType = "text/css";
                    break;
                case "js":
                    mimeType = "text/javascript";
                    break;
                case "json":
                    mimeType = "application/json";
                    break;
                case "png":
                    mimeType = "image/png";
                    break;
                case "jpg":
                    mimeType = "image/jpeg";
                    break;
                case "gif":
                    mimeType = "image/gif";
                    break;
                case "svg":
                    mimeType = "image/svg+xml";
                    break;
                case "ico":
                    mimeType = "image/x-icon";
                    break;
            }

            return new String[] { fileContent, mimeType };
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[] { "404", "text/plain" };
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        //get file in class
        String[] fileContent = getFileData();

        if (fileContent[0].equals("404")) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "404");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, fileContent[1], fileContent[0]);
    }
}

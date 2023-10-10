package org.firstinspires.ftc.lib.server.api;


import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.TeleOp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * API Endpoint that sends commands to the command line via the web interface running on a different machine.
 * This is used to control the robot remotely (as in computer management).
 */
public class CmdHandler extends ApiHandler {
    @Override
    public String getRoute() {
        return "/api/cmd";
    }

    @Override
    public boolean exactMatch() {
        return false;
    }

    @Override
    public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
        //check if post request
        if (session.getMethod() == NanoHTTPD.Method.POST) {
            Map<String, String> files = new HashMap<>();

            //get body
            try {
                session.parseBody(files);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            //convert postData to json object
            String json = files.get("postData");
            JsonObject object = gson.fromJson(json, JsonObject.class);

            if (object == null) return NanoHTTPD.newFixedLengthResponse("ERROR Invalid request body");
            if (!object.has("command")) {
                return NanoHTTPD.newFixedLengthResponse("ERROR Invalid request body, no command specified");
            }

            //get command
            String command = object.get("command").getAsString();

            System.out.println("[CMD] Received command: " + command);

            TeleOp.getTelemetry().addLine("CMD: " + command);

            //run command
            try {
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                String total = "";

                while ((line = reader.readLine()) != null) {
                    total += line + "\n";
                }

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (!process.isAlive()) {
                                return;
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            process.destroyForcibly();
                        } else {
                            process.destroy();
                        }

                    }
                };

                Timer timer = new Timer();
                timer.schedule(task, 1000);

                TeleOp.getTelemetry().addLine("waiting for process...");

                process.waitFor();

                TeleOp.getTelemetry().addLine("done!");

                process.destroy();

                TeleOp.getTelemetry().addLine(total);

                return NanoHTTPD.newFixedLengthResponse(total);
            } catch (Exception e) {
                e.printStackTrace();

                return NanoHTTPD.newFixedLengthResponse(e.getMessage());
            }

        } else {
            return NanoHTTPD.newFixedLengthResponse("Invalid request method");
        }
    }

}

package org.firstinspires.ftc.lib.server;

import org.firstinspires.ftc.lib.replay.log.writers.WebWriter;
import org.firstinspires.ftc.lib.server.api.ApiHandler;
import org.firstinspires.ftc.lib.server.api.CmdHandler;
import org.firstinspires.ftc.lib.server.api.PathApi;
import org.firstinspires.ftc.lib.server.api.RobotInfoHandler;
import org.firstinspires.ftc.lib.server.util.FileHost;
import org.firstinspires.ftc.lib.server.util.HTTPServer;
import org.firstinspires.ftc.lib.server.util.Route;
import org.firstinspires.ftc.lib.server.util.StaticHost;

public class Server {
    private HTTPServer m_server;

    public static void addRoute(Route route) {
        HTTPServer.getInstance().addRoute(route);
    }

    public Server() {
        m_server = HTTPServer.getInstance();

        m_server.addRoute(new StaticHost("public"));
        m_server.addRoute(new FileHost("public/home/index.html") {
            @Override
            public String getRoute() {
                return "/";
            }
        });

        m_server.addRoute(new FileHost("public/drivestation-sim/driverstation.html") {
            @Override
            public String getRoute() {
                return "/drivestation";
            }
        });

        m_server.addRoute(new FileHost("public/field/field.html") {
            @Override
            public String getRoute() {
                return "/field";
            }
        });

        m_server.addRoute(new FileHost("public/cmd/cmd.html") {
            @Override
            public String getRoute() {
                return "/cmd";
            }
        });

        m_server.addRoute(new FileHost("public/field/tracking.html") {
            @Override
            public String getRoute() {
                return "/tracking";
            }
        });

        m_server.addRoute(new FileHost("public/log/log.html") {
            @Override
            public String getRoute() {
                return "/log";
            }
        });

        m_server.addRoute(new FileHost("public/autoeditor/autoeditor.html") {
            @Override
            public String getRoute() {
                return "/autoeditor";
            }
        });

        m_server.addRoute(new ApiHandler());
        m_server.addRoute(new PathApi());
        m_server.addRoute(new CmdHandler());
        m_server.addRoute(new RobotInfoHandler());
        m_server.addRoute(new WebWriter.Endpoint());
    }
}

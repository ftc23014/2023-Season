package org.firstinspires.ftc.lib.server;

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
    }
}

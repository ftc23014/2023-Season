package org.firstinspires.ftc.lib.pathing;

import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.firstinspires.ftc.lib.pathing.segments.Segment;
import org.firstinspires.ftc.lib.systems.commands.Command;

import java.io.File;
import java.nio.file.Files;

public class Trajectory extends Command {

    private Segment[] m_segments;

    public Trajectory(Segment... segments) {
        super();

        m_segments = segments;
    }

    private void setConstants(AutonomousConstants constants) {
        for (Segment seg : m_segments) {
            seg.setConstants(constants);
        }
    }
}

package org.firstinspires.ftc.lib.pathing;

import org.firstinspires.ftc.lib.pathing.segments.Segment;
import org.firstinspires.ftc.lib.systems.commands.Command;

public class Trajectory extends Command {

    private Segment[] m_segments;

    public Trajectory(Segment ...segments) {
        super();

        m_segments = segments;
    }

    private void setConstants(AutonomousConstants constants) {
        for (Segment seg : m_segments) {
            seg.setConstants(constants);
        }
    }
}

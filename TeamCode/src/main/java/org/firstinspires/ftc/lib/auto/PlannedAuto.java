package org.firstinspires.ftc.lib.auto;

import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.SequentialCommand;

public class PlannedAuto {
    SequentialCommand command;
    AutonomousConstants constants;

    public PlannedAuto(AutonomousConstants constants, Command...commands) {
        command = new SequentialCommand(commands);

        for (Command command : commands) {
            if (command instanceof Trajectory) {
                Trajectory traj = ((Trajectory) command);
                traj.setConstants(constants);
                traj.generate();
            }
        }
    }

    public void start() {
        command.init();
    }

    public void loop() {
        command.execute();
    }


}

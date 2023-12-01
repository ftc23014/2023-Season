package org.firstinspires.ftc.lib.auto;

import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.IfOrSkipCommand;
import org.firstinspires.ftc.lib.systems.commands.SequentialCommand;

public class PlannedAuto {
    private SequentialCommand command;
    private AutonomousConstants constants;

    /**
     * Creates a new planned auto. Pretty much a glorified sequential command, but trajectory generation built in. Note: NOT A COMMAND.
     * @param constants Autonomous constants for the auto.
     * @param commands The list of commands to complete for the autonomous. This is sequential, so each command is finished IN ORDER.
     */
    public PlannedAuto(AutonomousConstants constants, Command...commands) {
        //we'll create a new sequential command to handle everything. don't want to rewrite it again :P
        command = new SequentialCommand(commands);

        //we'll loop through and look for trajectories. if there's a trajectory, set the constants and generate the path(s).
        for (Command command : commands) {
            if (command instanceof Trajectory) {
                Trajectory traj = ((Trajectory) command);
                traj.setConstants(constants);
                traj.generate();
            }

            if (command instanceof IfOrSkipCommand) {
                if (((IfOrSkipCommand) command).getCommand() instanceof Trajectory) {
                    Trajectory traj = ((Trajectory) ((IfOrSkipCommand) command).getCommand());
                    traj.setConstants(constants);
                    traj.generate();
                }
            }
        }
    }

    /**
     * Gets the autonomous constants
     * @return Autonomous constants
     */
    public AutonomousConstants getConstants() {
        return constants;
    }

    /**
     * Starts the auto. Run this in Autonomous#start().
     */
    public void start() {
        command.init();
    }

    /**
     * Runs the loop of the auto. Run this in Autonomous#loop().
     */
    public void loop() {
        command.execute();
    }

    public void stop() {
        command.cancel();
    }


}

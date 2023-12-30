package org.firstinspires.ftc.lib.systems.commands;

import java.util.ArrayList;

public class ParallelCommand extends Command {
    ArrayList<Command> commands;

    public ParallelCommand(Command ...commands) {
        this.commands = new ArrayList<>();
        for (Command command : commands) {
            this.commands.add(command);
        }
    }

    @Override
    public void init() {
        for (Command command : commands) {
            command.init();
        }
    }

    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public boolean hasFinished() {
        for (Command command : commands) {
            if (!command.hasFinished()) {
                return false;
            }
        }
        return true;
    }

    public Command[] getCommands() {
        return commands.toArray(new Command[0]);
    }
}

package org.firstinspires.ftc.lib.systems.commands;

import org.firstinspires.ftc.teamcode.R;

import java.util.ArrayList;
import java.util.Collections;

public class SequentialCommand extends Command {

    int currentCommand = 0;
    public ArrayList<Command> commands = new ArrayList<Command>();

    public SequentialCommand(Command... commands) {
        this.commands = new ArrayList<Command>();
        Collections.addAll(this.commands, commands);
    }

    @Override
    public void init() {
        if (currentCommand < commands.size()) {
            commands.get(currentCommand).init();
        }
    }

    @Override
    public void execute() {
        if (currentCommand >= commands.size()) return;

        commands.get(currentCommand).execute();

        if (commands.get(currentCommand).hasFinished()) {
            currentCommand++;
            if (currentCommand < commands.size()) {
                commands.get(currentCommand).init();
            }
        }
    }

    @Override
    public boolean hasFinished() {
        return currentCommand >= commands.size();
    }

    public void cancel() {
        //stops the current command
        if (currentCommand < commands.size()) {
            commands.get(currentCommand).cancel();
        }
    }
}

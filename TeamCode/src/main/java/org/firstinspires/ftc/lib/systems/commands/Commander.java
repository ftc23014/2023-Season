package org.firstinspires.ftc.lib.systems.commands;

import java.util.ArrayList;

public class Commander {
    public static ArrayList<Command> activeCommands = new ArrayList<Command>();

    public static void activateCommand(Command command) {
        activeCommands.add(command);
        command.init();
    }

    public static void update() {
        for (int i = 0; i < activeCommands.size(); i++) {
            Command command = activeCommands.get(i);
            command.execute();
            if (command.hasFinished() || command.isCancelled()) {
                activeCommands.remove(i);
                i--;
            }
        }
    }
}

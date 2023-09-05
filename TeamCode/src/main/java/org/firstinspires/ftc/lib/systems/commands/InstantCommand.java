package org.firstinspires.ftc.lib.systems.commands;

import java.lang.reflect.Method;

public class InstantCommand extends Command {

    private final Method method;

    public InstantCommand(Method method) {
        this.method = method;
    }

    @Override
    public void init() {
        try {
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

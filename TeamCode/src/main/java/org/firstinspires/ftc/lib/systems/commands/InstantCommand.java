package org.firstinspires.ftc.lib.systems.commands;

import org.firstinspires.ftc.lib.Lambda;

import java.lang.reflect.Method;

public class InstantCommand extends Command {

    private final Method method;
    private final Lambda lambda;

    public InstantCommand(Method method) {
        this.method = method;
        this.lambda = null;
    }

    public InstantCommand(Lambda lambda) {
        this.lambda = lambda;
        this.method = null;
    }

    @Override
    public void init() {
        if (method != null) {
            try {
                method.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            lambda.run();
        }
    }
}

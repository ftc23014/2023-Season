package org.firstinspires.ftc.lib.systems.commands;

public class WaitCommand extends Command {
    private double seconds;
    private double startTime;

    public WaitCommand(double seconds) {
        super();

        this.seconds = seconds;
    }

    @Override
    public void init() {
        super.init();

        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean hasFinished() {
        return System.currentTimeMillis() - startTime >= seconds * 1000;
    }
}

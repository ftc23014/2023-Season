package org.firstinspires.ftc.lib.systems.commands;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.UUID;

public class Command {
    private UUID uuid = UUID.randomUUID();

    public UUID getUUID() {
        return uuid;
    }

    private boolean cancelled = false;

    public void init() {

    }

    public void execute() {

    }

    public boolean hasFinished() {
        return true;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void activate() {
        Commander.activateCommand(this);
    }

    protected Telemetry telemetry() {
        return Subsystem.telemetry();
    }
}

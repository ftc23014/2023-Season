package org.firstinspires.ftc.lib.systems;

import org.firstinspires.ftc.lib.replay.Replayable;

public class Subsystem extends Replayable {

    public Subsystem() {
        super();
        Subsystems.addSubsystem(this);
    }

    public void init() {
        System.out.println("Enabled " + getBaseName() + "! Override onEnable and whileEnable to add functionality!");
    }

    public void periodic() {

    }

    public void onDisable() {
        System.out.println("Disabled " + getBaseName() + "! Override onDisable to add functionality!");
    }

    @Override
    public String getBaseName() {
        return this.getClass().getSuperclass().getName();
    }

    @Override
    public void replayInit() {

    }

    @Override
    public void exitReplay() {

    }
}

package org.firstinspires.ftc.lib.systems;

import org.firstinspires.ftc.lib.replay.Replayable;

public class Subsystem extends Replayable {
    public Subsystem() {
        super();
    }

    public void onEnable() {
        System.out.println("Enabled " + getBaseName() + "! Override onEnable and whileEnable to add functionality!");
    }

    public void whileEnable() {

    }

    public void onDisable() {
        System.out.println("Disabled " + getBaseName() + "! Override onDisable to add functionality!");
    }

    @Override
    public String getBaseName() {
        return "Subsystem";
    }

    @Override
    public void replayInit() {

    }

    @Override
    public void exitReplay() {

    }
}

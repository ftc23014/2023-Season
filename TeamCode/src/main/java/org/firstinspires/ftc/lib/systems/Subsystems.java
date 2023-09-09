package org.firstinspires.ftc.lib.systems;

import java.util.ArrayList;

public class Subsystems {
    private static ArrayList<Subsystem> subsystems = new ArrayList<>();

    protected static void addSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    public static void onInit() {
        for (Subsystem subsystem : subsystems) {
            subsystem.init();
        }
    }

    public static void periodic() {
        for (Subsystem subsystem : subsystems) {
            subsystem.periodic();
        }
    }

    public static void onDisable() {
        for (Subsystem subsystem : subsystems) {
            subsystem.onDisable();
        }
    }
}
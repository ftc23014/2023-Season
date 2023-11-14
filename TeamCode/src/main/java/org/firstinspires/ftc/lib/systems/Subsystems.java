package org.firstinspires.ftc.lib.systems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;

public class Subsystems {
    private static ArrayList<Subsystem> subsystems = new ArrayList<>();

    protected static OpMode alternateOpMode = null;

    protected static void addSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    public static void onInit() {
        for (Subsystem subsystem : subsystems) {
            subsystem.init();
        }
    }

    public static void onInit(OpMode alternate) {
        alternateOpMode = alternate;

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
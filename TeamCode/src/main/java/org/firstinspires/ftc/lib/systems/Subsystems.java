package org.firstinspires.ftc.lib.systems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.UUID;

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

        ArrayList<UUID> exclusionList = new ArrayList<>();

        for (Subsystem subsystem : subsystems) {
            subsystem.initDefaultCommands(exclusionList);
            subsystem.init();

            exclusionList.addAll(subsystem.getCommandUUIDs());
        }
    }

    public static void periodic() {
        ArrayList<UUID> exclusionList = new ArrayList<>();

        for (Subsystem subsystem : subsystems) {
            subsystem.runDefaultCommands(exclusionList);
            subsystem.periodic();

            exclusionList.addAll(subsystem.getCommandUUIDs());
        }
    }

    public static void onDisable() {
        ArrayList<UUID> exclusionList = new ArrayList<>();

        for (Subsystem subsystem : subsystems) {
            subsystem.cancelDefaultCommands(exclusionList);
            subsystem.onDisable();

            exclusionList.addAll(subsystem.getCommandUUIDs());
        }
    }
}
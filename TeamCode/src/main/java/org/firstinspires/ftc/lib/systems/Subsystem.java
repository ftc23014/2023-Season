package org.firstinspires.ftc.lib.systems;

import android.content.Context;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.replay.Replayable;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.TeleOp;
import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

import java.util.ArrayList;
import java.util.UUID;

public class Subsystem extends Replayable {

    private ArrayList<Command> defaultCommands = new ArrayList<>();

    public Subsystem() {
        super();
        Subsystems.addSubsystem(this);
    }

    public void addDefaultCommand(Command command) {
        defaultCommands.add(command);
    }

    public void removeDefaultCommand(Command command) {
        defaultCommands.remove(command);
    }

    protected ArrayList<UUID> getCommandUUIDs() {
        ArrayList<UUID> uuids = new ArrayList<>();

        for (Command command : defaultCommands) {
            uuids.add(command.getUUID());
        }

        return uuids;
    }

    protected void initDefaultCommands(ArrayList<UUID> exclusionList) {
        for (Command command : defaultCommands) {
            if (exclusionList.contains(command.getUUID())) {
                continue;
            }

            command.init();
        }
    }

    public void init() {
        System.out.println("Enabled " + getBaseName() + "! Override onEnable and whileEnable to add functionality!");
    }

    protected void runDefaultCommands(ArrayList<UUID> exclusionList) {
        for (Command command : defaultCommands) {
            if (exclusionList.contains(command.getUUID())) {
                continue;
            }

            command.execute();
            if (command.hasFinished()) {
                removeDefaultCommand(command);
            }
        }
    }

    public void periodic() {

    }

    protected void cancelDefaultCommands(ArrayList<UUID> exclusionList) {
        for (Command command : defaultCommands) {
            if (exclusionList.contains(command.getUUID())) {
                continue;
            }

            if (!command.hasFinished()) {
                command.cancel();
            }
        }
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

    public static HardwareMap getHardwareMap() {
        if (Subsystems.alternateOpMode != null)
             return Subsystems.alternateOpMode.hardwareMap;

        if (TeleOp.hasInstance() && TeleOp.getHardwareMap() != null) {
            return TeleOp.getHardwareMap();
        } else if (Autonomous.hasInstance() && Autonomous.getHardwareMap() != null) {
            return Autonomous.getHardwareMap();
        } else {
            throw new RuntimeException("No hardware map has been found!");
        }
    }

    public static Context getAppContext() {
        return getHardwareMap().appContext;
    }

    public static Telemetry telemetry() {
        if (Subsystems.alternateOpMode != null)
            return Subsystems.alternateOpMode.telemetry;

        if (TeleOp.hasInstance() && TeleOp.getTelemetry() != null) {
            return TeleOp.getTelemetry();
        } else if (Autonomous.hasInstance() && Autonomous.getTelemetry() != null) {
            return Autonomous.getTelemetry();
        } else {
            return null;
        }
    }

    public static Gamepad gamepad() {
        return gamepad(false);
    }

    public static Gamepad gamepad(boolean second) {
        if (TeleOp.hasInstance()) {
            return TeleOp.gamepad(!second);
        } else if (Subsystems.alternateOpMode != null) {
            return Subsystems.alternateOpMode.gamepad1;
        } else {
            return null; // don't return since in autonomous we don't have a gamepad
        }
    }
}

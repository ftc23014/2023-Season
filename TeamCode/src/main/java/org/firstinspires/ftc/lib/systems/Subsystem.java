package org.firstinspires.ftc.lib.systems;

import android.content.Context;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.replay.Replayable;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.TeleOp;
import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

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

    public static HardwareMap getHardwareMap() {
        if (Subsystems.alternateHardwareMap != null)
             return Subsystems.alternateHardwareMap;

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
        if (TeleOp.hasInstance() && TeleOp.getTelemetry() != null) {
            return TeleOp.getTelemetry();
        } else if (Autonomous.hasInstance() && Autonomous.getTelemetry() != null) {
            return Autonomous.getTelemetry();
        } else {
            return null;
        }
    }
}

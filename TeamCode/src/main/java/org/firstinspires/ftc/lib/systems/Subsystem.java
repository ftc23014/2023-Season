package org.firstinspires.ftc.lib.systems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.replay.Replayable;
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

    protected HardwareMap getHardwareMap() {
        if (TeleOp.hasInstance() && TeleOp.getHardwareMap() != null) {
            return TeleOp.getHardwareMap();
        } else if (Autonomous.hasInstance() && Autonomous.getHardwareMap() != null) {
            return Autonomous.getHardwareMap();
        } else {
            return null;
        }
    }
}

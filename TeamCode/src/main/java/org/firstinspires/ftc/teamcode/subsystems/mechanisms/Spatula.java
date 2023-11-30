package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Spatula extends Subsystem {
    private final double SPATULA_UP = 2;
    private final double SPATULA_DOWN = 0.5;

    private final double LOCK_DOWN = 0.2;

    private final double LOCK_UP = 0.7;



    private Servo spatula;

    private Servo lock;

    public Spatula() {
        super();

        spatula = getHardwareMap().servo.get("Spatula");
        lock = getHardwareMap().servo.get("Lock");
    }

    public Command deploy() {
        return new InstantCommand(this::setDeploy);
    }

    public Command retract() {
        return new InstantCommand(this::setRetract);
    }

    public void setDeploy() {
        spatula.setPosition(SPATULA_UP);
    }

    public void setLock() {
        lock.setPosition(LOCK_DOWN);
    }

    public void removeLock() {
        lock.setPosition(LOCK_UP);
    }

    public void setRetract() {
        spatula.setPosition(SPATULA_DOWN);
    }
}

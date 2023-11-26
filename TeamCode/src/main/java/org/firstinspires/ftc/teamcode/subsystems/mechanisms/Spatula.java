package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Spatula extends Subsystem {
    private final double SPATULA_UP = 0.6;
    private final double SPATULA_DOWN = 0;

    private Servo spatula;

    public Spatula() {
        super();

        spatula = getHardwareMap().servo.get("Spatula");
    }

    public Command spatulaUp() {
        return new InstantCommand(this::setSpatulaUp);
    }

    public Command spatulaDown() {
        return new InstantCommand(this::setSpatulaDown);
    }

    public void setSpatulaUp() {
        spatula.setPosition(SPATULA_UP);
    }

    public void setSpatulaDown() {
        spatula.setPosition(SPATULA_DOWN);
    }
}

package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Spatula extends Subsystem {
    private final double SPATULA_UP = 0.377;
    private final double SPATULA_DOWN = 0.97;

    private Servo spatula;

    public Spatula() {
        super();

        spatula = getHardwareMap().servo.get("Spatula");
    }

    public void setPosition(double position) {
        spatula.setPosition(position);
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

    public void setRetract() {
        spatula.setPosition(SPATULA_DOWN);
    }
}

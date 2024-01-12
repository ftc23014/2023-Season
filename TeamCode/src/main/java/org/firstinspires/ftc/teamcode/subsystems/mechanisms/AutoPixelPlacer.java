package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class AutoPixelPlacer extends Subsystem {
    private Servo autopixelplacer;

    public AutoPixelPlacer() {
        super();
    }

    @Override
    public void init() {
        autopixelplacer = getHardwareMap().get(Servo.class, "autoPixelPlacer");
    }

    public Command setDeploy() {
        return new InstantCommand(this::deploy);
    }

    public void deploy() {
        autopixelplacer.setPosition(0.35 /*  to let go of pixel*/);
    }

    public Command setRetract() {
        return new InstantCommand(this::retract);
    }

    public void retract() {
        autopixelplacer.setPosition(0.3); /* goes back to base position*/
    }

    public Command stopCommand() {
        return new InstantCommand(this::stop);
    }

    public void stop() { //TODO: check that these value are fine
        autopixelplacer.setPosition(0);
    }

    @Override
    public void periodic() {
    }
}

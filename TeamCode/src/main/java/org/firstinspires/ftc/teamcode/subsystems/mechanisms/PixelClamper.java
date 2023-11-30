package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class PixelClamper extends Subsystem {

    //TODO: adjust
    private final double PIXEL_CLAMPER_UP = 0.8;
    private final double PIXEL_CLAMPER_DOWN = 0.2;

    private Servo clamper;

    public PixelClamper() {
        super();

        clamper = getHardwareMap().servo.get("Spatula");
    }

    public Command deploy() {
        return new InstantCommand(this::setDeploy);
    }

    public Command retract() {
        return new InstantCommand(this::setRetract);
    }

    public void setDeploy() {
        clamper.setPosition(PIXEL_CLAMPER_DOWN);
    }

    public void setRetract() {
        clamper.setPosition(PIXEL_CLAMPER_UP);
    }
}

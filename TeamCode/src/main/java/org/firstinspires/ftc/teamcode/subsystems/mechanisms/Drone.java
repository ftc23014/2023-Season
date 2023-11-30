package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Drone extends Subsystem {
    private final double Drone_Shoot = 1;
    private final double Drone_Load = 0.4;

    private Servo drone;

    public Drone() {
        super();

        //drone = getHardwareMap().servo.get("Drone");
    }

    public Command deploy() {
        return new InstantCommand(this::setDeploy);
    }

    public Command retract() {
        return new InstantCommand(this::setRetract);
    }

    public void setDeploy() {
        drone.setPosition(Drone_Shoot);
    }
    public void setRetract() {
        drone.setPosition(Drone_Load);
    }
}

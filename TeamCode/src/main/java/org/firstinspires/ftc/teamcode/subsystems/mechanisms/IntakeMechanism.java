package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.lib.systems.Subsystem;

public class IntakeMechanism extends Subsystem {
    /**
     * TODO:
     * intake
     * move motor
     * yeah idk
     */

    private DcMotor intakeMotor;

    public IntakeMechanism() {
        super();
    }

    @Override
    public void init() {
        intakeMotor = getHardwareMap().get(DcMotor.class, "intake_motor");
    }
}

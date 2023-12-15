package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Unit;

public class Constants {

    public static class Autonomous {
        public final static AutonomousConstants autonomousConstants = new AutonomousConstants(
                new Unit(1, Unit.Type.Meters),
                new Unit(0.1, Unit.Type.Meters),
                new Unit(0.5, Unit.Type.Meters),
                8,
                new PIDController(0.4, 0.00, 0.00),
                1d / 32d
        );

        public static final boolean usePhysicsCalculations = false;
    }

}

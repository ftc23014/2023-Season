package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.math.WPIPIDController;

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
        public static final double centripetalForceMultiplier = -0.5d;
    }

    public static class Odometry {
        public final static Unit horizontalDistance = new Unit(34.55, Unit.Type.Centimeters);
        public final static Unit verticalDistance = new Unit(10.95, Unit.Type.Centimeters);
        public final static Unit halfDistance = new Unit(10.95 / 2d, Unit.Type.Centimeters);
    }

    public enum Side {
        LEFT_RED,
        RIGHT_BLUE
    }

    public static Side currentSide = Side.RIGHT_BLUE;

}

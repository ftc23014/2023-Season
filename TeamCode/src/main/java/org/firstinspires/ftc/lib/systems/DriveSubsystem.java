package org.firstinspires.ftc.lib.systems;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;

public class DriveSubsystem extends Subsystem {
    public void drive(Translation2d translation, Rotation2d rotationSpeed, boolean fieldRelative, boolean openLoop) {

    }

    public Unit getVelocity() {
        return Unit.zero();
    }

    public Translation2d getVelocity2d() {
        return Translation2d.zero();
    }
}

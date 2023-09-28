package org.firstinspires.ftc.lib.odometry;

import org.firstinspires.ftc.lib.math.Translation2d;

public class MecanumOdometry {
    private Translation2d currentPosition;

    public MecanumOdometry(Translation2d currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void reset(Translation2d currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Translation2d getCurrentPosition() {
        return currentPosition;
    }
}

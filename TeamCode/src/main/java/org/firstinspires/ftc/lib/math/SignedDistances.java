package org.firstinspires.ftc.lib.math;

public class SignedDistances {
    public double rectangleDistance(Translation2d point, Translation2d rect_position, double rect_width, double rect_height) {
        double dx = Math.max(Math.abs(point.getX() - rect_position.getX()) - rect_width / 2, 0);
        double dy = Math.max(Math.abs(point.getY() - rect_position.getY()) - rect_height / 2, 0);
        return Math.sqrt(dx * dx + dy * dy);
    }
}

package org.firstinspires.ftc.lib.math;

public class SignedDistance {
    public static double signedCircleDistance(Translation2d one, Translation2d circle, double r) {
        return Math.signum(one.getX() - circle.getX()) * Math.sqrt(Math.pow(one.getX() - circle.getX(), 2) + Math.pow(one.getY() - circle.getY(), 2)) - r;
    }

    public static double signedSquareDistance(Translation2d pos, Translation2d rectTopLeft, double w, double h) {
        double x = pos.getX();
        double y = pos.getY();
        double x1 = rectTopLeft.getX();
        double y1 = rectTopLeft.getY();
        double x2 = x1 + w;
        double y2 = y1 + h;

        double dx = Math.max(Math.max(x1 - x, 0), Math.max(x - x2, 0));
        double dy = Math.max(Math.max(y1 - y, 0), Math.max(y - y2, 0));

        return Math.signum(x - x1 - w / 2) * Math.sqrt(dx * dx + dy * dy);
    }
}

package org.firstinspires.ftc.lib.field;

import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;

/**
 * This class is used to store information about the field.
 * It is used for pathing, odometry, etc.
 */
public class Field {
    public static Field field;

    /**
     * Creates a new field.
     */
    public static void init() {
        field = new CenterStageField();
    }

    public Unit getWidth() {
        return new Unit(0, Unit.Type.Inches);
    }

    public Unit getHeight() {
        return new Unit(0, Unit.Type.Inches);
    }

    public static class Obstacle {
        Translation2d position;
        Unit width;
        Unit height;

        public Obstacle(Unit x, Unit y, Unit w, Unit h) {
            position = new Translation2d(x.get(Unit.Type.Centimeters), y.get(Unit.Type.Centimeters));
            width = w;
            height = h;
        }

        public Obstacle() {
            position = new Translation2d(0,0);
            width = new Unit(0, Unit.Type.Inches);
            height = new Unit(0, Unit.Type.Inches);
        }
    }
}

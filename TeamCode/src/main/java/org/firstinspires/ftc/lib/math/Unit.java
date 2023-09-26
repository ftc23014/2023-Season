package org.firstinspires.ftc.lib.math;

public class Unit {
    public enum Type {
        Inches(2.54),
        Feet(30.48),
        Centimeters(1),
        Meters(100);

        private final double conversion_to_cm;

        Type(double conversion_to_cm) {
            this.conversion_to_cm = conversion_to_cm;
        }

        public double convert_to_cm(double value) {
            return value * conversion_to_cm;
        }

        public double cm_to(double value) {
            return value / conversion_to_cm;
        }
    }

    private final double value;
    private final Type type;

    public Unit(double value, Type type) {
        this.value = value;
        this.type = type;
    }

    public double get(Unit.Type type) {
        return type.cm_to(this.type.convert_to_cm(value));
    }

}

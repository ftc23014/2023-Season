package org.firstinspires.ftc.lib.math;

public class Unit {
    public enum Type {
        Inches(2.54, "in"),
        Feet(30.48, "ft"),
        Centimeters(1, "cm"),
        Meters(100, "m");

        private final double conversion_to_cm;
        private final String symbol;

        Type(double conversion_to_cm, String symbol) {
            this.conversion_to_cm = conversion_to_cm;
            this.symbol = symbol;
        }

        public double convert_to_cm(double value) {
            return value * conversion_to_cm;
        }

        public double cm_to(double value) {
            return value / conversion_to_cm;
        }
    }

    public static Unit zero() {
        return new Unit(0, Type.Centimeters);
    }

    public static double convert(double n, Type t1, Type t2) {
        return new Unit(n, t1).get(t2);
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

    public Unit clamp(Unit min, Unit max) {
        return new Unit(Math.max(min.get(type), Math.min(max.get(type), get(type))), type);
    }

    @Override
    public String toString() {
        return value + type.symbol;
    }
}

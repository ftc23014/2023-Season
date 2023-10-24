package org.firstinspires.ftc.lib.math;

public class TimeUtils {
    public static String millisecondsToSecondPrintable(long ms, int decimalPlaces) {
        double seconds = Math.round((ms / 1000d) * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);

        return seconds + "s";
    }
}

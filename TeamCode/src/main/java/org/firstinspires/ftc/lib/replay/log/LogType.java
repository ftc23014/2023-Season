package org.firstinspires.ftc.lib.replay.log;

public enum LogType {
    STRING,
    INT,
    DOUBLE,
    BOOLEAN,
    STRING_ARRAY,
    DOUBLE_ARRAY,
    BYTE_ARRAY,
    UNKNOWN;

    public String smallString() {
        switch(this) {
            case DOUBLE:
                return "d";
            case INT:
                return "i";
            case STRING_ARRAY:
                return "S";
            case DOUBLE_ARRAY:
                return "D";
            case BYTE_ARRAY:
                return "B";
            case BOOLEAN:
                return "b";
            default:
                return "s";
        }
    }

    public static LogType fromSmallString(String s) {
        switch (s) {
            case "s":
                return STRING;
            case "d":
                return DOUBLE;
            case "i":
                return INT;
            case "S":
                return STRING_ARRAY;
            case "D":
                return DOUBLE_ARRAY;
            case "B":
                return BYTE_ARRAY;
            case "b":
                return BOOLEAN;
            default:
                return STRING;
        }
    }

    public static LogType fromClass(Class<?> klass) {
        if (klass == String.class) return STRING;
        if (klass == Integer.class || klass == int.class) return INT;
        if (klass == Double.class || klass == double.class) return DOUBLE;
        if (klass == Boolean.class || klass == boolean.class) return BOOLEAN;
        if (klass == String[].class) return STRING_ARRAY;
        if (klass == Double[].class || klass == double[].class) return DOUBLE_ARRAY;
        if (klass == byte[].class) return BYTE_ARRAY;

        return UNKNOWN;
    }
}
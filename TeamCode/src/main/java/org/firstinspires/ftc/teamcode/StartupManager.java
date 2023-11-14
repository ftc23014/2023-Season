package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.lib.math.TimeUtils;
import org.firstinspires.ftc.lib.simulation.Simulation;
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public class StartupManager {
    public static class Check {
        private String name;
        private boolean initialized = false;
        private long timestampCreated;
        private long timestampInitialized;

        public Check(String name) {
            this.name = name;
            this.timestampCreated = System.currentTimeMillis();
        }

        public void fulfill() {
            this.initialized = true;
            this.timestampInitialized = System.currentTimeMillis();
            System.out.println("Fulfilled check " + name + " in " + TimeUtils.millisecondsToSecondPrintable(timestampInitialized - timestampCreated, 1) + "!");
        }
    }

    private static ArrayList<Check> checks = new ArrayList<>();

    public static void addCheck(Class caller, String name) {
        checks.add(new Check(caller.getName() + "/" + name));
    }

    public static void addGlobalCheck(String name) {
        checks.add(new Check(name));
    }

    public static void fulfillCheck(Class caller, String name) {
        for (Check check : checks) {
            if (!check.name.equalsIgnoreCase(caller.getName() + "/" + name)) continue;

            check.fulfill();
        }
    }

    public static void fulfillGlobalCheck(String name) {
        for (Check check : checks) {
            if (!check.name.equalsIgnoreCase(name)) continue;

            check.fulfill();
        }
    }

    public static boolean checkFulfilled(Class caller, String name) {
        for (Check check : checks) {
            if (!check.name.equalsIgnoreCase(caller.getName() + "/" + name)) continue;

            return check.initialized;
        }

        return false;
    }

    public static boolean checkGlobalFulfilled(String name) {
        for (Check check : checks) {
            if (!check.name.equalsIgnoreCase(name)) continue;

            return check.initialized;
        }

        return false;
    }

    public static void clear() {
        checks.clear();
    }

    public static void printChecks(Telemetry telemetry) {
        if (!Simulation.inSimulation()) {
            telemetry.addData("Checks Fulfilled", new Func<Object>() {
                @Override public String value() {
                    int fulfilled = 0;
                    for (Check check : checks) {
                        if (check.initialized) fulfilled++;
                    }

                    return fulfilled + "/" + checks.size();
                }
            });

            for (final Check check : checks) {
                telemetry.addData(check.name, new Func<String>() {
                    @Override
                    public String value() {
                        return "[CHECK] " + check.name + ": " +
                                (check.initialized ?
                                        "✓ (took " + TimeUtils.millisecondsToSecondPrintable(check.timestampInitialized - check.timestampCreated, 1) + " to initialize)" :
                                        "✗ (has taken "  + TimeUtils.millisecondsToSecondPrintable(System.currentTimeMillis() - check.timestampCreated, 1) + " to initialize)"
                                );
                    }
                });
            }

            telemetry.update();
        }
    }
}

package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.lib.field.Field;
import org.firstinspires.ftc.lib.replay.ReplayManager;
import org.firstinspires.ftc.lib.replay.log.writers.NoLog;
import org.firstinspires.ftc.lib.systems.Subsystems;

public class Robot {

    private static boolean hasInit = false;


    public static void init() {
        if (hasInit) return;
        hasInit = true;

        Field.init();

        /* Initialize the log writer and replay manager first. */

        //This will make all System.out.println() calls go to the log file as well as the console.
        ReplayManager.captureConsoleToLog();
        // Set the log writer to a new FileWriter, which will write the data to a file.
        ReplayManager.setWriter(new NoLog());
        // Initialize the replay manager, which will handle the logging of the data.
        ReplayManager.init();
    }
}
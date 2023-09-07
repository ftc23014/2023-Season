package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.lib.replay.ReplayManager;
import org.firstinspires.ftc.lib.replay.log.writers.FileWriter;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class TeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private boolean teleOpEnabled = false;

    @Override
    public void runOpMode() {
        // -- SETUP --
        setup();

        //Pause the code until the user presses the play button, or the competition starts.
        waitForStart();

        // -- ENABLE --
        teleOpEnabled = true;

        teleOpEnable();

        // -- MAIN LOOP --
        while (opModeIsActive()) {
            teleOpLoop();
        }

        // -- DISABLE --
        teleOpEnabled = false;
    }

    public void setup() {
        /* Initialize the log writer and replay manager first. */

        //This will make all System.out.println() calls go to the log file as well as the console.
        ReplayManager.captureConsoleToLog();
        // Set the log writer to a new FileWriter, which will write the data to a file.
        ReplayManager.setWriter(new FileWriter());
        // Initialize the replay manager, which will handle the logging of the data.
        ReplayManager.init();
    }

    /**
     * Whether the tele-op is enabled or not.
     * @return Whether the tele-op is enabled or not.
     * */
    public boolean isTeleOpEnabled() {
        return teleOpEnabled;
    }

    public void teleOpEnable() {

    }

    public void teleOpLoop() {

    }
}

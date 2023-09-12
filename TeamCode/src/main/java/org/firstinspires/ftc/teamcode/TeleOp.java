package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.lib.replay.ReplayManager;
import org.firstinspires.ftc.lib.replay.log.writers.FileWriter;
import org.firstinspires.ftc.lib.replay.log.writers.NoLog;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp
public class TeleOp extends OpMode {
    private ElapsedTime m_runtime = new ElapsedTime();

    private boolean m_teleOpEnabled = false;

    @Override
    public void init() {
        /* Initialize the log writer and replay manager first. */

        //This will make all System.out.println() calls go to the log file as well as the console.
        ReplayManager.captureConsoleToLog();
        // Set the log writer to a new FileWriter, which will write the data to a file.
        ReplayManager.setWriter(new NoLog());
        // Initialize the replay manager, which will handle the logging of the data.
        ReplayManager.init();
    }

    /**
     * Whether the tele-op is enabled or not.
     * @return Whether the tele-op is enabled or not.
     * */
    public boolean isTeleOpEnabled() {
        return m_teleOpEnabled;
    }

    public void start() {
        // -- ENABLE --
        m_teleOpEnabled = true;
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        // -- DISABLE --
        m_teleOpEnabled = false;
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.lib.replay.ReplayManager;
import org.firstinspires.ftc.lib.replay.log.writers.FileWriter;
import org.firstinspires.ftc.lib.replay.log.writers.NoLog;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.vision.VisionSubsystem;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Main TeleOp")
public class TeleOp extends OpMode {
    private static TeleOp instance;

    public static boolean hasInstance() {
        return instance != null;
    }

    public static HardwareMap getHardwareMap() {
        return instance.hardwareMap;
    }

    public static Telemetry getTelemetry() {
        return instance.telemetry;
    }

    private ElapsedTime m_runtime = new ElapsedTime();

    private boolean m_teleOpEnabled = false;

    private VisionSubsystem m_visionSubsystem = new VisionSubsystem();

    @Override
    public void init() {
        instance = this;

        /* Initialize the log writer and replay manager first. */

        //This will make all System.out.println() calls go to the log file as well as the console.
        ReplayManager.captureConsoleToLog();
        // Set the log writer to a new FileWriter, which will write the data to a file.
        ReplayManager.setWriter(new NoLog());
        // Initialize the replay manager, which will handle the logging of the data.
        ReplayManager.init();

        Subsystems.onInit();

        telemetry.addLine("TeleOp Enabled!");
        telemetry.update();
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
        Subsystems.periodic();
    }

    @Override
    public void stop() {
        // -- DISABLE --
        m_teleOpEnabled = false;

        telemetry.addLine("Autonomous Disabled!");
        telemetry.update();

        Subsystems.onDisable();
    }
}

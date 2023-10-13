package org.firstinspires.ftc.teamcode;

import android.content.Context;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.LEDSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.MotorTestSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.vision.OLD_VisionSubsystem;
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

    public static Context getAppContext() {
        return instance.hardwareMap.appContext;
    }

    private ElapsedTime m_runtime = new ElapsedTime();

    private boolean m_teleOpEnabled = false;

    private VisionSubsystem m_visionSubsystem;
    private MotorTestSubsystem m_motorTestSubsystem; //= new MotorTestSubsystem();
    private LEDSubsystem m_ledSubsystem = new LEDSubsystem();

    @Override
    public void init() {
        instance = this;

        m_visionSubsystem =  new VisionSubsystem();

        Robot.init();

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

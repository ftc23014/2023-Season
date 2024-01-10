package org.firstinspires.ftc.teamcode;

import android.content.Context;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.commands.teleop.AssistantControls;
import org.firstinspires.ftc.teamcode.commands.teleop.DriverControls;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.*;
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

    public static Gamepad gamepad(boolean gamepad1) {
        return gamepad1 ? instance.gamepad1 : instance.gamepad2;
    }

    private ElapsedTime m_runtime = new ElapsedTime();

    private boolean m_teleOpEnabled = false;

    // subsystems

    private VisionSubsystem m_visionSubsystem;
    private MecanumDriveSubsystem m_mecanumDriveSubsystem;
    private Intake m_intakeSubsystem;

    private Drone m_droneSubsystem;

    private DualLinearSlide m_linearSlideSubsystem;
    private Hang m_hangSubsystem;
    private Bucket m_bucketSubsystem;

    // commands

    private DriverControls m_driverControls;
    private AssistantControls m_assistantControls;

    //end of commands/subsystems
    @Override
    public void init() {
        instance = this;

        StartupManager.clear();

        //SETUP SUBSYSTEMS HERE

        m_mecanumDriveSubsystem = new MecanumDriveSubsystem();

        m_visionSubsystem =  new VisionSubsystem();

        m_intakeSubsystem = new Intake();

        //m_droneSubsystem = new Drone();

        m_linearSlideSubsystem = new DualLinearSlide();

        m_hangSubsystem = new Hang();

        m_bucketSubsystem = new Bucket();

        m_driverControls = new DriverControls(
                gamepad1,
                m_mecanumDriveSubsystem,
                m_intakeSubsystem
        );

        m_assistantControls = new AssistantControls(
                gamepad2,
                m_linearSlideSubsystem,
//                m_droneSubsystem,
                m_hangSubsystem,
                m_bucketSubsystem
        );

        m_mecanumDriveSubsystem.addDefaultCommand(m_driverControls);
        m_linearSlideSubsystem.addDefaultCommand(m_assistantControls);

        //END SUBSYSTEM CREATION

        StartupManager.printChecks(telemetry);

        Robot.init();

        Subsystems.onInit();

        telemetry.addLine("TeleOp Enabled!");
        telemetry.update();
    }

    @Override
    public void init_loop() {
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

        m_mecanumDriveSubsystem.stop_motors();

        telemetry.addLine("Autonomous Disabled!");
        telemetry.update();

        Subsystems.onDisable();
    }
}

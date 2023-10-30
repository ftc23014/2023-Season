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
import org.firstinspires.ftc.teamcode.subsystems.LEDSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveSubsystem;
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
    private MecanumDriveSubsystem m_mecanumDriveSubsystem;
    private MotorTestSubsystem m_motorTestSubsystem; //= new MotorTestSubsystem();

    @Override
    public void init() {
        instance = this;

        //SETUP SUBSYSTEMS HERE
        m_mecanumDriveSubsystem = new MecanumDriveSubsystem();

        m_visionSubsystem =  new VisionSubsystem();
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
        if (Math.abs(gamepad1.left_stick_x) > 0.05 || Math.abs(gamepad1.left_stick_y) > 0.05 || Math.abs(gamepad1.right_stick_x) > 0.05) {
            m_mecanumDriveSubsystem.drive(
                    new Translation2d(
                            gamepad1.left_stick_x,
                            gamepad1.left_stick_y
                    ).scalar(m_mecanumDriveSubsystem.getVelocityLimit().get(Unit.Type.Meters)),
                    Rotation2d.fromDegrees(-Math.pow(gamepad1.right_stick_x, 5) * 90),
                    true,
                    true
            );
//            m_mecanumDriveSubsystem.driveMotors(new Translation2d(
//                    -gamepad1.left_stick_x,
//                    gamepad1.left_stick_y
//            ), gamepad1.right_stick_x);
        } else {
            m_mecanumDriveSubsystem.stop_motors();
        }

        Subsystems.periodic();
    }

    @Override
    public void stop() {
        // -- DISABLE --
        m_teleOpEnabled = false;

        m_mecanumDriveSubsystem.stop().execute();

        telemetry.addLine("Autonomous Disabled!");
        telemetry.update();

        Subsystems.onDisable();
    }
}

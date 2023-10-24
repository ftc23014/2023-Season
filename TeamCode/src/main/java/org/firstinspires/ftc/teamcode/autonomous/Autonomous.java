package org.firstinspires.ftc.teamcode.autonomous;


import android.content.Context;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.auto.PlannedAuto;
import org.firstinspires.ftc.lib.math.PIDController;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;
import org.firstinspires.ftc.lib.systems.commands.ParallelCommand;
import org.firstinspires.ftc.lib.systems.commands.SequentialCommand;
import org.firstinspires.ftc.lib.systems.commands.WaitCommand;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveSubsystem;

import java.io.File;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="main_autonomous")
@Disabled
public class Autonomous extends OpMode {
    private static Autonomous instance;
    private static OpMode k_autoReferral;

    public static boolean hasInstance() {
        return instance != null;
    }

    public static HardwareMap getHardwareMap() {
        return k_autoReferral.hardwareMap;
    }

    public static Telemetry getTelemetry() {
        return k_autoReferral.telemetry;
    }

    public static Autonomous setAutonomous(AutonomousMode autoMode, OpMode referral) {
        if (instance != null) {
            throw new RuntimeException("Autonomous was already created but you're changing it?");
        }

        k_autoReferral = referral;

        instance = new Autonomous(autoMode);

        return instance;
    }

    public static Autonomous getInstance() {
        if (instance == null) {
            throw new RuntimeException("Autonomous wasn't created!");
        }

        return instance;
    }

    public enum AutonomousMode {
        PICKUP_AUTONOMOUS,
        BACKDROP_AUTONOMOUS;
    }

    public enum PathSelectionFlags {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE_A,
        FIVE_B,
        FIVE_C,
        SIX
    }

    private MecanumDriveSubsystem m_driveSubsystem;

    private AutonomousMode m_autonomousMode;
    private boolean m_autonomousEnabled;

    public Autonomous(AutonomousMode autoMode) {
        m_autonomousMode = autoMode;
    }

    private PlannedAuto auto;

    @Override
    public void init() {
        //BezierSegment[] one = BezierSegment.loadFromFile(new File(""));

        m_driveSubsystem = new MecanumDriveSubsystem();

        Robot.init();
        Subsystems.onInit();

        generateAuto();
    }

    public void generateAuto() {
        AutonomousConstants constants = new AutonomousConstants(
                new Unit(1, Unit.Type.Meters),
                new Unit(0.5, Unit.Type.Meters),
                12,
                new PIDController(0.2, 0.00, 0.00),
                1d/32d
        );

        auto = new PlannedAuto(
            constants,
            new InstantCommand(() -> {
                telemetry().addLine("Autonomous Loaded!");
            }),
            new ParallelCommand(
                new InstantCommand(() -> {
                    telemetry().addLine("Autonomous Sequential 1!");
                }),
                new InstantCommand(() -> {
                    telemetry().addLine("Autonomous Sequential 2!");
                })
            ),
            new InstantCommand(() -> {
                telemetry().update();
            }),
            new WaitCommand(2),
            m_driveSubsystem.driveCommand(new Translation2d(0, 0.5), 0),
            new WaitCommand(2),
            m_driveSubsystem.stop(),
            new Trajectory(
                    m_driveSubsystem,
                    BezierSegment.loadFromResources(R.raw.example)
            )
        );

        telemetry().addLine("Autonomous Generated!");
        telemetry().update();
    }

    @Override
    public void start() {
        //This is called when the autonomous is enabled.

        // -- ENABLE --
        m_autonomousEnabled = true;

        auto.start();
    }

    @Override
    public void loop() {
        //This is called every loop of the autonomous.
        Subsystems.periodic();

        auto.loop();
    }

    @Override
    public void init_loop() {
        telemetry.update();
    }

    @Override
    public void stop() {
        // -- DISABLE --
        m_autonomousEnabled = false;

        Subsystems.onDisable();
    }

    private Telemetry telemetry() {
        return k_autoReferral.telemetry;
    }
}

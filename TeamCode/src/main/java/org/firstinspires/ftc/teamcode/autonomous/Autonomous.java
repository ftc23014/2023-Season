package org.firstinspires.ftc.teamcode.autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.auto.PlannedAuto;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.systems.commands.*;
import org.firstinspires.ftc.teamcode.commands.HuskyDetectCommand;
import org.firstinspires.ftc.teamcode.commands.TurnToCommand;
import org.firstinspires.ftc.teamcode.subsystems.SensorConeHuskyLensSubsystem;
import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.DualLinearSlide;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Spatula;

import java.util.concurrent.atomic.AtomicInteger;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="main_autonomous")
@Disabled
public class Autonomous extends OpMode {
    private static Autonomous instance;
    private static OpMode k_autoReferral;

    private static Pose2d startingPosition;

    public static boolean hasInstance() {
        return instance != null;
    }

    public static HardwareMap getHardwareMap() {
        return k_autoReferral.hardwareMap;
    }

    public static Telemetry getTelemetry() {
        return k_autoReferral.telemetry;
    }

    public static Pose2d getStartingPosition() {
        return startingPosition == null ? Pose2d.zero() : startingPosition;
    }

    public static Autonomous setAutonomous(AutonomousMode autoMode, StartingSide side, OpMode referral) {
        if (instance != null) {
            System.out.println("[Autonomous] Odd... Autonomous was already created but you're changing it? This is fine, we hope.");
        }

        k_autoReferral = referral;

        instance = new Autonomous(autoMode, side);

        return instance;
    }

    public static Autonomous getInstance() {
        if (instance == null) {
            throw new RuntimeException("Autonomous wasn't created!");
        }

        return instance;
    }

    public enum AutonomousMode {
        TESTING,
        BASIC_AUTO,
        FULL_AUTO,
        GOOFY,
        RIGHT_TO_LEFT;
    }

    public enum StartingSide {
        RED,
        BLUE;
    }

    public enum PathSelectionFlags {
        IGNORE,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE_A,
        FIVE_B,
        FIVE_C,
        SIX
    }

    public enum HuskyLensDetection {
        LEFT,
        MIDDLE,
        RIGHT
    }

    public MecanumDriveSubsystem m_driveSubsystem;

    private SensorConeHuskyLensSubsystem m_sensorConeHuskyLensSubsystem;

    private AutonomousMode m_autonomousMode;
    private StartingSide m_side;

    private PathSelectionFlags m_pathSelectionFlag;

    private HuskyLensDetection m_huskyLensDetection;
   // private Intake m_intakeSubsystem;
    //private Spatula m_spatulaSubsystem;
    //private DualLinearSlide m_linearSlideSubsystem;

    private boolean m_autonomousEnabled;

    public Autonomous(AutonomousMode autoMode, StartingSide side) {
        m_autonomousMode = autoMode;
        m_side = side;
        m_pathSelectionFlag = PathSelectionFlags.IGNORE;
    }

    public Autonomous(AutonomousMode autoMode, StartingSide side, PathSelectionFlags pathSelectionFlag) {
        m_autonomousMode = autoMode;
        m_side = side;
        m_pathSelectionFlag = pathSelectionFlag;
    }

    private PlannedAuto auto;

    private AtomicInteger m_detectedConePosition = new AtomicInteger(0);

    @Override
    public void init() {
        //BezierSegment[] one = BezierSegment.loadFromFile(new File(""));

        m_driveSubsystem = new MecanumDriveSubsystem();
        m_sensorConeHuskyLensSubsystem = new SensorConeHuskyLensSubsystem();
       // m_intakeSubsystem = new Intake();
       // m_spatulaSubsystem = new Spatula();
        //m_linearSlideSubsystem = new DualLinearSlide();

        Robot.init();
        Subsystems.onInit();

        generateAuto();

        telemetry().update();
    }

    public void generateAuto() {
        AutonomousConstants constants = new AutonomousConstants(
                new Unit(1, Unit.Type.Meters),
                new Unit(0.1, Unit.Type.Meters),
                new Unit(0.5, Unit.Type.Meters),
                6,
                new PIDController(0.4, 0.00, 0.00),
                1d/32d
        );

        constants.setUsePhysicsCalculations(false);

        //NEGATIVE is left,
        //POSITIVE is right

//        auto = new PlannedAuto(
//                constants,
//                new InstantCommand
//        )
        if (m_autonomousMode == AutonomousMode.BASIC_AUTO) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded!");
                    }),
                    new InstantCommand(() -> {
                        telemetry().update();
                    }),
                    m_driveSubsystem.driveCommand(
                            new Translation2d(0.2, 0),
                            0
                    ),
                    new WaitCommand(0.5),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.5),
                    m_driveSubsystem.driveCommand(
                            m_side == StartingSide.BLUE ?
                                    new Translation2d(0.5, 0)
                                    : new Translation2d(-0.5, 0)
                            , 0),
                    new WaitCommand(2),
                    m_driveSubsystem.stop()
            );
        } else if (m_autonomousMode == AutonomousMode.TESTING) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded!");
                    }),
                    new InstantCommand(() -> {
                        telemetry().update();
                    }),
                    new WaitCommand(1),
                    new Trajectory(
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.testing)
                    ),
                    new InstantCommand(() -> {
                        telemetry().addLine("Finished!");
                    }),
                    m_driveSubsystem.stop()
            );
        } else if (m_autonomousMode == AutonomousMode.GOOFY) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                    }),
                    new WaitCommand(0.1),
                    new Trajectory(
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.goofy_right_left_test)
                    )
            );
        } else if (m_autonomousMode == AutonomousMode.RIGHT_TO_LEFT) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                    }),
                    new WaitCommand(0.1),
                    new Trajectory(
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_right_to_backdrop)
                    )
            );
        }

        else if (m_autonomousMode == AutonomousMode.FULL_AUTO) {
            //if (m_pathSelectionFlag == PathSelectionFlags.ONE) {
                //m_huskyLensDetection = HuskyLensDetection.MIDDLE;

                auto = new PlannedAuto(
                        constants,
                        new InstantCommand(() -> {
                            telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                        }),
                        new WaitCommand(0.1),
                        new HuskyDetectCommand(
                                m_sensorConeHuskyLensSubsystem,
                                1,
                                (int detected) -> {
                                    if (detected == -2) {
                                        telemetry().addLine("ERROR WITH HUSKY?");
                                    }

                                    m_huskyLensDetection = detected == -1 ? HuskyLensDetection.LEFT : detected == 0 ? HuskyLensDetection.MIDDLE : HuskyLensDetection.RIGHT;
                                    telemetry().addLine("Detected tape: " + m_huskyLensDetection.name());
                                    telemetry.update();
                                }
                        ),
                        //new StallStop(), //for debugging.
                        new WaitCommand(0.2),
                        new Trajectory(
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.one_middle)
                        ).runFlippedX(m_side == StartingSide.RED),
                        new WaitCommand(0.1),
                        m_driveSubsystem.stop(),
                        new WaitCommand(0.1),
                        new IfOrSkipCommand(() -> {
                                return m_huskyLensDetection == HuskyLensDetection.LEFT;
                            },
                            new TurnToCommand(
                                    Rotation2d.fromDegrees(90), m_driveSubsystem
                            )
                        ),
                        new IfOrSkipCommand(() -> {
                                return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                            },
                            new TurnToCommand(
                                    Rotation2d.fromDegrees(-90), m_driveSubsystem
                            )
                        ),
                        new WaitCommand(0.1),
                        m_driveSubsystem.driveCommand(
                                new Translation2d(0, 0.3),
                                Rotation2d.zero(),
                                false,
                                true
                        ),
                        new WaitCommand(0.2),
                        m_driveSubsystem.stop(),
                        new WaitCommand(0.1),
                        //m_intakeSubsystem.intake_cmd(0.2),
                        new WaitCommand(0.5),
                        //m_intakeSubsystem.stop_cmd(),
                        m_driveSubsystem.driveCommand(
                                new Translation2d(0, -0.3),
                                Rotation2d.zero(),
                                false,
                                true
                        ),
                        new WaitCommand(0.2),
                        m_driveSubsystem.stop(),
                        new StallStop(), //DON'T CONTINUE
                        new WaitCommand(0.1),
                        new IfOrSkipCommand(
                            () -> {
                                return m_huskyLensDetection != HuskyLensDetection.RIGHT;
                            },
                            new TurnToCommand(
                                Rotation2d.fromDegrees(-90), m_driveSubsystem
                            )
                        ),
                        new IfOrSkipCommand(
                            () -> {
                                return m_huskyLensDetection != HuskyLensDetection.LEFT;
                            },
                            new Trajectory(
                                    m_driveSubsystem,
                                    BezierSegment.loadFromResources(R.raw.to_backboard_from_place_straight)
                            )
                        ),
                        new IfOrSkipCommand(
                            () -> {
                                return m_huskyLensDetection == HuskyLensDetection.LEFT;
                            },
                            new Trajectory(
                                    m_driveSubsystem,
                                    BezierSegment.loadFromResources(R.raw.to_backboard_from_place_curved)
                            )
                        ),
                        m_driveSubsystem.stop(),
                        new WaitCommand(0.1),
                        new IfOrSkipCommand(
                                () -> {
                                    return m_huskyLensDetection == HuskyLensDetection.LEFT;
                                },
                                new Trajectory(
                                        m_driveSubsystem,
                                        BezierSegment.loadFromResources(R.raw.left_backboard_place)
                                )
                        ),
                        new IfOrSkipCommand(
                                () -> {
                                    return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                                },
                                new Trajectory(
                                        m_driveSubsystem,
                                        BezierSegment.loadFromResources(R.raw.right_backboard_place)
                                )
                        ),
                        new IfOrSkipCommand(
                                () -> {
                                    return m_huskyLensDetection == HuskyLensDetection.MIDDLE;
                                },
                                new Trajectory(
                                        m_driveSubsystem,
                                        BezierSegment.loadFromResources(R.raw.middle_backboard_place)
                                )
                        ),
                        m_driveSubsystem.stop(),
                        new StallStop(),
                        //m_linearSlideSubsystem.power(0.2),
                        new WaitCommand(1.2),
                        //m_linearSlideSubsystem.power(0),
                        new WaitCommand(0.1)
                      //  m_spatulaSubsystem.deploy()
                );
            //}
        }

        telemetry().addLine("Autonomous Generated!");
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

        m_driveSubsystem.stop_motors();
        auto.stop();

        Subsystems.onDisable();
    }

    private Telemetry telemetry() {
        return k_autoReferral.telemetry;
    }
}

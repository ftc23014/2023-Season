package org.firstinspires.ftc.teamcode.autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.auto.PlannedAuto;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.systems.commands.*;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.HuskyDetectCommand;
import org.firstinspires.ftc.teamcode.commands.TurnToCommand;
import org.firstinspires.ftc.teamcode.subsystems.SensorConeHuskyLensSubsystem;
import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

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
        BLUE_LEFT_AUTO,

        BLUE_RIGHT_AUTO;
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
        AutonomousConstants constants = Constants.Autonomous.autonomousConstants;

        constants.setUsePhysicsCalculations(Constants.Autonomous.usePhysicsCalculations);
        constants.setCentripetalForceMultiplier(Constants.Autonomous.centripetalForceMultiplier);

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
                            BezierSegment.loadFromResources(R.raw.all_the_way_thru)
                    ),
                    new InstantCommand(() -> {
                        telemetry().addLine("Finished!");
                    }),
                    m_driveSubsystem.stop()
            );
        } else if (m_autonomousMode == AutonomousMode.BLUE_LEFT_AUTO) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                    }),
                    new WaitCommand(0.1),
                    // start huskylens detection procedure
                    new HuskyDetectCommand( // returns detected tape into HuskyLensDetection
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
                    new WaitCommand(0.2),
                    new Trajectory( // go towards middle of all three tapes
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.one_middle)
                    ).runFlippedX(m_side == StartingSide.RED),
                    new WaitCommand(0.1),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.1),
                    new IfOrSkipCommand(() -> { // if left tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.LEFT;
                    },
                            new TurnToCommand(
                                    Rotation2d.fromDegrees(90), m_driveSubsystem
                            )
                    ),
                    new IfOrSkipCommand(() -> { // if right tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                    },
                            new TurnToCommand(
                                    Rotation2d.fromDegrees(-90), m_driveSubsystem
                            )
                    ), // otherwise just stay facing middle
                    new WaitCommand(0.1),
                    m_driveSubsystem.driveCommand( // drive a tiny bit towards the tape
                            new Translation2d(0, 0.3),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(0.2),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.1),
                    //m_intakeSubsystem.intake_cmd(0.2), // outtake
                    new WaitCommand(0.5),
                    //m_intakeSubsystem.stop_cmd(),
                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                            new Translation2d(0, -0.3),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(0.2),
                    m_driveSubsystem.stop(),

                    // end huskylens detection procedure
                    new Trajectory( // go towards backboard
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_left_step_one)
                    ),
                    new WaitCommand(0.1),
                    // TODO: add linear slide placing code
                    new Trajectory( // go towards stack of white pixels
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_left_step_two)
                    ),
                    new WaitCommand(0.1),
                    // TODO: add intake code
                    new Trajectory( // go back to backboard
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_left_step_three)
                    ),
                    new WaitCommand(0.1)
                    // TODO: figure out how many cycles we can do during auto


            );
        } else if (m_autonomousMode == AutonomousMode.BLUE_RIGHT_AUTO) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                    }),
                    new WaitCommand(0.1),
                    // start huskylens detection procedure
                    new HuskyDetectCommand( // returns detected tape into HuskyLensDetection
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
                    new WaitCommand(0.2),
                    new Trajectory( // go towards middle of all three tapes
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.one_middle)
                    ).runFlippedX(m_side == StartingSide.RED),
                    new WaitCommand(0.1),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.1),
                    new IfOrSkipCommand(() -> { // if left tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.LEFT;
                    },
                            new TurnToCommand(
                                    Rotation2d.fromDegrees(90), m_driveSubsystem
                            )
                    ),
                    new IfOrSkipCommand(() -> { // if right tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                    },
                            new TurnToCommand(
                                    Rotation2d.fromDegrees(-90), m_driveSubsystem
                            )
                    ), // otherwise just stay facing middle
                    new WaitCommand(0.1),
                    m_driveSubsystem.driveCommand( // drive a tiny bit towards the tape
                            new Translation2d(0, 0.3),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(0.2),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.1),
                    //m_intakeSubsystem.intake_cmd(0.2), // outtake
                    new WaitCommand(0.5),
                    //m_intakeSubsystem.stop_cmd(),
                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                            new Translation2d(0, -0.3),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(0.2),
                    m_driveSubsystem.stop(),

                    // end huskylens detection procedure
                    new Trajectory( // go towards white pixel stack
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_right_step_one)
                    ),
                    new WaitCommand(0.1),
//                    new TurnToCommand(
//                                    Rotation2d.fromDegrees(-90), m_driveSubsystem
//                            ),

                    // intake

                    new WaitCommand(0.5),
//                    new TurnToCommand(
//                            Rotation2d.fromDegrees(0), m_driveSubsystem
//                    ),
                    new Trajectory( // go towards backboard
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_right_step_two)
                    ),
                    // TODO: add linear slide placing code
                    new WaitCommand(0.1),
//                    new TurnToCommand(
//                            Rotation2d.fromDegrees(90), m_driveSubsystem
//                    ),
                    new WaitCommand(0.5),
//                    new TurnToCommand(
//                            Rotation2d.fromDegrees(0), m_driveSubsystem
//                    ),
                    new Trajectory( // go to white pixel stack
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.blue_right_step_three)
                    ),
                    new WaitCommand(0.1)
                    // TODO: figure out how many cycles we can do during auto
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
                        new HuskyDetectCommand( // returns detected tape into HuskyLensDetection
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
                        new WaitCommand(0.2),
                        new Trajectory( // go towards middle of all three tapes
                            m_driveSubsystem,
                            BezierSegment.loadFromResources(R.raw.one_middle)
                        ).runFlippedX(m_side == StartingSide.RED),
                        new WaitCommand(0.1),
                        m_driveSubsystem.stop(),
                        new WaitCommand(0.1),
                        new IfOrSkipCommand(() -> { // if left tape is detected, turn towards
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

package org.firstinspires.ftc.teamcode.autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.auto.PlannedAuto;
import org.firstinspires.ftc.lib.field.Field;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.systems.commands.*;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.AprilTagAutoMove;
import org.firstinspires.ftc.teamcode.commands.DriveToEncoderPosition;
import org.firstinspires.ftc.teamcode.commands.HuskyDetectCommand;
import org.firstinspires.ftc.teamcode.commands.TurnToCommand;
import org.firstinspires.ftc.teamcode.subsystems.SensorConeHuskyLensSubsystem;
import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.AutoPixelPlacer;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Bucket;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.DualLinearSlide;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.vision.VisionSubsystem;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

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

    public Autonomous setStartingPosition(Pose2d startingPosition) {
        this.startingPosition = startingPosition;
        return this;
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
        RED_RIGHT_AUTO,
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
    private DualLinearSlide m_linearSlideSubsystem;
    private VisionSubsystem m_visionSubsystem;
    private AutoPixelPlacer m_autoPixelPlacerSubsystem;
    private Bucket m_bucketSubsystem;

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
        m_linearSlideSubsystem = new DualLinearSlide();
        m_visionSubsystem = new VisionSubsystem();
        m_autoPixelPlacerSubsystem = new AutoPixelPlacer();
        m_bucketSubsystem = new Bucket();

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
                    new HuskyDetectCommand( // returns detected tape into HuskyLensDetection
                            m_sensorConeHuskyLensSubsystem,
                            1,
                            (int detected) -> {
                                if (detected == -2) {
                                    telemetry().addLine("ERROR WITH HUSKY?");
                                }


                                //Manual Detection - TODO: Disable
                                m_huskyLensDetection = detected == -1 ? HuskyLensDetection.LEFT : detected == 0 ? HuskyLensDetection.MIDDLE : HuskyLensDetection.RIGHT;
                                telemetry().addLine("Detected tape: " + m_huskyLensDetection.name());
                                telemetry.update();
                            }
                    ),
//                    new InstantCommand(() -> {
//                        m_driveSubsystem.resetPosition(new Pose2d(
//                                Unit.convert(11.5, Unit.Type.Inches, Unit.Type.Meters),
//                                Unit.convert(57.75, Unit.Type.Inches, Unit.Type.Meters),
//                                Rotation2d.zero()
//                        ));
//                    }),
//                    new DriveToEncoderPosition(
//                            new Translation2d(
//                                    Unit.convert(46.5, Unit.Type.Inches, Unit.Type.Meters),
//                                    Unit.convert(46.25, Unit.Type.Inches, Unit.Type.Meters)
//                            ),
//                            new Unit(3, Unit.Type.Centimeters)
//                    ),
//                    m_driveSubsystem.stop(),
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded!");
                    })
            );
//            auto = new PlannedAuto(
//                    constants,
//                    new InstantCommand(() -> {
//                        m_driveSubsystem.resetPosition(new Pose2d(
//                                Unit.convert(34.5, Unit.Type.Inches, Unit.Type.Meters),
//                                Unit.convert(34.75, Unit.Type.Inches, Unit.Type.Meters),
//                                Rotation2d.zero()
//                        ));
//                    }),
//                    new WaitCommand(0.1),
//                    new TurnToCommand(Rotation2d.fromDegrees(-90), m_driveSubsystem),
//                    m_driveSubsystem.stop(),
//                    new WaitCommand(1),
//                    new AprilTagAutoMove(
//                            m_visionSubsystem,
//                            AprilTagAutoMove.Side.Blue,
//                            AprilTagAutoMove.Position.Right
//                    ),
//                    m_driveSubsystem.stop()
//            );
        } else if (m_autonomousMode == AutonomousMode.BLUE_LEFT_AUTO) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                        m_driveSubsystem.resetPosition(
                                new Pose2d(
                                    Unit.convert(9.75, Unit.Type.Inches, Unit.Type.Meters),
                                    Unit.convert(57.75, Unit.Type.Inches, Unit.Type.Meters),
                                    Rotation2d.zero()
                                )
                        );
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


                                //Manual Detection - TODO: Disable
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
                    new WaitCommand(0.15),
                    new IfOrSkipCommand(() -> { // if left tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.LEFT;
                    }, new SequentialCommand(
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Unit.convert(45.5, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(52.25, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            m_driveSubsystem.stop(),
                            new WaitCommand(0.1),
                            m_autoPixelPlacerSubsystem.setDeploy(),
                            new WaitCommand(0.1),
                            m_autoPixelPlacerSubsystem.setRetract(),
                            new WaitCommand(0.5),
                            m_driveSubsystem.driveCommand(
                                    new Translation2d(0.5, 0),
                                    Rotation2d.zero(),
                                    false,
                                    true
                            ),
                            new WaitCommand(0.5),
                            m_driveSubsystem.stop(),
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Unit.convert(13.5, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(57.25, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            m_driveSubsystem.stop(),

                            new WaitCommand(0.1),
                            m_driveSubsystem.driveCommand(
                                    new Translation2d(-0.75, 0),
                                    Rotation2d.zero(),
                                    false,
                                    true
                            ),
                            new WaitCommand(1.3),
                            m_driveSubsystem.stop(),
//
//                            new WaitCommand(0.1),
//                            new DriveToEncoderPosition(
//                                    new Translation2d(
//                                            Unit.convert(13.5, Unit.Type.Inches, Unit.Type.Meters),
//                                            Unit.convert(25.75, Unit.Type.Inches, Unit.Type.Meters)
//                                    ),
//                                    new Unit(3, Unit.Type.Centimeters)
//                            ),
//                            new WaitCommand(0.1),
//                            m_driveSubsystem.stop(),

                            new WaitCommand(0.1),
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Unit.convert(32.25, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(31.75, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            new WaitCommand(0.1),
                            m_driveSubsystem.stop()
                    )),
                    new IfOrSkipCommand(() -> { // if right tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                    },
                            new SequentialCommand(
                                    new TurnToCommand(
                                            Rotation2d.fromDegrees(180), m_driveSubsystem
                                    ),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(0, -0.4),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.4),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(-0.35, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.35),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setDeploy(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setRetract(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(1.4, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(1),
                                    m_driveSubsystem.stop()

                            )
                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.MIDDLE;
                    },
                            new SequentialCommand(
                                    new TurnToCommand(
                                            Rotation2d.fromDegrees(180), m_driveSubsystem
                                    ),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(0.625, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.625),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(0, 0.1),
                                            Rotation2d.zero(),
                                            false,
                                            true),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setDeploy(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setRetract(),
                                    new WaitCommand(0.25),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0.75,
                                                    0
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.75),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0,
                                                    -0.75
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.75),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0.4,
                                                    0
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.4),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0,
                                                    0.3
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.3),
                                    m_driveSubsystem.stop()


                            )
                    ),
                    //drive to APRIL TAG position from here!
//                    new DriveToEncoderPosition(
//                            new Translation2d(
//                                    Unit.convert(34.5, Unit.Type.Inches, Unit.Type.Meters),
//                                    Unit.convert(34.75, Unit.Type.Inches, Unit.Type.Meters)
//                            ),
//                            new Unit(3, Unit.Type.Centimeters)
//                    ),
                    new TurnToCommand(Rotation2d.fromDegrees(-90), m_driveSubsystem),
                    m_driveSubsystem.stop(),
                    new WaitCommand(1),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                    },
                            new SequentialCommand(
                                    new AprilTagAutoMove(
                                            m_visionSubsystem,
                                            AprilTagAutoMove.Side.Blue,
                                            AprilTagAutoMove.Position.Right
                                    ),
                                    new WaitCommand(2),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0.65,
                                                    0
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.17),
                                    m_driveSubsystem.stop()

                            )



                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.MIDDLE;
                    },
                            new AprilTagAutoMove(
                                    m_visionSubsystem,
                                    AprilTagAutoMove.Side.Blue,
                                    AprilTagAutoMove.Position.Center
                            )
                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.LEFT;
                    },
                            new AprilTagAutoMove(
                                    m_visionSubsystem,
                                    AprilTagAutoMove.Side.Blue,
                                    AprilTagAutoMove.Position.Left
                            )
                    ),
                    m_driveSubsystem.stop(),
                    new TurnToCommand(Rotation2d.fromDegrees(-90), m_driveSubsystem),
                    m_driveSubsystem.stop(),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.MIDDLE),
                    m_bucketSubsystem.extendForAuto(),
                    m_bucketSubsystem.setDeploy(),
                    new WaitCommand(2),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.RETRACTED),
                    new WaitCommand(1),
                    m_driveSubsystem.driveCommand(
                            new Translation2d(0, 0.4),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(0.3),
                    m_bucketSubsystem.setDeployPusher(),
                    new WaitCommand(0.15),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.VERYLOW),
                    new IfOrSkipCommand(
                            () -> {
                                //If position is middle or right
                                return m_huskyLensDetection == HuskyLensDetection.MIDDLE || m_huskyLensDetection == HuskyLensDetection.RIGHT;
                            },
                            new WaitCommand(1.2)
                    ),
                    new IfOrSkipCommand(
                            () -> {
                                //If position is left
                                return m_huskyLensDetection == HuskyLensDetection.LEFT;
                            },
                            new WaitCommand(1.5)
                    ),
                    m_driveSubsystem.stop(),
                    new WaitCommand(1),
                    m_driveSubsystem.driveCommand(
                            new Translation2d(0, -0.2),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(1),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.1),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.MIDDLE),
                    m_bucketSubsystem.setRetract(),
                    m_bucketSubsystem.setRetractPusher(),
                    new WaitCommand(0.9),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.RETRACTED),
                    new TurnToCommand(Rotation2d.fromDegrees(0), m_driveSubsystem)
            );
        } else if (m_autonomousMode == AutonomousMode.RED_RIGHT_AUTO) {
            Constants.currentSide = Constants.Side.RIGHT_RED;

            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                        m_driveSubsystem.resetPosition(
                                new Pose2d(
                                        Unit.convert(129.25, Unit.Type.Inches, Unit.Type.Meters),
                                        Unit.convert(57.75, Unit.Type.Inches, Unit.Type.Meters),
                                        Rotation2d.zero()
                                )
                        );
                    }),
                    new WaitCommand(0.1),
                    // start huskylens detection procedure
//                    new HuskyDetectCommand( // returns detected tape into HuskyLensDetection
//                            m_sensorConeHuskyLensSubsystem,
//                            1,
//                            (int detected) -> {
//                                if (detected == -2) {
//                                    telemetry().addLine("ERROR WITH HUSKY?");
//                                }
//
//
//                                //Manual Detection - TODO: Disable
//                                m_huskyLensDetection = detected == -1 ? HuskyLensDetection.LEFT : detected == 0 ? HuskyLensDetection.MIDDLE : HuskyLensDetection.RIGHT;
//                                telemetry().addLine("Detected tape: " + m_huskyLensDetection.name());
//                                telemetry.update();
//                            }
//                    ),
                    new InstantCommand(() -> {
                        m_huskyLensDetection = HuskyLensDetection.LEFT;
                    }),
                    new WaitCommand(0.2),
//                    new Trajectory( // go towards middle of all three tapes
//                            m_driveSubsystem,
//                            BezierSegment.loadFromResources(R.raw.one_middle)
//                    ).runFlippedX(true),
//                    new DriveToEncoderPosition(
//                            new Translation2d(
//                                    104.25,
//                                    58
//                            ),
//                            new Unit(3, Unit.Type.Centimeters)
//                    ),
                    m_driveSubsystem.driveCommand(
                            new Translation2d(
                                    0,
                                    -0.5
                            ),
                            Rotation2d.zero(),
                            true,
                            true
                    ),
                    new WaitCommand(0.1),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.15),
                    new IfOrSkipCommand(() -> { // if left tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.LEFT && false;
                    }, new SequentialCommand(
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Field.field.getWidth().get(Unit.Type.Meters) - Unit.convert(45.5, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(52.25, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            m_driveSubsystem.stop(),
                            new WaitCommand(0.1),
                            m_autoPixelPlacerSubsystem.setDeploy(),
                            new WaitCommand(0.1),
                            m_autoPixelPlacerSubsystem.setRetract(),
                            new WaitCommand(0.5),
                            m_driveSubsystem.driveCommand(
                                    new Translation2d(-0.5, 0),
                                    Rotation2d.zero(),
                                    false,
                                    true
                            ),
                            new WaitCommand(0.5),
                            m_driveSubsystem.stop(),
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Field.field.getWidth().get(Unit.Type.Meters) - Unit.convert(13.5, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(57.25, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            m_driveSubsystem.stop(),

                            new WaitCommand(0.1),
                            m_driveSubsystem.driveCommand(
                                    new Translation2d(0.75, 0),
                                    Rotation2d.zero(),
                                    false,
                                    true
                            ),
                            new WaitCommand(1.3),
                            m_driveSubsystem.stop(),
                            new WaitCommand(0.1),
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Field.field.getWidth().get(Unit.Type.Meters) - Unit.convert(32.25, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(31.75, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            new WaitCommand(0.1),
                            m_driveSubsystem.stop()
                    )),
                    new IfOrSkipCommand(() -> { // if right tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT && false;
                    },
                            new SequentialCommand(
                                    new TurnToCommand(
                                            Rotation2d.fromDegrees(180), m_driveSubsystem
                                    ),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(0, -0.4),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.4),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(-0.35, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.35),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setDeploy(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setRetract(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(-1.4, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(1),
                                    m_driveSubsystem.stop()

                            )
                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.MIDDLE && false;
                    },
                            new SequentialCommand(
                                    new TurnToCommand(
                                            Rotation2d.fromDegrees(180), m_driveSubsystem
                                    ),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(-0.625, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.625),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(0, 0.1),
                                            Rotation2d.zero(),
                                            false,
                                            true),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setDeploy(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setRetract(),
                                    new WaitCommand(0.25),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    -0.75,
                                                    0
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.75),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0,
                                                    -0.75
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.75),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    -0.4,
                                                    0
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.4),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    0,
                                                    0.3
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.3),
                                    m_driveSubsystem.stop()


                            )
                    ),
                    //drive to APRIL TAG position from here!
                    new TurnToCommand(Rotation2d.fromDegrees(90), m_driveSubsystem),
                    m_driveSubsystem.stop(),
                    new WaitCommand(1),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                    },
                            new SequentialCommand(
                                    new AprilTagAutoMove(
                                            m_visionSubsystem,
                                            AprilTagAutoMove.Side.Red,
                                            AprilTagAutoMove.Position.Right
                                    ),
                                    new WaitCommand(2),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(
                                                    -0.65,
                                                    0
                                            ),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.17),
                                    m_driveSubsystem.stop()

                            )
                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.MIDDLE;
                    },
                            new AprilTagAutoMove(
                                    m_visionSubsystem,
                                    AprilTagAutoMove.Side.Red,
                                    AprilTagAutoMove.Position.Center
                            )
                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.LEFT;
                    },
                            new AprilTagAutoMove(
                                    m_visionSubsystem,
                                    AprilTagAutoMove.Side.Red,
                                    AprilTagAutoMove.Position.Left
                            )
                    ),
                    m_driveSubsystem.stop(),
                    new TurnToCommand(Rotation2d.fromDegrees(90), m_driveSubsystem),
                    m_driveSubsystem.stop(),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.MIDDLE),
                    //m_bucketSubsystem.extendForAuto(),
                    m_bucketSubsystem.setDeploy(),
                    new WaitCommand(2),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.VERYLOW),
                    new WaitCommand(1),
                    m_driveSubsystem.driveCommand(
                            new Translation2d(0, 0.4),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(0.3),
                    m_bucketSubsystem.setDeployPusher(),
                    new WaitCommand(0.15),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.VERYLOW),
                    new IfOrSkipCommand(
                            () -> {
                                //If position is middle or right
                                return m_huskyLensDetection == HuskyLensDetection.MIDDLE || m_huskyLensDetection == HuskyLensDetection.RIGHT;
                            },
                            new WaitCommand(1.2)
                    ),
                    new IfOrSkipCommand(
                            () -> {
                                //If position is left
                                return m_huskyLensDetection == HuskyLensDetection.LEFT;
                            },
                            new WaitCommand(1.5)
                    ),
                    m_driveSubsystem.stop(),
                    new WaitCommand(1),
                    m_driveSubsystem.driveCommand(
                            new Translation2d(0, -0.2),
                            Rotation2d.zero(),
                            false,
                            true
                    ),
                    new WaitCommand(1),
                    m_driveSubsystem.stop(),
                    new WaitCommand(0.1),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.MIDDLE),
                    m_bucketSubsystem.setRetract(),
                    m_bucketSubsystem.setRetractPusher(),
                    new WaitCommand(0.9),
                    m_linearSlideSubsystem.position(DualLinearSlide.SlidePosition.RETRACTED),
                    new TurnToCommand(Rotation2d.fromDegrees(0), m_driveSubsystem)
            );
        } else if (m_autonomousMode == AutonomousMode.BLUE_RIGHT_AUTO) {
            auto = new PlannedAuto(
                    constants,
                    new InstantCommand(() -> {
                        telemetry().addLine("Autonomous Loaded - Running " + m_pathSelectionFlag.name() + "!");
                        m_driveSubsystem.resetPosition(
                                new Pose2d(
                                        Unit.convert(9.75, Unit.Type.Inches, Unit.Type.Meters),
                                        Unit.convert(57.75, Unit.Type.Inches, Unit.Type.Meters),
                                        Rotation2d.zero()
                                )
                        );
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


                                //Manual Detection - TODO: Disable
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
                    new WaitCommand(0.15),
                    new IfOrSkipCommand(() -> { // if left tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.LEFT;
                    }, new SequentialCommand(
                            new DriveToEncoderPosition(
                                    new Translation2d(
                                            Unit.convert(45.5, Unit.Type.Inches, Unit.Type.Meters),
                                            Unit.convert(52.25, Unit.Type.Inches, Unit.Type.Meters)
                                    ),
                                    new Unit(3, Unit.Type.Centimeters)
                            ),
                            m_driveSubsystem.stop(),
                            new WaitCommand(0.1),
                            m_autoPixelPlacerSubsystem.setDeploy(),
                            new WaitCommand(0.1),
                            m_autoPixelPlacerSubsystem.setRetract(),
                            new WaitCommand(0.5),
                            m_driveSubsystem.stop()
                    )),
                    new IfOrSkipCommand(() -> { // if right tape is detected, turn towards it
                        return m_huskyLensDetection == HuskyLensDetection.RIGHT;
                    },
                            new SequentialCommand(
                                    new TurnToCommand(
                                            Rotation2d.fromDegrees(180), m_driveSubsystem
                                    ),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(0, -0.4),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.4),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(-0.35, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.35),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setDeploy(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setRetract(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.stop()

                            )
                    ),
                    new IfOrSkipCommand(() -> {
                        return m_huskyLensDetection == HuskyLensDetection.MIDDLE;
                    },
                            new SequentialCommand(
                                    new TurnToCommand(
                                            Rotation2d.fromDegrees(180), m_driveSubsystem
                                    ),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand( // drive the bit back from the tape (to original ops)
                                            new Translation2d(0.625, 0),
                                            Rotation2d.zero(),
                                            false,
                                            true
                                    ),
                                    new WaitCommand(0.625),
                                    m_driveSubsystem.stop(),
                                    new WaitCommand(0.1),
                                    m_driveSubsystem.driveCommand(
                                            new Translation2d(0, 0.1),
                                            Rotation2d.zero(),
                                            false,
                                            true),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setDeploy(),
                                    new WaitCommand(0.1),
                                    m_autoPixelPlacerSubsystem.setRetract(),
                                    new WaitCommand(0.25),
                                    m_driveSubsystem.stop()

                            )
                    )
                    //drive to APRIL TAG position from here!
//                    new DriveToEncoderPosition(
//                            new Translation2d(
//                                    Unit.convert(34.5, Unit.Type.Inches, Unit.Type.Meters),
//                                    Unit.convert(34.75, Unit.Type.Inches, Unit.Type.Meters)
//                            ),
//                            new Unit(3, Unit.Type.Centimeters)
//                    ),
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

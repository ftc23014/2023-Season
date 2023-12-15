package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

public class AutoPlaceCommand extends Command {
    public enum PlacePosition {
        LEFT(new Translation2d(
                0, 0
        )),
        CENTER(new Translation2d(
                0, 0
        )),
        RIGHT(new Translation2d(
                0, 0
        ));

        private Translation2d position;

        PlacePosition(Translation2d position) {
            this.position = position;
        }
    }

    private PlacePosition position;

    private MecanumDriveSubsystem driveSubsystem;

    private Trajectory trajectory;

    public AutoPlaceCommand(MecanumDriveSubsystem driveSubsystem, PlacePosition position) {
        this.position = position;
    }

    @Override
    public void init() {
        trajectory = new Trajectory(
                driveSubsystem,
                new BezierSegment(
                        new Waypoint(
                                driveSubsystem.getPosition().getPosition(),
                                Rotation2d.zero(),
                                Waypoint.Type.HARD
                        ),
                        new Waypoint(
                                new Translation2d(
                                        driveSubsystem.getPosition().getX(),
                                        position.position.getY()
                                ),
                                Rotation2d.zero(),
                                Waypoint.Type.HARD
                        ),
                        new Waypoint(
                                new Translation2d(
                                        position.position.getX(),
                                        driveSubsystem.getPosition().getY()
                                ),
                                Rotation2d.zero(),
                                Waypoint.Type.HARD
                        ),
                        new Waypoint(
                                position.position,
                                Rotation2d.zero(),
                                Waypoint.Type.HARD
                        )
                )
        );

        AutonomousConstants constants = Constants.Autonomous.autonomousConstants;

        constants.setUsePhysicsCalculations(Constants.Autonomous.usePhysicsCalculations);

        trajectory.setConstants(
            constants
        );

        trajectory.generate();
    }


    public boolean trajectoryGenerated() {
        return trajectory.finishedGenerating();
    }

    @Override
    public void execute() {
        if (!trajectoryGenerated()) {
            return;
        }

        trajectory.execute();
    }

    @Override
    public void cancel() {
        trajectory.cancel();
    }

    @Override
    public boolean hasFinished() {
        return trajectory.hasFinished();
    }
}

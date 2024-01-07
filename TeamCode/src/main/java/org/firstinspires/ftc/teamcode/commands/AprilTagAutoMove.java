package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.auto.AutonomousConstants;
import org.firstinspires.ftc.lib.math.*;
import org.firstinspires.ftc.lib.pathing.Trajectory;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.vision.VisionSubsystem;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;

public class AprilTagAutoMove extends Command {
    public static enum Side {
        Blue, Red;
    }

    public static enum Position {
        Left, Center, Right;
    }


    private final VisionSubsystem v_subsystem;

    private Trajectory trajectory;
    private TurnToCommand turnToCommand;

    private Side side;
    private Position position;

    public AprilTagAutoMove(VisionSubsystem v_subsystem, Side side, Position position) {
        super();

        this.v_subsystem = v_subsystem;

        this.side = side;
        this.position = position;
    }

    private final Unit maxDistance = new Unit(1.5, Unit.Type.Meters);

    @Override
    public void init() {
        Translation2d pose = v_subsystem.getCurrentPose().getPosition();

        if (pose.isZero()) {
            System.out.println("Could not find the robot's pose!");
            cancel();
            return;
        }

        boolean inDistance = false;

        for (int i = 1; i <= 8; i++) {
            AprilTagMetadata tag = AprilTagGameDatabase.getCenterStageTagLibrary().lookupTag(i);
            Translation2d rawTagPos = VisionSubsystem.convertTagFieldPosition(tag.fieldPosition);
            Translation2d tagPos = new Translation2d(
                    Unit.convert(rawTagPos.getX(), Unit.Type.Inches, Unit.Type.Meters),
                    Unit.convert(rawTagPos.getY(), Unit.Type.Inches, Unit.Type.Meters)
            );

            if (tagPos.distance(pose) < maxDistance.get(Unit.Type.Meters)) {
                inDistance = true;
                break;
            }
        }

        if (!inDistance) {
            System.out.println("Robot is not in range of any tags!");
            cancel();
            return;
        }

        AprilTagLibrary tagLibrary = AprilTagGameDatabase.getCenterStageTagLibrary();
        int tag = -1;

        switch (position) {
            case Left:
                tag = 1;
                break;
            case Center:
                tag = 2;
                break;
            case Right:
                tag = 3;
                break;
        }

        if (side.equals(Side.Red)) {
            tag += 3;
        }

        Translation2d tagPos = VisionSubsystem.convertTagFieldPosition(tagLibrary.lookupTag(tag).fieldPosition);

        tagPos = tagPos.convertTo(Unit.Type.Inches, Unit.Type.Meters).translateBy(
                0,
                .25d
        );

        BezierSegment path = VisionSubsystem.pathToTarget(
            tagPos,
            VisionSubsystem.PathMakingStrategy.HORIZ_CURVE
        );

        //System.out.println("generating path going from " + tagPos + " to " + path.getPathObject().getWaypoints()[3]);

        turnToCommand = new TurnToCommand(
            Rotation2d.fromDegrees(-90),
            MecanumDriveSubsystem.instance()
        );

        turnToCommand.init();

        trajectory = new Trajectory(MecanumDriveSubsystem.instance(), path);

        AutonomousConstants cs = Constants.Autonomous.autonomousConstants;

        cs.setPID(new WPIPIDController(
                cs.getPID().getPID()[0],
                cs.getPID().getPID()[1],
                cs.getPID().getPID()[2]
        ));

        cs.setUsePhysicsCalculations(Constants.Autonomous.usePhysicsCalculations);

        trajectory.setConstants(cs);

        trajectory.generate();
    }

    boolean stillGenerating = true;

    @Override
    public void execute() {
        if (isCancelled()) return;

        if (!turnToCommand.hasFinished()) {
            turnToCommand.execute();
            return;
        }

        if (!trajectory.finishedGenerating() && stillGenerating) {
            return;
        }

        if (stillGenerating) {
            stillGenerating = false;
            trajectory.init();
        }

        telemetry().addLine("moving towards " + side + " " + position);

        trajectory.execute();
    }

    @Override
    public boolean hasFinished() {
        return trajectory.hasFinished() || isCancelled();
    }
}

package org.firstinspires.ftc.teamcode.subsystems.vision;

import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.TeleOp;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

public class VisionSubsystem extends Subsystem {

    public static enum PathMakingStrategy {
        BETWEEN,
        VERT_CURVE,
        HORIZ_CURVE;
    }

    private AprilTagProcessor aprilTag;

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    private BackboardDetectionPipeline backboardDetectionPipeline;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal myVisionPortal;

    private Translation2d currentPose;
    private Rotation2d currentRotation;

    @Override
    public void init() {
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setLensIntrinsics(3298.7389543652603, 3265.0187042219723, 1165.7536942923, 826.4908289614423)
                .build();

        //backboardDetectionPipeline = new BackboardDetectionPipeline(BackboardDetectionPipeline.Strategy.Tower_Of_Babel);
        
        myVisionPortal = new VisionPortal.Builder()
                .setCamera(getHardwareMap().get(WebcamName.class, "Webcam 1"))
                .addProcessors(aprilTag)
                .build();
    }

    @Override
    public void periodic() {
        updatePose();
    }

    @Override
    public void onDisable() {
        //release the camera for other uses
        myVisionPortal.getActiveCamera().close();
    }

    /**
     * Get the current pose of the robot.
     * @return The current pose of the robot.
     */
    public Translation2d getCurrentPosition() {
        return currentPose;
    }

    /**
     * Get the current rotation of the robot.
     * @return The current rotation of the robot.
     */
    public Rotation2d getCurrentRotation() {
        return currentRotation;
    }

    boolean verbose = false;

    public BezierSegment pathToTarget(Translation2d target, PathMakingStrategy strat) {
        Translation2d currentPos = getCurrentPosition();

        Translation2d controlpoint1;
        Translation2d controlpoint2;

        if (strat == PathMakingStrategy.VERT_CURVE) {
            controlpoint1 = new Translation2d(
                    currentPos.getX(),
                    target.getY()
            );

            controlpoint2 = new Translation2d(
                    target.getX(),
                    currentPos.getY()
            );
        } else if (strat == PathMakingStrategy.HORIZ_CURVE) {
            controlpoint1 = new Translation2d(
                    target.getX(),
                    currentPos.getY()
            );

            controlpoint2 = new Translation2d(
                    currentPos.getX(),
                    target.getY()
            );
        } else {
            controlpoint1 = new Translation2d(
                    (currentPos.getX() + target.getX()) / 2,
                    (currentPos.getY() + target.getY()) / 2
            );
            controlpoint2 = controlpoint1.copy();
        }

        BezierSegment path = new BezierSegment(
                new Waypoint(
                        currentPos,
                        Rotation2d.zero(),
                        Waypoint.Type.HARD
                ),
                new Waypoint(
                        controlpoint1,
                        Rotation2d.zero(),
                        Waypoint.Type.HARD
                ),
                new Waypoint(
                        controlpoint2,
                        Rotation2d.zero(),
                        Waypoint.Type.HARD
                ),
                new Waypoint(
                        target,
                        Rotation2d.zero(),
                        Waypoint.Type.HARD
                )
        );

        if (verbose) {
            System.out.println("Generated path: " + path.toString());
        }

        return path;
    }

    private void updatePose() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();

        if (!currentDetections.isEmpty()) {
            currentPose = new Translation2d(
                    currentDetections.get(0).ftcPose.x,
                    currentDetections.get(0).ftcPose.y
            );

            currentRotation = Rotation2d.fromDegrees(
                    currentDetections.get(0).ftcPose.yaw
            );
        }
    }

    public Pose2d getCurrentPose() {
        return new Pose2d(currentPose, currentRotation);
    }
}

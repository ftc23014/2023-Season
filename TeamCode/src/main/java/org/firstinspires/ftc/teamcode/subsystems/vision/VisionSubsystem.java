package org.firstinspires.ftc.teamcode.subsystems.vision;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.iki.elonen.NanoHTTPD;
import org.firstinspires.ftc.lib.field.Field;
import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.pathing.Waypoint;
import org.firstinspires.ftc.lib.pathing.segments.BezierSegment;
import org.firstinspires.ftc.lib.server.api.ApiHandler;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.TeleOp;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.*;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;
import org.openftc.apriltag.AprilTagPose;

import java.util.ArrayList;
import java.util.List;

public class VisionSubsystem extends Subsystem {

    private static VisionSubsystem instance;

    public static VisionSubsystem getInstance() {
        return instance;
    }

    public static class VisionPose extends ApiHandler {
        @Override
        public String getRoute() {
            return "/api/vision_pose";
        }

        @Override
        public boolean exactMatch() {
            return false;
        }

        @Override
        public NanoHTTPD.Response getResponse(NanoHTTPD.IHTTPSession session) {
            Gson gson = new Gson();
            JsonObject obj = new JsonObject();

            obj.addProperty("x", instance.getCurrentPosition().getX());
            obj.addProperty("y", instance.getCurrentPosition().getY());
            obj.addProperty("yaw", instance.getCurrentRotation().getDegrees());

            JsonArray detections = new JsonArray();

            for (AprilTagDetection detection : instance.currentDetections) {
                JsonObject detectionObj = new JsonObject();
                detectionObj.addProperty("id", detection.id);
                detectionObj.addProperty(
                        "x",
                        Unit.convert(detection.ftcPose.x, Unit.Type.Inches, Unit.Type.Meters)
                );
                detectionObj.addProperty(
                        "y",
                        Unit.convert(detection.ftcPose.y, Unit.Type.Inches, Unit.Type.Meters)
                );
                detectionObj.addProperty(
                        "real_x",
                        Unit.convert(
                                -AprilTagGameDatabase.getCenterStageTagLibrary().lookupTag(detection.id).fieldPosition.getData()[1] + (0.5 * Field.field.getWidth().get(Unit.Type.Inches)),
                                Unit.Type.Inches, Unit.Type.Meters
                        )
                );
                detectionObj.addProperty(
                        "real_y",
                        Unit.convert(
                                -AprilTagGameDatabase.getCenterStageTagLibrary().lookupTag(detection.id).fieldPosition.getData()[0] + (0.5 * Field.field.getHeight().get(Unit.Type.Inches)),
                                Unit.Type.Inches, Unit.Type.Meters
                        )
                );
                detectionObj.addProperty("yaw", detection.ftcPose.yaw);
                detections.add(detectionObj);
            }

            obj.add("detections", detections);

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", gson.toJson(obj));
        }
    }

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

    private List<AprilTagDetection> currentDetections = new ArrayList<>();

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

        instance = this;
    }

    @Override
    public void periodic() {
        updatePose();
    }

    @Override
    public void onDisable() {
        try {
            //release the camera for other uses
            myVisionPortal.getActiveCamera().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static BezierSegment pathToTarget(Translation2d target, PathMakingStrategy strat) {
        Translation2d currentPos = instance.getCurrentPosition();

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
                        Waypoint.Type.SOFT
                ),
                new Waypoint(
                        controlpoint2,
                        Rotation2d.zero(),
                        Waypoint.Type.SOFT
                ),
                new Waypoint(
                        target,
                        Rotation2d.zero(),
                        Waypoint.Type.HARD
                )
        );

        if (instance.verbose) {
            System.out.println("Generated path: " + path.toString());
        }

        return path;
    }

    public static Translation2d convertTagFieldPosition(VectorF vec) {
        return new Translation2d(
                -vec.getData()[1] + (0.5 * Field.field.getWidth().get(Unit.Type.Inches)),
                -vec.getData()[0] + (0.5 * Field.field.getHeight().get(Unit.Type.Inches))
        );
    }

    private void updatePose() {
        currentDetections = aprilTag.getDetections();

        Translation2d[] detections = new Translation2d[currentDetections.size()];

        if (!currentDetections.isEmpty()) {
            int i = 0;
            for (AprilTagDetection detection : currentDetections) {
                AprilTagMetadata tag = AprilTagGameDatabase.getCenterStageTagLibrary().lookupTag(detection.id);
                AprilTagPoseFtc pose = detection.ftcPose;

                //use the tag data and the pose data to calculate the robot's position
                Translation2d tagRelPos = convertTagFieldPosition(tag.fieldPosition);

                detections[i] = new Translation2d(
                        Unit.convert(tagRelPos.getX() - pose.x, Unit.Type.Inches, Unit.Type.Meters) + (0.065d),
                        Unit.convert(tagRelPos.getY() + pose.y, Unit.Type.Inches, Unit.Type.Meters) + (0.22d)
                );

                //fix later?
                currentRotation = Rotation2d.fromDegrees(
                        pose.yaw
                );

                i++;
            }

            currentPose = Translation2d.average(detections);
        }
    }

    public boolean detecting() {
        return !currentDetections.isEmpty();
    }

    public Pose2d getCurrentPose() {
        return new Pose2d(currentPose, currentRotation);
    }
}

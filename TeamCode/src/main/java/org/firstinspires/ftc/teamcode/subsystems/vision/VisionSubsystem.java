package org.firstinspires.ftc.teamcode.subsystems.vision;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
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
        telemetryAprilTag();
        telemetryTfod();
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
    public Translation2d getCurrentPose() {
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

    /**
     * Telemetry for AprilTag.
     */
    private void telemetryAprilTag() {
        if (!verbose) return;

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry().addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                currentPose = new Translation2d(
                        detection.ftcPose.x,
                        detection.ftcPose.y
                );

                currentRotation = Rotation2d.fromDegrees(
                        detection.ftcPose.yaw
                );

                telemetry().addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry().addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry().addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry().addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry().addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry().addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

    }   // end method TeleOp.getTelemetry()AprilTag()

    /**
     * Add TeleOp.getTelemetry() about TensorFlow Object Detection (TFOD) recognitions.
     */
    private void telemetryTfod() {
        if (!verbose) return;

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry().addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry().addData(""," ");
            telemetry().addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry().addData("- Position", "%.0f / %.0f", x, y);
            telemetry().addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method TeleOp.getTelemetry()Tfod()
    
}

package org.firstinspires.ftc.teamcode.subsystems.vision;

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
    @Override
    public void init() {
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
                //.setLensIntrinsics(3298.7389543652603, 3265.0187042219723, 1165.7536942923, 826.4908289614423)
                .build();

        tfod = new TfodProcessor.Builder()
                .build();

        backboardDetectionPipeline = new BackboardDetectionPipeline(BackboardDetectionPipeline.Strategy.Tower_Of_Babel);
        
        myVisionPortal = new VisionPortal.Builder()
                .setCamera(getHardwareMap().get(WebcamName.class, "Webcam 1"))
                .addProcessors(tfod, aprilTag)
                .build();
    }

    @Override
    public void periodic() {
        telemetryAprilTag();
        telemetryTfod();
    }

    @Override
    public void onDisable() {

    }

    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        TeleOp.getTelemetry().addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                TeleOp.getTelemetry().addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                TeleOp.getTelemetry().addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                TeleOp.getTelemetry().addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                TeleOp.getTelemetry().addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                TeleOp.getTelemetry().addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                TeleOp.getTelemetry().addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

    }   // end method TeleOp.getTelemetry()AprilTag()

    /**
     * Add TeleOp.getTelemetry() about TensorFlow Object Detection (TFOD) recognitions.
     */
    private void telemetryTfod() {
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        TeleOp.getTelemetry().addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            TeleOp.getTelemetry().addData(""," ");
            TeleOp.getTelemetry().addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            TeleOp.getTelemetry().addData("- Position", "%.0f / %.0f", x, y);
            TeleOp.getTelemetry().addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method TeleOp.getTelemetry()Tfod()
    
}

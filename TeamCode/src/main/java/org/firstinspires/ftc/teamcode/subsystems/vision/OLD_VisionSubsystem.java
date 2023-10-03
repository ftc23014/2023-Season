package org.firstinspires.ftc.teamcode.subsystems.vision;


import org.firstinspires.ftc.lib.simulation.Simulation;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.TeleOp;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;

public class OLD_VisionSubsystem extends Subsystem {
    OpenCvWebcam simple;
    double fx = 3298.7389543652603;
    double fy = 3265.0187042219723;
    double cx = 1165.7536942923;
    double cy = 826.4908289614423;
    double tagsize = 0.0865;

    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    public OLD_VisionSubsystem() {
        super();

        if (Simulation.inSimulation()) return;

        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
    }

    @Override
    public void init() {
        if (Simulation.inSimulation()) {
            System.out.println("Not initializing vision subsystem in simulation!");
            return;
        }

        // UNITS ARE METERS


        // UNITS ARE METERS

        int cameraMonitorViewId = getHardwareMap().appContext.getResources().getIdentifier("cameraMonitorViewId", "id", getHardwareMap().appContext.getPackageName());
        simple = OpenCvCameraFactory.getInstance().createWebcam(getHardwareMap().get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        simple.setPipeline(aprilTagDetectionPipeline);

        simple.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                /*
                 * Tell the camera to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Also, we specify the rotation that the camera is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                simple.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
                TeleOp.getTelemetry().addData("Camera Error", "Could not open camera. ErrorCode: " + errorCode);
                TeleOp.getTelemetry().update();
            }
        });
    }

    @Override
    public void periodic() {
        ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getDetectionsUpdate();
        if (detections != null) {
            if (detections.size() == 0) {
                TeleOp.getTelemetry().addLine("No AprilTags detected!");
            } else {
                for (AprilTagDetection detection : detections) {
                    TeleOp.getTelemetry().addLine(String.format("Translation X: %.2f", detection.pose.x));
                    TeleOp.getTelemetry().addLine(String.format("Translation Y: %.2f", detection.pose.y));
                    TeleOp.getTelemetry().addLine(String.format("Translation Z: %.2f", detection.pose.z));
                }
            }
        }

        TeleOp.getTelemetry().update();

    }

    @Override
    public void onDisable() {

    }
}



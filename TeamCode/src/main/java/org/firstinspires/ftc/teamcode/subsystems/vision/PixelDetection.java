package org.firstinspires.ftc.teamcode.subsystems.vision;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

@Autonomous(name="Auto: Pixel Detector")
public class PixelDetection extends LinearOpMode {
    // Handle hardware stuff...

    int width = 960;
    int height = 720;
    // store as variable here so we can access the location
    PixelDetectionPipeline detector = new PixelDetectionPipeline(width);
    OpenCvCamera Camera;

    @Override
    public void runOpMode() {
        // robot logic...

        // https://github.com/OpenFTC/EasyOpenCV/blob/master/examples/src/main/java/org/openftc/easyopencv/examples/InternalCameraExample.java
        // Initialize the back-facing camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("Webcam 1", "class", hardwareMap.appContext.getPackageName());
        Camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        // Connect to the camera
        Camera.openCameraDevice();
        // Use the PixelDetector pipeline
        // processFrame() will be called to process the frame
        Camera.setPipeline(detector);
        // Remember to change the camera rotation
        Camera.startStreaming(width, height, OpenCvCameraRotation.SIDEWAYS_LEFT);

        //...

        PixelDetectionPipeline.PixelLocation location = detector.getLocation();

        waitForStart();

        // more robot logic...
    }

}
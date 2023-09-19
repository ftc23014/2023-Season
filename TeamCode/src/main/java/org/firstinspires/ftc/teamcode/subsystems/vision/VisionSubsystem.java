package org.firstinspires.ftc.teamcode.subsystems.vision;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

public class VisionSubsystem extends Subsystem {
    OpenCvWebcam simple;

    AprilTagDetectionPipeline AprilTagDetectionPipeline;

    @Override
    public void init() {
        double fx = 3298.7389543652603;
        double fy = 3265.0187042219723;
        double cx = 1165.7536942923;
        double cy = 826.4908289614423;

        // UNITS ARE METERS
        double tagsize = 0.0865;

        int cameraMonitorViewId = getHardwareMap().appContext.getResources().getIdentifier("cameraMonitorViewId", "id", getHardwareMap().appContext.getPackageName());
        simple = OpenCvCameraFactory.getInstance().createWebcam(getHardwareMap().get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        simple.setPipeline(new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy));

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
                telemetry.addData("Camera Error", "Could not open camera. ErrorCode: " + errorCode);
                telemetry.update();
            }
        });
    }

    @Override
    public void periodic() {

    }

    @Override
    public void onDisable() {

    }
}



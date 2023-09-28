package org.firstinspires.ftc.teamcode.subsystems.vision;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.tensorflow.lite.TensorFlowLite;

public class BackboardDetectionPipeline extends OpenCvPipeline {

    @Override
    public Mat processFrame(Mat input) {
        //convert to hsv
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);

        //convert to grayscale
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2GRAY);

        //find contours

        return null;
    }
}

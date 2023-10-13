package org.firstinspires.ftc.teamcode.subsystems.vision;

import android.graphics.Canvas;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.tensorflow.lite.TensorFlowLite;

public class BackboardDetectionPipeline implements VisionProcessor {

    public enum Strategy {
        //place the pixel on the same row it's in currently unless it's above a certain threshold.
        //strategy is on the left side of the backboard.
        //if there will be little time for placing, this is the best strategy, but risky. can result in pixels falling off.
        //Named after the Greek myth of sisyphus, who was punished by the gods to roll a boulder up a hill,
        //only for it to roll back down when he reached the top. This is similar to the strategy of placing,
        //where it emphasizes the risk of the pixel falling off.
        Sisyphus,

        //creates a tower in the center of the backboard, and places the pixel in the center of the tower.
        //generally, a better strategy for points if it's confirmed that the 30pt threshold can be reached.
        //also better for mosaics in terms of strategy.
        //Named after the Tower of Babel, a tower built by the people of Babylon to reach the heavens.
        //This is similar to the strategy of placing, where it makes a strong foundation for the pixels
        //to be placed in more planned positions, resulting in a better mosaic.
        Tower_Of_Babel,

        //places the pixel at the best place to construct the same shape as sisyphus, but
        //does it in a more constructive/methodic way.
        //takes more time, but is reliable and can guarantee 30pts mark reached.
        //it's between sisyphus and tower of babel in terms of strategy.
        //questionable for mosaic planning though.
        //Named after Khufu, the pharaoh who built the Great Pyramid of Giza in relation to the
        //construction being planned out and methodic, as well as the shape of the final product.
        Khufu,
    }

    private Strategy strategy;

    public BackboardDetectionPipeline(Strategy strategy) {
        super();

        this.strategy = strategy;
    }

    public void switchStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }

    @Override
    public Object processFrame(Mat input, long captureTimeNanos) {
        //convert to hsv
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);

        //convert to grayscale
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2GRAY);

        //find contours

        return input;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {

    }
}

package org.firstinspires.ftc.teamcode.subsystems.vision;

import android.graphics.Canvas;
import android.util.Pair;
import org.firstinspires.ftc.lib.Lambda;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;
import org.tensorflow.lite.TensorFlowLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackboardDetectionPipeline implements VisionProcessor {

    public static BackboardDetectionPipeline instance;

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

    private boolean requestFrame = false;

    private final boolean drawGraphics = true;

    private List<Double> backboardPercentages = new ArrayList<>();

    private Lambda onFrameProcessed;

    public BackboardDetectionPipeline(Strategy strategy) {
        super();

        instance = this;

        this.strategy = strategy;
        this.backboardPercentages = new ArrayList<>();
    }

    public void requestFrame(Lambda onFrameProcessed) {
        this.onFrameProcessed = onFrameProcessed;
        requestFrame = true;
    }

    public void switchStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Double> getBackboardPercentages() {
        return backboardPercentages;
    }

    // # UTILS SECTION

    private boolean toverlap(Translation2d rect1pos, Translation2d rect1opos, Translation2d rect2pos, Translation2d rect2opos) {
        if (rect1opos.getX() >= rect2opos.getX()
                || rect1opos.getY() <= rect2pos.getY()
                || rect1opos.getX() <= rect2pos.getY()
                || rect1pos.getY() >= rect2opos.getY()
        ) {
            return false;
        }

        return true;
    }

    private boolean tmoverlap(Translation2d rect1pos, Translation2d rect1size, Translation2d rect2pos, Translation2d rect2size) {
        return toverlap(rect1pos, rect1pos.translateBy(rect1size), rect2pos, rect2pos.translateBy(rect2size));
    }

    private boolean overlap(Rect rect1, Rect rect2) {
        return toverlap(new Translation2d(rect1.x, rect1.y), new Translation2d(rect1.x + rect1.width, rect1.y + rect1.height), new Translation2d(rect2.x, rect2.y), new Translation2d(rect2.x + rect2.width, rect2.y + rect2.height));
    }

    private boolean similarContours(MatOfPoint c1, MatOfPoint c2) {
        return c1.size() == c2.size() && Imgproc.boundingRect(c1).equals(Imgproc.boundingRect(c2));
    }

    private double det(Pair<Double, Double> a, Pair<Double, Double> b) {
        return a.first * b.second - a.second * b.first;
    }

    private Translation2d line_intersection(Translation2d line1p1, Translation2d line1p2, Translation2d line2p1, Translation2d lin2p2) {
        double xdiff1 = line1p1.getX() - line1p2.getX();
        double xdiff2 = line2p1.getX() - lin2p2.getX();
        double ydiff1 = line1p1.getY() - line1p2.getY();
        double ydiff2 = line2p1.getY() - lin2p2.getY();

        double div = det(new Pair<>(xdiff1, xdiff2), new Pair<>(ydiff1, ydiff2));
        if (div == 0) {
            //lines do not intersect
            return null;
        }

        double d = det(new Pair<>(line1p1.getX(), line1p2.getX()), new Pair<>(line1p1.getY(), line1p2.getY()));
        double e = det(new Pair<>(line2p1.getX(), lin2p2.getX()), new Pair<>(line2p1.getY(), lin2p2.getY()));

        double x = det(new Pair<>(d, e), new Pair<>(xdiff1, xdiff2)) / div;
        double y = det(new Pair<>(d, e), new Pair<>(ydiff1, ydiff2)) / div;

        return new Translation2d(x, y);
    }

    // # END UTILS SECTION

    // # PROCESSING SECTION
    @Override
    public Mat processFrame(Mat input, long captureTimeNanos) {
        //we don't want to process the frame if we don't need to. this is to save processing power since it's a bit resource intensive.
        if (!requestFrame) return input;

        requestFrame = false;

        Mat image = input.clone();
        Mat copy = input.clone();

        //convert to grayscale
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);

        Mat thresh = new Mat();

        double ret = Imgproc.threshold(image, thresh,230, 240, 240);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        if (drawGraphics) {
            Imgproc.drawContours(copy, contours, -1, new Scalar(255,255,0), 5);
        }

        //sort by area, highest to lowest
        contours.sort((o1, o2) -> (int) (Imgproc.contourArea(o2) - Imgproc.contourArea(o1)));

        List<MatOfPoint> highContours = new ArrayList<>();

        int i = 0;
        int j = 0;

        //go through all the contours, remove any that are too small, or the ratio is not like the backboard lines (using a 1:5 ratio here)
        for (MatOfPoint c : contours) {
            Rect rect = Imgproc.boundingRect(c);

            if (rect.width * rect.height < 1000) {
                i++;
                continue;
            }

            if (rect.width / rect.height < 5) {
                i++;
                continue;
            }

            Imgproc.drawContours(input, contours, i, new Scalar(255, 255, 0), 5);

            highContours.add(c);

            i++;
            j++;
            if (j >= 5) {
                break;
            }
        }

        //sort the high contours by area, highest to lowest
        highContours.sort((o1, o2) -> (int) (Imgproc.contourArea(o2) - Imgproc.contourArea(o1)));

        //if there's less than 2 contours, then we can't do the calculation.
        if (highContours.size() < 2) {
            //report nothing found
            return input;
        }


        List<Rect> preCombinedContours = new ArrayList<>();

        //go through all the contours and combine the ones that are close together
        for (MatOfPoint c : highContours) {
            Rect rect = Imgproc.boundingRect(c);

            List<Rect> closeby = new ArrayList<>();

            int hci = 0;

            for (MatOfPoint hc : highContours) {
                if (similarContours(c, hc)) {
                    hci++;
                    continue;
                }

                Rect rect2 = Imgproc.boundingRect(hc);

                if (overlap(rect, rect2)) {
                    closeby.add(rect2);
                    highContours.remove(hci);
                    hci--;
                }

                hci++;
            }

            if (!closeby.isEmpty()) {
                Rect combined = rect;

                for (Rect r : closeby) {
                    combined = new Rect(
                            Math.min(combined.x, r.x),
                            Math.min(combined.y, r.y),
                            Math.max(combined.width, r.width) - Math.min(combined.x, r.x),
                            Math.max(combined.y + combined.height, r.y + r.height) - Math.min(combined.y, r.y)
                    );
                }

                preCombinedContours.add(combined);
            }
        }


        List<Rect> combinedContours = new ArrayList<>();

        //quick second pass through to combine the contours again just in case

        i = 0;

        for (Rect rect : preCombinedContours) {
            List<Rect> closeby = new ArrayList<>();

            int hci = 0;

            for (Rect r : preCombinedContours) {
                if (i == hci) {
                    hci++;
                    continue;
                }

                if (overlap(rect, r)) {
                    closeby.add(r);
                    preCombinedContours.remove(hci);
                    hci--;
                }

                hci++;
            }

            if (!closeby.isEmpty()) {
                Rect combined = rect;

                for (Rect r : closeby) {
                    combined = new Rect(
                            Math.min(combined.x, r.x),
                            Math.min(combined.y, r.y),
                            Math.max(combined.width, r.width) - Math.min(combined.x, r.x),
                            Math.max(combined.y + combined.height, r.y + r.height) - Math.min(combined.y, r.y)
                    );
                }

                combinedContours.add(combined);
            }
        }

        //find the average combined area
        double avgCombinedArea = 0d;

        for (Rect rect : combinedContours) {
            avgCombinedArea += rect.area();
        }

        //if there's more than three, reduce the amount of contours by removing the ones that are less than half the average area
        if (combinedContours.size() > 3) {
            avgCombinedArea /= combinedContours.size();
            i = 0;

            for (Rect rect : combinedContours) {
                if (rect.area() < avgCombinedArea * 0.5) {
                    combinedContours.remove(i);
                    i--;
                    //quick prevent too small
                    if (combinedContours.size() == 2) break;
                }

                i++;
            }
        }

        //draw combined contours
        if (drawGraphics) {
            for (Rect rect : combinedContours) {
                Imgproc.rectangle(copy, rect, new Scalar(255, 0, 0), 5);
            }
        }

        //sort by y value
        combinedContours.sort((o1, o2) -> (int) (o1.y - o2.y));

        //if there's less than 2 contours, then we can't do the calculation.
        if (combinedContours.size() < 2) {
            //unable to do the calculation.
            return input;
        }

        //hand it off to the processing function once we know the bar locations
        return processBar(input, combinedContours.get(0), combinedContours.get(1));
    }

    private Mat processBar(Mat input, Rect bar1, Rect bar2) {
        Mat copy = input.clone();
        Mat image = input.clone();

        //general drawing things, not really important, drawing the sides of the backboard
        double slope = ((double) bar2.y - bar1.y) / ((double) bar2.x - bar1.x);
        double y_val = slope * -bar1.x + bar1.y;

        if (drawGraphics) {
            Imgproc.line(copy, new org.opencv.core.Point(bar1.x, bar1.y), new org.opencv.core.Point(0, (int) Math.round(y_val)), new Scalar(0, 255, 0), 5);
        }

        double slope2 = (bar2.y - bar1.y) / ((bar2.x + bar2.width) - (bar1.x + bar1.width));

        y_val = slope2 * (input.width() - bar1.x - bar1.width) + bar1.y;

        if (drawGraphics) {
            Imgproc.line(copy, new org.opencv.core.Point(bar1.x + bar1.width, bar1.y), new org.opencv.core.Point(input.width(), (int) Math.round(y_val)), new Scalar(0, 255, 0), 5);
        }

        int center_x = (bar1.x + bar1.width) / 2;
        int center_y = (bar1.y + bar1.height) / 2;

        //get the general intersection between the two sides, used for drawing again.
        Translation2d ipoint = line_intersection(
                new Translation2d(bar1.x, bar1.y),
                new Translation2d(0, (int) Math.round(y_val)),
                new Translation2d(bar1.x + bar1.width, bar1.y),
                new Translation2d(input.width(), (int) Math.round(y_val))
        );

        final int sections = 5;

        //track how long it takes to process this.
        long start = System.currentTimeMillis();

        ArrayList<Pair<Double, Double>> backboardInfo = new ArrayList<>();

        //loop through sections
        for (int i = 0; i <= sections; i++) {
            //again, general drawing things, draw the "sections" of the backboard.
            Translation2d point_between = new Translation2d(
                    bar1.x + Math.round(bar1.width + ((float) i / sections)),
                    bar1.y
            );

            double p_slope = (point_between.getY() - ipoint.getY()) / (point_between.getX() - ipoint.getX());

            double p_y_val = p_slope * (-point_between.getX()) + point_between.getY();
            int s = 0;

            if (p_y_val < point_between.getY()) {
                p_y_val = p_slope * (image.width() - point_between.getX()) + point_between.getY();
                s = image.width();
            }

            if (drawGraphics && !(i == 0 || i == sections)) {
                Imgproc.line(copy, new org.opencv.core.Point(point_between.getX(), point_between.getY()), new org.opencv.core.Point(s, (int) Math.round(p_y_val)), new Scalar(0, 255, 0), 5);
            }

            //gets a bit weird when i = sections, so we just skip it.
            if (i == sections) {
                continue;
            }

            //real work, split the mat into submats based on the section sizes and process them.
            Translation2d p2 = new Translation2d(
                    point_between.getX() + Math.round(bar1.width * ((float) 1 / sections)),
                    image.height()
            );

            Pair<Double, Double> dist = processSingleBar(
                    image.submat(new Rect(
                            new Point(point_between.getX(), point_between.getY()),
                            new Point(p2.getX(), p2.getY())
                    ))
            );

            //add the info from processing the bar to the list.
            backboardInfo.add(
                    dist
            );
        }

        //get the highest bottom of the backboard, generally the correct one since it's not always accurate.
        double highestBottomOfBackboard = 0d;

        for (Pair<Double, Double> pair : backboardInfo) {
            if (pair.second < highestBottomOfBackboard) {
                highestBottomOfBackboard = pair.second;
            }
        }

        //draw a line accross for the bottom of the backboard.
        if (drawGraphics) {
            Imgproc.line(copy, new Point(
                    0, highestBottomOfBackboard + bar1.y
            ), new Point(
                    image.width(),
                    highestBottomOfBackboard + bar1.y
            ), new Scalar(0, 0, 255), 5);
        }

        System.out.println("total time taken: " + (System.currentTimeMillis() - start) + "ms");

        //get the percentages of the backboard from the bottom of the backboard to the bottom of the top bar.
        ArrayList<Double> percentages = new ArrayList<>();

        for (Pair<Double, Double> pair : backboardInfo) {
            double heightUpToBackboard = highestBottomOfBackboard - pair.first;
            double percentage = heightUpToBackboard / (highestBottomOfBackboard - bar1.y);

            percentages.add(percentage);
        }

        //set the percentages
        backboardPercentages = percentages;

        //run the event lambda if it's not null
        if (onFrameProcessed != null) {
            onFrameProcessed.run();
        }

        return copy;
    }

    private Pair<Double, Double> processSingleBar(Mat input) {
        /** general settings */

        //how many pixels to skip when graphing, lower = more accurate but slower, higher = less accurate but faster
        final int per = 8;
        //how many pixels in a row must be empty to be considered empty
        final int empty_count_threshold = 50;
        //how many pixels in a row must be taken to be considered "in pixels" and has passed the bottom of the empty space
        final int taken_count_threshold = 100;
        //the y value the pixel stack starts after, to prevent false positives due to lighting/etc
        final int stack_starts_after = 300;

        /** end settings */

        Mat img = input.clone();

        //timing to see how long it takes to process a single bar
        long start_time = System.currentTimeMillis();

        //convert to grayscale
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY);

        Mat edges = img.clone();
        //get the edges of the image
        Imgproc.Canny(img, edges, 50, 150, 3);

        //find those edges, and get the contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //garbage collect hierarchy
        hierarchy.release();

        List<Rect> rects = new ArrayList<>();

        //loop through, convert those contours to rects
        for (MatOfPoint c : contours) {
            Rect rect = Imgproc.boundingRect(c);

            rects.add(rect);
        }

        //garbage collect contours
        contours.clear();

        //sort by y value
        rects.sort((o1, o2) -> (int) (o1.y - o2.y));

        List<Integer> graph = new ArrayList<>();

        //loop through the image, and graph the amount of rects in each row
        for (int y = 0; y < img.height(); y += per) {
            int count = 0;

            for (Rect rect : rects) {
                if (rect.y <= y && rect.y + rect.height >= y) {
                    count++;
                }
            }

            graph.add(count);
        }

        //garbage collect rects
        rects.clear();

        //get the largest value in the graph, which is the amount of rects in a row
        int largest = Collections.max(graph);

        int free_count = 0;
        int lowest_free_count = 0;
        int taken_c = 0;
        boolean passedLargestValue = false;

        int bottomOfBackboard = 0;

        //loop through the graph, and find the lowest free count, and the bottom of the backboard
        for (int y = 0; y < graph.size(); y++) {
            if (graph.get(y) > 0) {
                if (free_count > 0 && !passedLargestValue) {
                    if (free_count >= empty_count_threshold) {
                        lowest_free_count = y;
                    }

                    free_count = 0;
                }

                taken_c += per;

                if (graph.get(y) == largest) {
                    bottomOfBackboard = y * per;
                }

                if (taken_c >= taken_count_threshold && y * per > stack_starts_after && !passedLargestValue) {
                    passedLargestValue = true;
                }
            } else {
                taken_c = 0;
                free_count += per;
            }
        }

        System.out.println("Time taken for single bar processing: " + (System.currentTimeMillis() - start_time) + "ms");

        return new Pair<>((double) lowest_free_count, (double) bottomOfBackboard);
    }

    // # END PROCESSING SECTION

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {

    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }
}

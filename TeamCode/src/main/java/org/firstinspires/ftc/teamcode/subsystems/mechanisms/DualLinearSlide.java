package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.teamcode.subsystems.vision.BackboardDetectionPipeline;

import java.util.List;

public class DualLinearSlide extends Subsystem {
    /***
     * TODO:
     * Use PID control to control the position of the linear slide.
     * Add in positions for the linear slide to go to (may want to implement that in a command though).
     *
     * note: probably the hardest subsystem. going to be a bit of a pain to tune.
     * 1. we'll know the initial position of the linear slide, so that's good for auto.
     * 2. we need to finish auto with the slides retracted, or else we'll have a problem/buggy behavior during teleop.
     * 3. needs to have tuned PID values, and we need to make sure that the PID values are good for both extending and retracting.
     * 4. figure out accurate positions, and make sure that they are consistent.
     */

    private final Rotation2d linearSlideAngle = Rotation2d.fromDegrees(30);

    double groundToBottomOfPlacingPosition = 0;
    double backboardAngle = 0;
    double groundToBottomOfLinearSlide = 0;

    Unit distanceFromBottomOfBackboardToThirdLevel = new Unit(0, Unit.Type.Meters);

    public double getSlideHeightFromBackboardDistance(double distanceUpBackboard) {
        double total = distanceUpBackboard + groundToBottomOfPlacingPosition;

        double h = total * Math.sin(
                Math.PI - backboardAngle
        );

        return h - groundToBottomOfLinearSlide;
    }
    
    public double getHeightOfLinearSlideFromBackboardDistance(double distanceUpBackboard) {
        double slideHeight = getSlideHeightFromBackboardDistance(distanceUpBackboard);
        
        return slideHeight / Math.sin(Math.PI - linearSlideAngle.getRadians());
    }

    public double calculateDistanceFromBackboardFromLinearSlideHeight(double linearSlideHeight, double perpDistanceFromGround) {
        return Math.sqrt(Math.pow(linearSlideHeight, 2) - Math.pow(
                perpDistanceFromGround - groundToBottomOfLinearSlide, 2
        ));
    }

    public Translation2d getPointToMoveTo() {
        //get the percentages from the backboard pipeline
        List<Double> bbps = BackboardDetectionPipeline.instance.getBackboardPercentages();

        //convert the percentages to meters from the backboard
        bbps.replaceAll(aDouble -> aDouble * distanceFromBottomOfBackboardToThirdLevel.get(Unit.Type.Meters));

        //run the algorithm to get the most optimal point to move to

        return Translation2d.zero();
    }
}

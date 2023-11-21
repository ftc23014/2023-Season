package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
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

    private DcMotor leftSlideMotor;
    private DcMotor rightSlideMotor;

    private Gamepad gamepad;

    private final Rotation2d linearSlideAngle = Rotation2d.fromDegrees(30);

    private final Unit groundToBottomOfPlacingPosition = new Unit(20, Unit.Type.Centimeters);
    private final Rotation2d backboardAngle = Rotation2d.fromDegrees(120);
    private final Unit groundToBottomOfLinearSlide = new Unit(4, Unit.Type.Centimeters);

    Unit distanceFromBottomOfBackboardToThirdLevel = new Unit(0, Unit.Type.Meters);


    public DualLinearSlide() {
        super();

        leftSlideMotor = getHardwareMap().dcMotor.get("Linear_Motor1");
        rightSlideMotor = getHardwareMap().dcMotor.get("Linear_Motor2");
    }

    public void setPower(double p) {
        leftSlideMotor.setPower(p);
        rightSlideMotor.setPower(-p);
    }

    public Unit getSlideHeightFromBackboardDistance(Unit distanceUpBackboard) {
        double total = distanceUpBackboard.get(Unit.Type.Centimeters) + groundToBottomOfPlacingPosition.get(Unit.Type.Centimeters);

        double h = total * Math.sin(
                Math.PI - backboardAngle.getRadians()
        );

        return new Unit(h - groundToBottomOfLinearSlide.get(Unit.Type.Centimeters), Unit.Type.Centimeters);
    }
    
    public Unit getHeightOfLinearSlideFromBackboardDistance(Unit distanceUpBackboard) {
        double slideHeight = getSlideHeightFromBackboardDistance(distanceUpBackboard).get(Unit.Type.Centimeters);
        
        return new Unit(slideHeight / Math.sin(Math.PI - linearSlideAngle.getRadians()), Unit.Type.Centimeters);
    }

    public Unit calculateDistanceFromBackboardFromLinearSlideHeight(Unit linearSlideHeight, Unit perpDistanceFromGround) {
        return new Unit(Math.sqrt(Math.pow(linearSlideHeight.get(Unit.Type.Centimeters), 2) - Math.pow(
                perpDistanceFromGround.get(Unit.Type.Centimeters) - groundToBottomOfLinearSlide.get(Unit.Type.Centimeters), 2
        )), Unit.Type.Centimeters);
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

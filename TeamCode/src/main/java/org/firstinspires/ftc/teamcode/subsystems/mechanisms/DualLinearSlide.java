package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.math.WPIPIDController;
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
     * 2. we need to finish auto with the slides retracted, or else we'll have a problem/buggy behavior during teleop. (maybe?)
     * 3. needs to have tuned PID values, and we need to make sure that the PID values are good for both extending and retracting.
     * 4. figure out accurate positions, and make sure that they are consistent.
     */

    private enum Direction {
        UP,
        DOWN,
        NONE;
    }

    public enum ControlType {
        MANUAL,
        PID;
    }

    private final Unit maxLinearSlideHeight = new Unit(0, Unit.Type.Centimeters);
    private final Unit threshold = new Unit(0, Unit.Type.Centimeters);

    private DcMotor leftSlideMotor;
    private DcMotor rightSlideMotor;

    private Direction direction = Direction.NONE;
    private ControlType controlType = ControlType.MANUAL;


    private Unit currentGoalHeight = new Unit(0, Unit.Type.Meters);

    //need pid controller implemented, prob instantiate wpipidcontroller here

    public DualLinearSlide() {
        super();

        leftSlideMotor = getHardwareMap().dcMotor.get("Linear_Motor1");
        rightSlideMotor = getHardwareMap().dcMotor.get("Linear_Motor2");
    }

    @Override
    public void periodic() {
        if (direction == Direction.DOWN) {
            //stop if less than the threshold
            if (
                    getLeftPosition() < threshold.get(Unit.Type.Centimeters)
                            || getRightPosition() < threshold.get(Unit.Type.Centimeters)
            ) {
                setPower(0);
                direction = Direction.NONE;
                controlType = ControlType.MANUAL;
            }
        } else if (direction == Direction.UP) {
            //stop if we're near the top
            if (
                    getLeftPosition() > maxLinearSlideHeight.get(Unit.Type.Centimeters) - threshold.get(Unit.Type.Centimeters)
                            || getRightPosition() > maxLinearSlideHeight.get(Unit.Type.Centimeters) - threshold.get(Unit.Type.Centimeters)
            ) {
                setPower(0);
                direction = Direction.NONE;
                controlType = ControlType.MANUAL;
            }
        }

        if (controlType == ControlType.PID) {
            //PID stuff
        }
    }

    public void setMoveUp() {
        setPower(0.5);
    }

    public void setMoveDown() {
        setPower(-0.5);
    }

    public ControlType getMode() {
        return controlType;
    }

    /**
     * Sets the position of the linear slide.
     * @param height the height to set the linear slide to
     */
    public void setPosition(Unit height) {
        controlType = ControlType.PID;
        //constrict the height to be between 0 and the max height
        currentGoalHeight = height.clamp(Unit.zero(), maxLinearSlideHeight);
    }

    /**
     * Sets the power of the linear slide.
     * @param p the power to set the linear slide to, [-1, 1]. + is up, - is down.
     */
    public void setPower(double p) {
        controlType = ControlType.MANUAL;

        //also check if we're going to go past the max height or below threshold
        if (p > 0) {
            if (getLeftPosition() > maxLinearSlideHeight.get(Unit.Type.Centimeters) - threshold.get(Unit.Type.Centimeters)
                    || getRightPosition() > maxLinearSlideHeight.get(Unit.Type.Centimeters) - threshold.get(Unit.Type.Centimeters)) {
                p = 0;
            }
        } else if (p < 0) {
            if (getLeftPosition() < threshold.get(Unit.Type.Centimeters)
                    || getRightPosition() < threshold.get(Unit.Type.Centimeters)) {
                p = 0;
            }
        }

        //constrict the power to be between -1 and 1
        internalSetPower(WPIPIDController.clamp(p, -1, 1));
    }

    /**
     * Sets the power of the linear slide. This is an internal function, and should not be used outside of this class.
     * @param p the power to set the linear slide to, [-1, 1]
     */
    private void internalSetPower(double p) {
        if (p < 0) {
            direction = Direction.DOWN;
        } else if (p > 0) {
            direction = Direction.UP;
        } else {
            direction = Direction.NONE;
        }

        leftSlideMotor.setPower(p);
        rightSlideMotor.setPower(-p);
    }

    @Override
    public void onDisable() {
        setPower(0);
    }


    public double getLeftPosition() {
        return leftSlideMotor.getCurrentPosition();
    }

    public double getRightPosition() {
        return rightSlideMotor.getCurrentPosition();
    }

    /**
     * UTIL FUNCTIONS
     * **/
    private final Rotation2d linearSlideAngle = Rotation2d.fromDegrees(30);

    private final Unit groundToBottomOfPlacingPosition = new Unit(20, Unit.Type.Centimeters);
    private final Rotation2d backboardAngle = Rotation2d.fromDegrees(120);
    private final Unit groundToBottomOfLinearSlide = new Unit(4, Unit.Type.Centimeters);

    Unit distanceFromBottomOfBackboardToThirdLevel = new Unit(0, Unit.Type.Meters);

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

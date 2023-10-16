package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.odometry.motion.ExpectedRelativeMotion;
import org.firstinspires.ftc.lib.replay.Replay;
import org.firstinspires.ftc.lib.replay.log.Log;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;
import org.firstinspires.ftc.lib.systems.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;


public class MecanumDriveSubsystem extends DriveSubsystem {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    //[gyroscope declaration]

    @Log(link="expected_motion_replay")
    private ExpectedRelativeMotion motionProfile;

    @Override
    public void init() {
        //init expected motion profile, used for debugging.
        motionProfile = new ExpectedRelativeMotion(this, 4);

        // init dc motors
        frontLeft = getHardwareMap().dcMotor.get("frontLeft");
        frontRight = getHardwareMap().dcMotor.get("frontRight");
        backLeft = getHardwareMap().dcMotor.get("backLeft");
        backRight = getHardwareMap().dcMotor.get("backRight");

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void drive(Translation2d translation, Rotation2d rotationSpeed, boolean fieldRelative, boolean openLoop) {
        motionProfile.setExpectedMotion(translation, rotationSpeed);
    }

    public Command driveCommand(Translation2d power, double rotate) {
        return new InstantCommand(() -> {
            driveMotors(power, rotate);
        });
    }

    public Command stop() {
        return new InstantCommand(() -> {
            stop();
        });
    }

    public void driveMotors(Translation2d power, double rotate) {
        double drive = power.getY();
        double strafe = power.getX();

        double frontLeftPower = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower = drive - strafe + rotate;
        double backRightPower = drive + strafe - rotate;

        // set power to motors
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

    }
    @Override
    public void onDisable() {
        stop(); // stop all motors
    }

    public void stop_motors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    @Replay(name="expected_motion_replay")
    private void expectedMotionReplay(String json) {
        //todo: fill in
        return;
    }
}

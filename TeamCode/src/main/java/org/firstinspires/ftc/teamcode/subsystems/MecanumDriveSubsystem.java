package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.odometry.motion.ExpectedRelativeMotion;
import org.firstinspires.ftc.lib.replay.Replay;
import org.firstinspires.ftc.lib.replay.log.Log;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.teamcode.StartupManager;


public class MecanumDriveSubsystem extends DriveSubsystem {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private Unit m_maxVelocity = new Unit(1, Unit.Type.Meters); //per second
    private Unit m_velocityLimit = new Unit(1, Unit.Type.Meters); //per second

    private AdafruitBNO055IMU m_imu;
    private Orientation m_angles;
    private Acceleration m_gravity;


    private Rotation2d startingAngle = Rotation2d.zero();

    /**
     * Creates a new MecanumDriveSubsystem.
     * @param maxVelocity The maximum possible velocity of the robot wheels.
     * @param speedLimit The maximum speed of the robot (this is a limit on the wheel velocity).
     */
    public MecanumDriveSubsystem(Unit maxVelocity, Unit speedLimit) {
        super();

        m_maxVelocity = maxVelocity;
        m_velocityLimit = speedLimit;
    }

    public MecanumDriveSubsystem() {
        this(new Unit(1, Unit.Type.Meters), new Unit(1, Unit.Type.Meters));
    }

    //[gyroscope declaration]

    @Log(link="expected_motion_replay")
    private ExpectedRelativeMotion motionProfile;

    @Override
    public void init() {
        //init expected motion profile, used for debugging.
        motionProfile = new ExpectedRelativeMotion(this, 4);

        StartupManager.addCheck(getClass(), "Motor Init");
        StartupManager.addCheck(getClass(), "IMU Init");

        // init dc motors
        frontLeft = getHardwareMap().dcMotor.get("frontLeft");
        frontRight = getHardwareMap().dcMotor.get("frontRight");
        backLeft = getHardwareMap().dcMotor.get("backLeft");
        backRight = getHardwareMap().dcMotor.get("backRight");

        StartupManager.fulfillCheck(getClass(), "Motor Init");

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        setupIMU();

        StartupManager.fulfillCheck(getClass(), "IMU Init");
    }

    private void setupIMU() {
        AdafruitBNO055IMU.Parameters parameters = new AdafruitBNO055IMU.Parameters();
        //parameters.i2cAddr = I2CADDR_ALTERNATE;
        parameters.angleUnit           = AdafruitBNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = AdafruitBNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        //not sure if we need this, generally the IMU worked well.
        //parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample OpMode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        m_imu = getHardwareMap().get(AdafruitBNO055IMU.class, "gyro");
        m_imu.initialize(parameters);
    }

    public void drive(Translation2d translation, Rotation2d rotationSpeed, boolean fieldRelative, boolean openLoop) {
        //First, rotate the motion to the robot's current rotation
        Translation2d rotated = fieldRelative ?
                translation.rotateBy(getAngle().inverse())
                : translation;

        //Second, scale down the motion to the speed limit
        double maxVelocity = m_maxVelocity.get(Unit.Type.Meters);
        double velocityLimit = m_velocityLimit.get(Unit.Type.Meters);

        Translation2d limited = new Translation2d(
                Math.min(rotated.getX(), velocityLimit) / maxVelocity,
                Math.min(rotated.getY(), velocityLimit) / maxVelocity
        );

        motionProfile.setExpectedMotion(limited, rotationSpeed);

        if (openLoop) {
            driveMotors(limited, rotationSpeed.getDegrees());
        } else {
            throw new RuntimeException("Mecanum drive closed loop control has not been implemented yet!");
        }
    }

    public Command driveCommand(Translation2d power, double rotate) {
        return new InstantCommand(() -> {
            driveMotors(power, rotate);
        });
    }

    public Command stop() {
        return new InstantCommand(this::stop_motors);
    }

    public void driveMotors(Translation2d power, double rotate) {
        double drive = power.getY();
        double strafe = power.getX();

        double frontLeftPower = drive + strafe + rotate;
        double frontRightPower = drive - strafe - rotate;
        double backLeftPower = drive - strafe + rotate;
        double backRightPower = drive + strafe - rotate;

        // set power to motors
        frontLeft.setPower(-frontLeftPower);
        frontRight.setPower(-frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

    }
    @Override
    public void onDisable() {
        stop(); // stop all motors
    }

    /**
     * Stops the motors of the robot.
     */
    public void stop_motors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    //TODO: test and return translation2d instead for x and y velocity.
    public Velocity getIMUVelocity() {
        return m_imu.getVelocity();
    }

    /**
     * Gets the real angle of the robot, relative to 0° being the starting angle.
     * @return The real angle of the robot.
     */
    public Rotation2d getAngle() {
        return Rotation2d.fromDegrees(
                m_imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle
        );
    }

    /**
     * Gets the angle of the robot relative to the field orientation.
     * @return The angle of the robot relative to the field orientation.
     */
    public Rotation2d getRealAngle() {
        return Rotation2d.fromDegrees(getRealAngle().getDegrees() - startingAngle.getDegrees());
    }

    public Unit getVelocityLimit() {
        return m_velocityLimit;
    }

    private boolean setupAngleLogging = false;

    @Override
    public void periodic() {
        if (!setupAngleLogging) {
            m_imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
            setupAngleLogging = true;
        }
    }

    @Replay(name="expected_motion_replay")
    private void expectedMotionReplay(String json) {
        //todo: fill in
        return;
    }
}

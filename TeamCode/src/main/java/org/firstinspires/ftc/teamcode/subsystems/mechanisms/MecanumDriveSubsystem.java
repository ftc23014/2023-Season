package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.odometry.MecanumOdometry;
import org.firstinspires.ftc.lib.odometry.motion.ExpectedRelativeMotion;
import org.firstinspires.ftc.lib.replay.Replay;
import org.firstinspires.ftc.lib.replay.log.Log;
import org.firstinspires.ftc.lib.systems.DriveSubsystem;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.teamcode.StartupManager;
import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

import java.util.ArrayList; //Got a warning; java.util.ArrayList<java.lang.Double> ?

public class MecanumDriveSubsystem extends DriveSubsystem {

    private static MecanumDriveSubsystem instance;

    public static MecanumDriveSubsystem instance() {
        return instance;
    }

    public static Unit maxVelocity = new Unit((312d / 60d) * 0.1d * Math.PI, Unit.Type.Meters);

    /**
     * PORTS:
     * Front Left: 2 (left encoder)
     * Front Right: 1 (back encoder)
     * Back Left: 3
     * Back Right: 0 (right encoder)
     * */
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

    private double joyStickX;
    private double joyStickY;
    private double velocityX;
    private double velocityY;

    private MecanumOdometry odometry;

    private final double baseTrackRadius = 0.0; //Change once determined

    /**
     * Creates a new MecanumDriveSubsystem.
     * @param maxVelocity The maximum possible velocity of the robot wheels.
     * @param speedLimit The maximum speed of the robot (this is a limit on the wheel velocity).
     */
    public MecanumDriveSubsystem(Unit maxVelocity, Unit speedLimit) {
        super();

        instance = this;

        m_maxVelocity = maxVelocity;
        m_velocityLimit = speedLimit;

        odometry = MecanumOdometry.create(
                Autonomous.getStartingPosition(),
                true
        );
    }

    public MecanumDriveSubsystem() {
        this(maxVelocity, new Unit(1, Unit.Type.Meters));
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
                translation.rotateBy(getAngle())
                : translation;

        //Second, scale down the motion to the speed limit
        double maxVelocity = m_maxVelocity.get(Unit.Type.Meters);
        double velocityLimit = m_velocityLimit.get(Unit.Type.Meters);

        Translation2d limited = new Translation2d(
                -Math.min(rotated.getX(), velocityLimit) / maxVelocity,
                Math.min(rotated.getY(), velocityLimit) / maxVelocity
        );

        motionProfile.setExpectedMotion(limited, rotationSpeed);

        if (openLoop) {
            driveMotors(limited, rotationSpeed.getDegrees());
        } else {
            throw new RuntimeException("Mecanum drive closed loop control has not been implemented yet!");
        }

        joyStickX = translation.getX();
        joyStickY = translation.getY();

        // Calculate velocity based on joystick inputs
        velocityX = joyStickX * maxVelocity;
        velocityY = joyStickY * maxVelocity;
    }

    public Command driveCommand(Translation2d power, Rotation2d rotate, boolean fieldRelative, boolean openLoop) {
        return new InstantCommand(() -> {
            drive(power, rotate, fieldRelative, openLoop);
        });
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
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(-backLeftPower); //back left uses gears so it needs to be reversed
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

    public MecanumOdometry getOdometry() {
        return odometry;
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
//        telemetry().addData("Joystick X", joyStickX);
//        telemetry().addData("Joystick Y", joyStickY);
//        telemetry().addData("Velocity X (m/s)", velocityX);
//        telemetry().addData("Velocity Y (m/s)", velocityY);
        odometry.updateOdometry((double) frontLeft.getCurrentPosition(), (double) -backRight.getCurrentPosition(), (double) frontRight.getCurrentPosition());
    }

    /**
     * @return [left, right, back]
     * */
    public double[] getOdoPositions() {
        return new double[] {
                frontLeft.getCurrentPosition(),
                backRight.getCurrentPosition(),
                frontRight.getCurrentPosition()
        };
    }

    @Replay(name="expected_motion_replay")
    private void expectedMotionReplay(String json) {
        //todo: fill in
        return;
    }


    /**
     * Kinematics - For determining position of wheels (end effector?) during driving
     * Kinematics based off of Mecanum Drive
     * ! Forward Kinematics = Joint space -> Cartesian Space
     * ! Inverse Kinematics = Cartesian Space -> Joint Space
     */
    public ArrayList kinematicsAll (double LVFR, double LVBR, double LVFL, double LVBL, double VFrelative, double Vstrafe, double Omega)
    {
        ArrayList <Double> calculatedValues= new ArrayList();

        double Vfr = LVFR; //linear velocity of the front (leading) right wheel
        double Vbr = LVBR; //linear velocity of the back (trailing) right wheel
        double Vfl = LVFL; //linear velocity of the front (leading) left wheel
        double Vbl = LVBL; //linear velocity of the back (trailing) left wheel

        double Vf = VFrelative;//FORWARD velocity of robot body relative to itself
        double Vs = Vstrafe; //strafe (sideways) velocity of the robot, relative to itself.

        double omega =  Omega;//rotational(angular) velocity of robot //radians per second //+ values COUNTERCLOCKWISE from above
        double Rb = baseTrackRadius;//base track radius = distance between wheel and center of robot (1/2 wheel distance from each other)

        //linear velocity conversion says: Wheel rotational velocity in radians/second can be
        // converted to linear velocity by multiplying by the wheel’s radius

        /**
         Forward Kinematics: relate the velocity of the wheels to the forward and
         rotational velocities of the robot, relative to itself.
         */
        Vf = ((Vfr + Vbr + Vfl + Vbl)/4);
        Vs = ((Vfr + Vbl - Vfl - Vbr)/4);
        omega = ((Vbr + Vfr - Vfl - Vbl)/(4*2*Rb));

        /**
         * Inverse Kinematics: relate the desired velocity of the robot to the velocity required of the wheels
         */
        Vfl = Vf - Vs - (2*Rb*omega);
        Vbl = Vf + Vs - (2*Rb*omega);
        Vbr = Vf - Vs + (2*Rb*omega);
        Vfr = Vf + Vs + (2*Rb*omega);

        calculatedValues.add(Vf);
        calculatedValues.add(Vs);
        calculatedValues.add(Vfl);
        calculatedValues.add(Vbl);
        calculatedValues.add(Vbr);
        calculatedValues.add(Vfr);

        return(calculatedValues);
    }
}

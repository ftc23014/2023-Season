package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;

/**
 * Just an example of how to move a motor with a subsystem.
 */
public class BucketSubsystem extends Subsystem {
    Servo bucketPusherServo;
    Servo bucketFlipperServo;

    Gamepad gamepad;

    public void setGamepad(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public void init() {
        bucketFlipperServo = getHardwareMap().get(Servo.class, "bucket_flipper");
        bucketPusherServo = getHardwareMap().get(Servo.class, "bucket_pusher");
    }

    @Override
    public void periodic() {

    }

    // TODO: check that these values are good (also for other subs)
    @Override
    public void onDisable() {
        bucketFlipperServo.setPosition(0.0);
        bucketPusherServo.setPosition(0.0);
        }
    }


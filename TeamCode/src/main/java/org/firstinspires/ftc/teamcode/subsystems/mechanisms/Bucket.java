package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Bucket extends Subsystem {
    private Servo bucketPusherServo;
    private Servo bucketFlipperServo;

    public Bucket() {
        super();
    }

    @Override
    public void init() {
        bucketFlipperServo = getHardwareMap().get(Servo.class, "bucket_flipper");
        bucketPusherServo = getHardwareMap().get(Servo.class, "bucket_pusher");
    }

    public Command setDeploy() {
        return new InstantCommand(this::deploy);
    }

    public void deploy() {
        bucketFlipperServo.setPosition(0.7246 /* flipped servo position*/);
        bucketPusherServo.setPosition(0.9082 /* pushed bucket position */);
    }

    public Command setRetract() {
        return new InstantCommand(this::retract);
    }

    public void retract() {
        bucketFlipperServo.setPosition(0.0233);
        bucketPusherServo.setPosition(0.4);
    }

    public Command stopCommand() {
        return new InstantCommand(this::stop);
    }

    public void stop() { //TODO: check that these value are fine
        bucketPusherServo.setPosition(0);
        bucketFlipperServo.setPosition(0);
    }

    @Override
    public void periodic() {
    }
}

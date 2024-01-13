package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.lib.systems.commands.InstantCommand;

public class Bucket extends Subsystem {
    private Servo bucketPusherServo;
    private Servo bucketFlipperServo;

    private Command disableCMD;

    private boolean pushServoDisabled = false;

    public Bucket() {
        super();
    }

    @Override
    public void init() {
        bucketFlipperServo = getHardwareMap().get(Servo.class, "bucket_flipper");
        bucketPusherServo = getHardwareMap().get(Servo.class, "bucket_pusher");
    }

    private Command disablePusher() {
        return new Command() {
            private long startTime;

            @Override
            public void init() {
                startTime = System.currentTimeMillis();
            }

            @Override
            public void execute() {
                if (isCancelled()) return;
                if (System.currentTimeMillis() - startTime < 1000) {
                    return;
                }

                bucketPusherServo.getController().pwmDisable();

                cancel();

                disableCMD = null;
                pushServoDisabled = true;
            }
        };
    }


    public Command setDeploy() {
        return new InstantCommand(this::deployFlipper);
    }

    public Command setDeployPusher() {
        return new InstantCommand(this::deployPusher);
    }

    public Command setRetractPusher() {
        return new InstantCommand(this::retractPusher);
    }

    public void deploy() {
//        if (pushServoDisabled) {
//            bucketPusherServo.getController().pwmEnable();
//            pushServoDisabled = false;
//        }
//
//        if (disableCMD != null) {
//            disableCMD.cancel();
//            disableCMD = null;
//        }

        bucketFlipperServo.setPosition(0.68 /* flipped servo position*/);
    }

    public Command extendForAuto() {
        return new InstantCommand(this::extendForAuto_func);
    }

    public void extendForAuto_func() {
        bucketPusherServo.setPosition(0.55);
    }

    public void deployBoth() {
        deploy();
        deployPusher();
    }

    public void deployFlipper() {
        bucketFlipperServo.setPosition(0.62);
    }

    public void retractBoth() {
        retract();
        retractPusher();
    }

    public void deployPusher() {
        bucketPusherServo.setPosition(0.695/* pushed bucket position */);
    }

    public void retractPusher() {
        bucketPusherServo.setPosition(0.42 /* retracted bucket position */);
    }

    public Command setRetract() {
        return new InstantCommand(this::retract);
    }

    public void retract() {
        bucketFlipperServo.setPosition(0.14);
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
        if (disableCMD != null) {
            disableCMD.execute();
        }
    }
}

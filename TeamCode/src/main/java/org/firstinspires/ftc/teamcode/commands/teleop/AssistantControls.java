package org.firstinspires.ftc.teamcode.commands.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.*;

public class AssistantControls extends Command {


   // private Drone m_droneSubsystem;

    private DualLinearSlide m_linearSlideSubsystem;

    private Hang m_hangSubsystem;

    private Bucket m_bucketSubsystem;

    private Gamepad gamepad2;

    final private boolean k_linearSlidePIDEnabled = true;


    private boolean m_droneLauncherDeployed = false;
    private boolean m_lastDroneLauncherButtonState = false;


    private boolean m_hangDeployed = false;
    private boolean m_lastHangState = false;

    private boolean m_bucketDeployed = false;
    private boolean m_lastBucketState = false;

    private boolean m_lastBucketPusherState = false;

    private boolean m_buckedPusherDeployed = false;

    private boolean m_linearSlideZeroed = true;
    private boolean m_lastLinearSlideButtonState = false;

    private boolean m_lastDpadUpState = false;
    private boolean m_lastDpadDownState = false;
    private boolean m_lastDpadLeftState = false;
    private boolean m_lastDpadRightState = false;

    // bottom to top
    private int m_selectedRow = 0;
    // Left to right
    private int m_selectedColumn = 0;

    private long m_lastTelemetryUpdate = 0;

    public AssistantControls(Gamepad gamepad2, DualLinearSlide linearSlide /*Drone drone*/, Hang hang, Bucket bucket) {
        super();

        this.gamepad2 = gamepad2;
        m_linearSlideSubsystem = linearSlide;
//        m_droneSubsystem = drone;
        m_hangSubsystem = hang;
        m_bucketSubsystem = bucket;
    }

    /**
     * Controls:
     *
     * Left Bumper + Right Bumper: Launch drone
     *
     * A: Toggle linear slide. If linear slide is in manual, will be retracting.
     *    If linear slide is in automatic, will flip between zeroed and PID set.
     * B: Flip & Push Bucket
     * X: Set in place for hang
     * Y: Push Bucket
     *
     * Right Stick: Hang motor
     *
     *
     * DPAD: Select linear slide position selection
     */

    private double position = 0;

    @Override
    public void execute() {

        if (Math.abs(gamepad2.right_stick_y) > 0.05) {
            position += gamepad2.right_stick_y / 100;
            telemetry().addLine("Position: " + position);
            telemetry().update();
        }

        if (gamepad2.right_bumper) {
            position = 1;
            telemetry().addLine("Position: " + position);
            telemetry().update();
        }


        if (Math.abs(gamepad2.left_stick_y) > 0.05) {
            m_linearSlideSubsystem.setPower(-gamepad2.left_stick_y * 0.9);
        } else if (m_linearSlideSubsystem.getMode() == DualLinearSlide.ControlType.MANUAL) {
            m_linearSlideSubsystem.setPower(0);
        }

        //System.out.println("slide pos: " + m_linearSlideSubsystem.getLeftPosition() + " - " + m_linearSlideSubsystem.getRightPosition());

        if (m_lastDroneLauncherButtonState != gamepad2.left_bumper && gamepad2.left_bumper && gamepad2.right_bumper) {
            m_droneLauncherDeployed = !m_droneLauncherDeployed;

            if (m_droneLauncherDeployed) {
                //m_droneSubsystem.setDeploy();
            } else {
                //m_droneSubsystem.setRetract();
            }
        }

        if (m_lastBucketState != gamepad2.b && gamepad2.b) {
            m_bucketDeployed = !m_bucketDeployed;

            if (m_bucketDeployed) {
                m_bucketSubsystem.deployBoth();
                m_buckedPusherDeployed = true;
            } else {
                m_bucketSubsystem.retract();
            }
        }

        if (m_lastBucketPusherState != gamepad2.y && gamepad2.y) {
            if (m_buckedPusherDeployed) {
                m_bucketSubsystem.retractPusher();
            } else {
                m_bucketSubsystem.deployPusher();
            }

            m_buckedPusherDeployed = !m_buckedPusherDeployed;
        }

        if (m_lastLinearSlideButtonState != gamepad2.a && gamepad2.a && k_linearSlidePIDEnabled) {
            m_lastLinearSlideButtonState = gamepad2.a;

            m_linearSlideZeroed = !m_linearSlideZeroed;

            if (m_linearSlideZeroed) {
                m_linearSlideSubsystem.returnToZero();
            } else {
                DualLinearSlide.SlidePosition position = DualLinearSlide.SlidePosition.values()[m_selectedRow];

                m_linearSlideSubsystem.setPosition(position.getHeight());
            }
        }

        if (m_lastHangState != gamepad2.x && gamepad2.x) {
            if (Math.abs(gamepad2.right_stick_y) < 0.05) {
                if (m_hangDeployed) {
                    m_hangSubsystem.hangUp();
                } else {
                    m_hangSubsystem.hangDown();
                }

                m_hangDeployed = !m_hangDeployed;
            }
        }

        if (Math.abs(gamepad2.right_stick_y) > 0.05 && gamepad2.x) {
            m_hangSubsystem.setMotorSpeed(gamepad2.right_stick_y);
        } else {
            m_hangSubsystem.setMotorSpeed(0);
        }

        m_lastDroneLauncherButtonState = gamepad2.left_bumper;
        m_lastBucketState = gamepad2.b;
        m_lastLinearSlideButtonState = gamepad2.a;
        m_lastHangState = gamepad2.x;
        m_lastBucketPusherState = gamepad2.y;

        //selection controls
        if (m_lastDpadDownState != gamepad2.dpad_down && gamepad2.dpad_down) {
            m_selectedRow--;
            if (m_selectedRow < 0) {
                m_selectedRow = 0;
            }
        }

        if (m_lastDpadUpState != gamepad2.dpad_up && gamepad2.dpad_up) {
            m_selectedRow++;
            if (m_selectedRow > 2) {
                m_selectedRow = 2;
            }
        }

        if (m_lastDpadLeftState != gamepad2.dpad_left && gamepad2.dpad_left) {
            m_selectedColumn--;
            if (m_selectedColumn < 0) {
                m_selectedColumn = 0;
            }
        }

        if (m_lastDpadRightState != gamepad2.dpad_right && gamepad2.dpad_right) {
            m_selectedColumn++;
            if (m_selectedColumn > 2) {
                m_selectedColumn = 2;
            }
        }

        m_lastDpadDownState = gamepad2.dpad_down;
        m_lastDpadUpState = gamepad2.dpad_up;
        m_lastDpadLeftState = gamepad2.dpad_left;
        m_lastDpadRightState = gamepad2.dpad_right;

        //share CPU time
        if (System.currentTimeMillis() > m_lastTelemetryUpdate + 50) {
            m_lastTelemetryUpdate = System.currentTimeMillis();

            //telemetry updates
            showSelectionTelemetry();
            telemetry().addLine();
            showComponentStates();

            telemetry().update();
        }
    }

    private void showSelectionTelemetry() {
        String topRow = "|";
        String middleRow = "|";
        String bottomRow = "|";

        for (int i = 0; i < 3; i++) {
            if (i == m_selectedColumn) {
                if (m_selectedRow == 2) {
                    topRow += "X|";
                    middleRow += " |";
                    bottomRow += " |";
                } else if (m_selectedRow == 1) {
                    middleRow += "X|";
                    topRow += " |";
                    bottomRow += " |";
                } else if (m_selectedRow == 0) {
                    bottomRow += "X|";
                    topRow += " |";
                    middleRow += " |";
                }
            } else {
                topRow += "  |";
                middleRow += "  |";
                bottomRow += "  |";
            }
        }

        telemetry().addLine("-------");
        telemetry().addLine(topRow);
        telemetry().addLine(middleRow);
        telemetry().addLine(bottomRow);
        telemetry().addLine("-------");
    }

    private void showComponentStates() {
        telemetry().addLine("Bucket (B): " + (m_bucketDeployed ? "Deployed" : "Retracted"));
        telemetry().addLine("Drone Launcher (Bumpers): " + (m_droneLauncherDeployed ? "Deployed" : "Retracted"));
        telemetry().addLine("Linear Slide (A, ignore b/c F.U): " + (m_linearSlideZeroed ? "Zeroed" : "Not Zeroed"));
        telemetry().addLine("Pixel Clamper (X): " + (m_hangDeployed ? "Deployed" : "Retracted"));
    }

    @Override
    public boolean hasFinished() {
        return false;
    }
}

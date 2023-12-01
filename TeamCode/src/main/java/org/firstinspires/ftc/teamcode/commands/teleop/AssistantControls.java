package org.firstinspires.ftc.teamcode.commands.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.*;

public class AssistantControls extends Command {
    private Intake m_intakeSubsystem;
    private Spatula m_spatulaSubsystem;

    private Drone m_droneSubsystem;

    private DualLinearSlide m_linearSlideSubsystem;

    private PixelClamper m_pixelClamperSubsystem;

    private Gamepad gamepad2;

    final private boolean k_linearSlidePIDEnabled = false;

    private boolean m_spatulaDeployed = false;
    private boolean m_lastSpatulaButtonState = false;

    private boolean m_droneLauncherDeployed = false;
    private boolean m_lastDroneLauncherButtonState = false;

    private boolean m_pixelClamperDeployed = false;
    private boolean m_lastPixelClamperButtonState = false;

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

    public AssistantControls(Gamepad gamepad2, Intake intake, Spatula spatula, DualLinearSlide linearSlide, Drone drone, PixelClamper pixelClamper) {
        super();

        this.gamepad2 = gamepad2;
        m_intakeSubsystem = intake;
        m_spatulaSubsystem = spatula;
        m_linearSlideSubsystem = linearSlide;
        m_droneSubsystem = drone;
        m_pixelClamperSubsystem = pixelClamper;
    }

    /**
     * Controls:
     *
     * Left Bumper + Right Bumper: Launch drone
     *
     * A: Toggle linear slide. If linear slide is in manual, will be retracting.
     *    If linear slide is in automatic, will flip between zeroed and PID set.
     * B: Intake
     * Y: Spatula
     * X: Pixel clamper / Lock
     *
     *
     *
     * DPAD: Select linear slide position selection
     */

    private double position = 0;

    @Override
    public void execute() {

//        if (Math.abs(gamepad2.right_stick_y) > 0.05) {
//            position += gamepad2.right_stick_y / 100;
//            m_spatulaSubsystem.setPosition(position < 0 ? 1 + (position % 1) : position % 1);
//            telemetry().addLine("Position: " + position);
//            telemetry().update();
//        }

//        if (gamepad2.right_bumper) {
//            position = 1;
//            m_spatulaSubsystem.setPosition(position);
//            telemetry().addLine("Position: " + position);
//            telemetry().update();
//        }

        if (!gamepad2.b) {
            if (Math.abs(gamepad2.right_stick_y) > 0.05) {
                m_intakeSubsystem.intake(gamepad2.right_stick_y);
            } else {
                m_intakeSubsystem.stop();
            }
        } else {
            m_intakeSubsystem.intake(0.8);
        }


        if (Math.abs(gamepad2.left_stick_y) > 0.05) {
            m_linearSlideSubsystem.setPower(gamepad2.left_stick_y / 2);
        } else if (m_linearSlideSubsystem.getMode() == DualLinearSlide.ControlType.MANUAL) {
            m_linearSlideSubsystem.setPower(0);
        }

        if (m_lastDroneLauncherButtonState != gamepad2.left_bumper && gamepad2.left_bumper && gamepad2.right_bumper) {
            m_droneLauncherDeployed = !m_spatulaDeployed;

            if (m_droneLauncherDeployed) {
                m_droneSubsystem.setDeploy();
            } else {
                m_droneSubsystem.setRetract();
            }
        }

        if (m_lastSpatulaButtonState != gamepad2.y && gamepad2.y) {
            m_spatulaDeployed = !m_spatulaDeployed;

            if (m_spatulaDeployed) {
                m_spatulaSubsystem.setDeploy();
            } else {
                m_spatulaSubsystem.setRetract();
            }
        }

        if (m_lastLinearSlideButtonState != gamepad2.a && gamepad2.a && k_linearSlidePIDEnabled) {
            m_lastLinearSlideButtonState = gamepad2.a;

            if (m_linearSlideZeroed) {
                if (m_linearSlideSubsystem.isZeroed()) {
                    m_linearSlideZeroed = false;
                }
            } else {
                if (!m_linearSlideSubsystem.isZeroed()) {
                    m_linearSlideZeroed = true;
                }
            }

            if (m_linearSlideZeroed) {
                m_linearSlideSubsystem.returnToZero();
            } else {
                DualLinearSlide.SlidePosition position = DualLinearSlide.SlidePosition.values()[m_selectedColumn];

                m_linearSlideSubsystem.setPosition(position.getHeight());
            }
        }

        if (m_lastPixelClamperButtonState != gamepad2.x && gamepad2.x) {
            m_pixelClamperDeployed = !m_pixelClamperDeployed;

            if (m_pixelClamperDeployed) {
                m_pixelClamperSubsystem.setDeploy();
            } else {
                m_pixelClamperSubsystem.setRetract();
            }
        }

        m_lastDroneLauncherButtonState = gamepad2.left_bumper;
        m_lastSpatulaButtonState = gamepad2.b;
        m_lastLinearSlideButtonState = gamepad2.a;
        m_lastPixelClamperButtonState = gamepad2.x;

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
        telemetry().addLine("Spatula (Y): " + (m_spatulaDeployed ? "Deployed" : "Retracted"));
        telemetry().addLine("Drone Launcher (Bumpers): " + (m_droneLauncherDeployed ? "Deployed" : "Retracted"));
        //f.u = few updates
        telemetry().addLine("Linear Slide (A, ignore b/c F.U): " + (m_linearSlideZeroed ? "Zeroed" : "Not Zeroed"));
        telemetry().addLine("Pixel Clamper (X): " + (m_pixelClamperDeployed ? "Deployed" : "Retracted"));
        telemetry().addLine("Intake (B): doing something");
    }

    @Override
    public boolean hasFinished() {
        return false;
    }
}

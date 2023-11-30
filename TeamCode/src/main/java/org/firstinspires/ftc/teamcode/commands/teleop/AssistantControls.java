package org.firstinspires.ftc.teamcode.commands.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Drone;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.DualLinearSlide;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.Spatula;

public class AssistantControls extends Command {
    private Intake m_intakeSubsystem;
    private Spatula m_spatulaSubsystem;

    private Drone m_droneSubsystem;

    private DualLinearSlide m_linearSlideSubsystem;

    private Gamepad gamepad2;

    private boolean m_spatulaDeployed = false;
    private boolean m_lastSpatulaButtonState = false;

    public AssistantControls(Gamepad gamepad2, Intake intake, Spatula spatula, DualLinearSlide linearSlide, Drone drone) {
        super();

        this.gamepad2 = gamepad2;
        m_intakeSubsystem = intake;
        m_spatulaSubsystem = spatula;
        m_linearSlideSubsystem = linearSlide;
        m_droneSubsystem = drone;
    }

    @Override
    public void execute() {
        if (Math.abs(gamepad2.right_stick_y) > 0.05) {
            m_intakeSubsystem.intake(gamepad2.right_stick_y);
        } else {
            m_intakeSubsystem.stop();
        }

        if (gamepad2.y) {
            m_droneSubsystem.deploy();

        }
        else if (gamepad2.x) {
            m_droneSubsystem.retract();

        }



        if (Math.abs(gamepad2.left_stick_y) > 0.05) {
            m_linearSlideSubsystem.setPower(gamepad2.left_stick_y / 2);
        } else if (m_linearSlideSubsystem.getMode() == DualLinearSlide.ControlType.MANUAL) {
            m_linearSlideSubsystem.setPower(0);
        }

        if (m_lastSpatulaButtonState != gamepad2.b && gamepad2.b) {
            m_spatulaDeployed = !m_spatulaDeployed;

            if (m_spatulaDeployed) {
                m_spatulaSubsystem.setDeploy();
            } else {
                m_spatulaSubsystem.setRetract();
            }
            if (gamepad2.dpad_down){
                m_spatulaSubsystem.setLock();
            }
            else if (gamepad2.dpad_up){
                m_spatulaSubsystem.removeLock();
            }
        }

        m_lastSpatulaButtonState = gamepad2.b;
    }

    @Override
    public boolean hasFinished() {
        return false;
    }
}

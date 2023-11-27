package org.firstinspires.ftc.teamcode.commands.teleop;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Translation2d;
import org.firstinspires.ftc.lib.math.Unit;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

public class DriverControls extends Command {
    private MecanumDriveSubsystem m_mecanumDriveSubsystem;
    private Gamepad gamepad1;

    public DriverControls(Gamepad gamepad1, MecanumDriveSubsystem driveSubsystem) {
        super();

        this.gamepad1 = gamepad1;

        m_mecanumDriveSubsystem = driveSubsystem;
    }

    @Override
    public void execute() {
        if (Math.abs(gamepad1.left_stick_x) > 0.05 || Math.abs(gamepad1.left_stick_y) > 0.05 || Math.abs(gamepad1.right_stick_x) > 0.05) {
            m_mecanumDriveSubsystem.drive(
                    new Translation2d(
                            gamepad1.left_stick_x,
                            gamepad1.left_stick_y
                    ).scalar(m_mecanumDriveSubsystem.getVelocityLimit().get(Unit.Type.Meters)),
                    Rotation2d.fromDegrees(-Math.pow(gamepad1.right_stick_x, 9) * 90),
                    true,
                    true
            );
        } else {
            m_mecanumDriveSubsystem.stop_motors();
        }
    }

    @Override
    public boolean hasFinished() {
        return false;
    }
}

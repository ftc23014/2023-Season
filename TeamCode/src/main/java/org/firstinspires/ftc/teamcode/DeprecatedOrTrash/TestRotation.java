package org.firstinspires.ftc.teamcode.DeprecatedOrTrash;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.systems.Subsystems;
import org.firstinspires.ftc.teamcode.commands.TurnToCommand;
import org.firstinspires.ftc.teamcode.subsystems.mechanisms.MecanumDriveSubsystem;

@TeleOp(name="testing rotation", group="Testing")
public class TestRotation extends OpMode {
    private TurnToCommand command;
    private MecanumDriveSubsystem driveSubsystem;

    private double angle = 0;
    private double rotatingTo = 0;

    @Override
    public void init() {
        driveSubsystem = new MecanumDriveSubsystem();

        Subsystems.onInit(this);
    }

    @Override
    public void loop() {
        angle = Math.atan2(
                gamepad1.left_stick_y,
                gamepad1.left_stick_x
        );

        boolean executing = false;

        if (gamepad1.b) {
            if (command == null) {
                command = new TurnToCommand(Rotation2d.fromRadians(angle), driveSubsystem);
                rotatingTo = angle;
            }

            if (!command.hasFinished()) {
                command.execute();
                executing = true;
            }
        } else {
            command = null;
        }

        telemetry.addLine("line: " + ((angle / Math.PI) * 180) + " degrees");
        telemetry.addLine("executing: " + executing);
        telemetry.addLine("rotating to: " + ((rotatingTo / Math.PI) * 180) + " degrees");
        telemetry.update();

        Subsystems.periodic();
    }
}

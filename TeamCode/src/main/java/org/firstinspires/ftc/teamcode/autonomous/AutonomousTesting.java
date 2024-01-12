package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.lib.math.Pose2d;
import org.firstinspires.ftc.lib.math.Rotation2d;
import org.firstinspires.ftc.lib.math.Unit;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Testing Auto", group = "Testing", preselectTeleOp="Main TeleOp")
public class AutonomousTesting extends OpMode {
    @Override
    public void init() {
        Autonomous.setAutonomous(Autonomous.AutonomousMode.TESTING, Autonomous.StartingSide.BLUE, this).setStartingPosition(new Pose2d(
                Unit.convert(11.25, Unit.Type.Inches, Unit.Type.Meters),
                Unit.convert(104.25, Unit.Type.Inches, Unit.Type.Meters),
                Rotation2d.zero()
        )).init();
    }

    @Override
    public void init_loop() {
        Autonomous.getInstance().init_loop();
    }

    @Override
    public void start() {
        Autonomous.getInstance().start();
    }

    @Override
    public void loop() {
        Autonomous.getInstance().loop();
    }

    @Override
    public void stop() {
        Autonomous.getInstance().stop();
    }
}

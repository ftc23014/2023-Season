package org.firstinspires.ftc.teamcode.autonomous;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.lib.systems.Subsystems;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="main_autonomous")
@Disabled
public class Autonomous extends OpMode {
    private static Autonomous instance;
    private static OpMode k_autoReferral;

    public static Autonomous setAutonomous(AutonomousMode autoMode, OpMode referral) {
        if (instance != null) {
            throw new RuntimeException("Autonomous was already created but you're changing it?");
        }

        k_autoReferral = referral;

        instance = new Autonomous(autoMode);

        return instance;
    }

    public static Autonomous getInstance() {
        if (instance == null) {
            throw new RuntimeException("Autonomous wasn't created!");
        }

        return instance;
    }

    public enum AutonomousMode {
        PICKUP_AUTONOMOUS,
        BACKDROP_AUTONOMOUS;
    }


    private AutonomousMode m_autonomousMode;
    private boolean m_autonomousEnabled;

    public Autonomous(AutonomousMode autoMode) {
        m_autonomousMode = autoMode;
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {
        //This is called when the autonomous is enabled.

        // -- ENABLE --
        m_autonomousEnabled = true;
    }

    @Override
    public void loop() {
        //This is called every loop of the autonomous.
        Subsystems.periodic();
    }

    @Override
    public void stop() {
        // -- DISABLE --
        m_autonomousEnabled = false;

        Subsystems.onDisable();
    }
}

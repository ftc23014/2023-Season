package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.lib.simulation.Simulation;
import org.firstinspires.ftc.teamcode.autonomous.Autonomous;

public class Simulate {
    /**
     * This can be used to simulate the robot.
     * To run this, click on the play button to the left of the main method.
     * Then, click on "Run 'Simulate.main()' with Coverage".
     * This will start the simulation - you can then use the web interface to control the robot.
     */
    public static void main(String[] args) {
        // Start the simulation
        Simulation.start(
                new TeleOp(),
                new Autonomous(Autonomous.AutonomousMode.PICKUP_AUTONOMOUS)
        );
    }
}
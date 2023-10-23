package org.firstinspires.ftc.teamcode.subsystems.mechanisms;

import org.firstinspires.ftc.lib.systems.Subsystem;

public class DualLinearSlide extends Subsystem {
    /***
     * TODO:
     * Use PID control to control the position of the linear slide.
     * Add in positions for the linear slide to go to (may want to implement that in a command though).
     *
     * note: probably the hardest subsystem. going to be a bit of a pain to tune.
     * 1. we'll know the initial position of the linear slide, so that's good for auto.
     * 2. we need to finish auto with the slides retracted, or else we'll have a problem/buggy behavior during teleop.
     * 3. needs to have tuned PID values, and we need to make sure that the PID values are good for both extending and retracting.
     * 4. figure out accurate positions, and make sure that they are consistent.
     */
}

package org.firstinspires.ftc.teamcode.DeprecatedOrTrash;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.LED;
import org.firstinspires.ftc.lib.systems.Subsystem;

public class LEDSubsystem extends Subsystem {
    private final int numberOfLEDs = 0;

    private DigitalChannel[] leds;

    @Override
    public void init() {
        leds = new DigitalChannel[numberOfLEDs * 2];
        for (int i = 0; i < numberOfLEDs; i++) {
            leds[i] = getHardwareMap().get(DigitalChannel.class, "led" + i + "green");
            leds[i + 1] = getHardwareMap().get(DigitalChannel.class, "led" + i + "red");

            leds[i].setMode(DigitalChannel.Mode.OUTPUT);
            leds[i + 1].setMode(DigitalChannel.Mode.OUTPUT);
        }
    }

    public void setLED(int index, boolean red, boolean green) {
        if (index * 2 > leds.length) return;

        leds[index * 2].setState(green);
        leds[index * 2 + 1].setState(red);
    }

    @Override
    public void periodic() {

    }

    @Override
    public void onDisable() {

    }
}

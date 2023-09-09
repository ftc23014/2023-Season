package org.firstinspires.ftc.teamcode.subsystems;

import org.firstinspires.ftc.lib.replay.Replay;
import org.firstinspires.ftc.lib.replay.log.Log;
import org.firstinspires.ftc.lib.systems.Subsystem;

public class ExampleSubsystem extends Subsystem {
    public ExampleSubsystem() {
        
    }

    @Log
    private int exampleInt = 0;

    private int exampleInt2 = 0;

    @Log(link="setExampleInt2")
    private int getExampleInt2() {
        return exampleInt2;
    }

    @Replay(name="setExampleInt2")
    private void setExampleInt2(int exampleInt2) {
        this.exampleInt2 = exampleInt2;
    }

    @Override
    public void init() {

    }

    @Override
    public void periodic() {
        if (!this.replaying()) {
            exampleInt++;
            exampleInt2++;
        } else {
            System.out.println("in replay!");
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void replayInit() {
        exampleInt = 0;
        exampleInt2 = 0;
        System.out.println("Replay init!");
    }

    @Override
    public void exitReplay() {
        System.out.println("Replay exit!");
        System.out.println("Final exampleInt: " + exampleInt);
        System.out.println("Final exampleInt2: " + exampleInt2);
    }

    @Override
    public String getBaseName() {
        return "ExampleSubsystem";
    }
}

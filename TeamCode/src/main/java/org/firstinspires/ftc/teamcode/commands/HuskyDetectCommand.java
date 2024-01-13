package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.lib.Lambda;
import org.firstinspires.ftc.lib.LambdaFromInt;
import org.firstinspires.ftc.lib.systems.commands.Command;
import org.firstinspires.ftc.teamcode.subsystems.SensorConeHuskyLensSubsystem;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HuskyDetectCommand extends Command {
    private LambdaFromInt m_onFinish = null;

    private final long m_detectionTime;
    private SensorConeHuskyLensSubsystem m_huskyLensDetect;

    private int m_leftCount = 0;
    private int m_middleCount = 0;
    private int m_rightCount = 0;

    private Timer m_timer = new Timer();

    private boolean hasFinished = false;

    public HuskyDetectCommand(SensorConeHuskyLensSubsystem huskyLensDetect, double detectionTimeSeconds, LambdaFromInt onFinish) {
        super();

        m_huskyLensDetect = huskyLensDetect;
        m_detectionTime = (long) Math.round(detectionTimeSeconds * 1000);
        m_onFinish = onFinish;
    }

    @Override
    public void init() {
        TimerTask ttask = new TimerTask() {
            @Override
            public void run() {
                hasFinished = true;

                int mostDetections = Math.max(m_leftCount, Math.max(m_middleCount, m_rightCount));
                int detectedTheMost = 0;

                if (mostDetections == m_leftCount) {
                    detectedTheMost = -1;
                } else if (mostDetections == m_middleCount) {
                    detectedTheMost = 0;
                } else {
                    detectedTheMost = 1;
                }

                Random random1 = new Random();
                detectedTheMost = random1.nextInt(3) - 1;
                if (mostDetections == 0) {
                    detectedTheMost = -2;
                }

                m_huskyLensDetect.setDetecting(false);

                m_onFinish.run(
                        detectedTheMost
                );

                m_timer.cancel();
            }
        };

        m_timer.schedule(ttask, m_detectionTime);

        m_huskyLensDetect.setDetecting(true);
    }

    public void execute() {
        if (m_huskyLensDetect.hasDetected()) {
            if (m_huskyLensDetect.getLastDetection() == -1) {
                m_leftCount++;
            } else if (m_huskyLensDetect.getLastDetection() == 0) {
                m_middleCount++;
            } else {
                m_rightCount++;
            }
        }
    }

    @Override
    public boolean hasFinished() {
        return hasFinished;
    }
}

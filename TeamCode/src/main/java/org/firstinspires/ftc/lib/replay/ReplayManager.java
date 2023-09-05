package org.firstinspires.ftc.lib.replay;

import org.firstinspires.ftc.lib.replay.log.LogDataThread;
import org.firstinspires.ftc.lib.replay.log.ReplayLog;
import org.firstinspires.ftc.lib.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ReplayManager {
    private static ReplayManager instance;

    public static ReplayManager getInstance() {
        if (instance == null) {
            instance = new ReplayManager();
        }
        return instance;
    }

    public static void register(Replayable replayable) {
        getInstance().replayables.add(replayable);
    }

    private ArrayList<Replayable> replayables = new ArrayList<>();

    private int replayCycle = 0;

    private ReplayManager() {
        replayables = new ArrayList<>();
    }

    Timer replayTimer;

    public void enterReplay(File file) {
        if (!file.exists()) {
            throw new RuntimeException("Log file does not exist!");
        }

        //check file extension
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!extension.equals("log")) {
            throw new RuntimeException("Log file is not a log file!");
        }

        ReplayLog replayLog = new ReplayLog(FileUtils.read(file));

        for (Replayable replayable : replayables) {
            replayable.replayInit();
        }

        replayCycle = 0;

        final int totalReplayCycles = replayLog.getCycles();

        replayTimer = new Timer();

        TimerTask replayFrame = new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName("Replay Cycle " + replayCycle);

                if (replayCycle >= totalReplayCycles) {
                    exitReplay();
                    return;
                }

                HashMap<String, LogDataThread> data = replayLog.getThreadsMap();

                for (String key : data.keySet()) {
                    LogDataThread thread = data.get(key);

                    String bname = thread.getBaseName();

                    for (Replayable replayable : replayables) {
                        if (replayable.getBaseName().equals(bname)) {
                            replayable.feedThread(thread, replayCycle);
                        }
                    }
                }

                replayCycle++;
            }
        };

        replayTimer.scheduleAtFixedRate(replayFrame, Logger.getLogInterval(), Logger.getLogInterval());
    }

    public void exitReplay() {
        for (Replayable replayable : replayables) {
            replayable.exitReplay();
        }

        replayTimer.cancel();
        replayCycle = 0;
    }
}

package org.firstinspires.ftc.lib.replay;

import org.firstinspires.ftc.lib.replay.log.*;
import org.firstinspires.ftc.lib.replay.log.writers.LogWriter;
import org.firstinspires.ftc.lib.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReplayManager {

    //In milliseconds
    private static int logInterval = 100;

    private static int cycle = 0;

    private static LogWriter writer;
    private static HashMap<String, Integer> nameAssignedToMap = new HashMap<>();
    private static HashMap<Integer, String> lastAssign = new HashMap<>();

    private static int nameAssignedTo = 0;

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

    public static void log() {
        writer.saveInfo("ALC" + cycle++ + (cycle < 3 ? "(s)" : ""));

        if (cycle < 3) return;

        try {
            for (Replayable klass : getInstance().replayables) {
                Field[] fields = klass.getClass().getDeclaredFields();
                Method[] methods = klass.getClass().getDeclaredMethods();

                boolean movedToMethods = false;

                for (int i = 0; i < fields.length + methods.length; i++) {
                    Log logAnnotation;
                    Method method = null;
                    Field field = null;

                    if (i >= fields.length) {
                        if (!movedToMethods) {
                            movedToMethods = true;
                        }
                    }

                    boolean fieldWasAccessible = false;

                    if (!movedToMethods) {
                        logAnnotation = fields[i].getAnnotation(Log.class);
                        field = fields[i];

                        fieldWasAccessible = field.isAccessible();
                        field.setAccessible(true);
                    } else {
                        logAnnotation = methods[i - fields.length].getAnnotation(Log.class);
                        method = methods[i - fields.length];
                    }

                    if (logAnnotation == null) continue;
                    if (method == null && movedToMethods) continue;
                    if (field == null && !movedToMethods) continue;

                    String name = logAnnotation.name();

                    if (name.isEmpty()) {
                        if (movedToMethods) {
                            name = method.getName();
                        } else {
                            name = field.getName();
                        }
                    }

                    //if (!movedToMethods) System.out.println("name: " + name + ", " + field.getName());

                    LogType logType = LogType.fromClass(movedToMethods ? method.getReturnType() : field.getType());

                    if (name.contains("/")) {
                        //replace all slashes with underscores, just in case.
                        name = name.replaceAll("/", "|");
                    }

                    if (logAnnotation != null) {
                        if (nameAssignedToMap.get(name) == null) {
                            nameAssignedToMap.put(name, nameAssignedTo++);
                            writer.saveInfo("a" + klass.getBaseName() + "|" + name + logType.smallString() + "/" + nameAssignedToMap.get(name));
                        }

                        int assignedTo = nameAssignedToMap.get(name);
                        String assignable = "";

                        try {
                            switch (logType) {
                                case STRING:
                                    assignable = movedToMethods ? (String) method.invoke(klass).toString() : (String) field.get(klass).toString();
                                    break;
                                case INT:
                                    assignable = movedToMethods ? "" + (Integer) method.invoke(klass) : "" + (Integer) field.get(klass);
                                    break;
                                case DOUBLE:
                                    assignable = movedToMethods ? "" + (Double) method.invoke(klass) : "" + (Double) field.get(klass);
                                    break;
                                case BOOLEAN:
                                    assignable = movedToMethods ? "" + (Boolean) method.invoke(klass) : "" + (Boolean) field.get(klass);
                                    break;
                                case STRING_ARRAY:
                                    //save as string, separated by commas
                                    assignable = movedToMethods ? Arrays.toString((String[]) method.invoke(klass)) : Arrays.toString((String[]) field.get(klass));
                                    break;
                                case DOUBLE_ARRAY:
                                    double[] doubles = movedToMethods ? (double[]) method.invoke(klass) : (double[]) field.get(klass);

                                    //Convert to a string
                                    String doubleString = "[";

                                    for (double d : doubles) {
                                        doubleString += d + ",";
                                    }

                                    doubleString = doubleString.substring(0, doubleString.length() - 1) + "]";

                                    assignable = doubleString;
                                    break;
                                case BYTE_ARRAY:
                                    byte[] bytes = movedToMethods ? (byte[]) method.invoke(klass) : (byte[]) field.get(klass);

                                    //Convert to a string
                                    String byteString = "";

                                    for (byte b : bytes) {
                                        byteString += (char) b;
                                    }

                                    assignable = byteString;
                                    break;
                                case UNKNOWN:
                                    throw new Exception("Unknown log type! Please only use String, int, double, boolean, String[], double[], or byte[], or encode your information into a String!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (assignable.length() > 0) {
                            if (lastAssign.get(assignedTo) != null && lastAssign.get(assignedTo).equals(assignable)) continue;

                            writer.saveInfo("d" + assignedTo + "/" + assignable);

                            lastAssign.put(assignedTo, assignable);
                        }
                    }

                    if (field != null) field.setAccessible(fieldWasAccessible);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    String base_name = thread.getBaseName();

                    for (Replayable replayable : replayables) {
                        if (replayable.getBaseName().equals(base_name)) {
                            replayable.feedThread(thread, replayCycle);
                        }
                    }
                }

                replayCycle++;
            }
        };

        replayTimer.scheduleAtFixedRate(replayFrame, logInterval, logInterval);
    }

    public void exitReplay() {
        for (Replayable replayable : replayables) {
            replayable.exitReplay();
        }

        replayTimer.cancel();
        replayCycle = 0;
    }
}

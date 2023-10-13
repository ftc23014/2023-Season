package org.firstinspires.ftc.lib.replay.log.writers;

import org.firstinspires.ftc.lib.simulation.Simulation;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FileWriter extends LogWriter {
    private String[] args;

    public FileWriter() {
        this.args = new String[] { "default", "Europe/Amsterdam" };
    }

    public FileWriter(String ...args) {
        if (args.length != 2) throw new RuntimeException("Invalid arguments for FileWriter! [directory-name, timezone]");
        this.args = args;
    }

    private static final String extension = "log";

    public static String getExtension() {
        return extension;
    }

    private String filePath;

    private boolean initializedFile = false;

    @Override
    public void initialize() {
        filePath = args[0];

        if (args[0].equals("default")) {
            String homeFolderPath = System.getProperty("user.home") + (!Simulation.inSimulation() ? "/sdcard" : "") + "/ftcteamlogs";
            File homeFolder = new File(homeFolderPath);

            if (!homeFolder.exists()) {
                homeFolder.mkdir();
            }

            Calendar cal = Calendar.getInstance();
            //Set timezone to Amsterdam
            cal.setTimeZone(TimeZone.getTimeZone(args[1]));
            cal.setTime(new Date());

            filePath = homeFolderPath + "/" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.HOUR) + "-" + cal.get(Calendar.SECOND) + "." + extension;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        //Append string to file
        String str = "Successfully generated log file at " + new Date().getTime() + ".\n";
        try {
            java.io.FileWriter fw = new java.io.FileWriter(file, true);
            fw.write(str);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializedFile = true;
    }

    @Override
    public void saveLine(String line) {
        if (!initializedFile) return;

        //Append line to file
        try {
            java.io.FileWriter fw = new java.io.FileWriter(filePath, true);
            fw.write(line + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveInfo(String encodedInfo) {
        if (!initializedFile) return;

        //Append line to file
        try {
            if (encodedInfo == null) return;

            java.io.FileWriter fw = new java.io.FileWriter(filePath, true);
            fw.write(encodedInfo + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        //Append string to file
        String str = "Successfully closed log file at " + new Date().getTime() + "";
        try {
            java.io.FileWriter fw = new java.io.FileWriter(filePath, true);
            fw.write(str);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

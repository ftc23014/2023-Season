package org.firstinspires.ftc.lib.replay.log;

import org.firstinspires.ftc.lib.replay.ReplayManager;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

public class SysoutMiddleman extends OutputStream {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintStream original;

    public SysoutMiddleman(PrintStream original) {
        this.original = original;
    }

    @Override
    public void write(int i) {
        buffer.write(i);
        original.write(i);

        if (i == '\n') {
            try {
                String s = get();

                Calendar cal = Calendar.getInstance();
                //Set timezone to Amsterdam
                cal.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Amsterdam"));
                cal.setTime(new Date());

                String time = "[" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "] ";

                ReplayManager.getWriter().saveLine(time + s);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public String get() throws UnsupportedEncodingException {
        return buffer.toString("UTF-8");
    }
}


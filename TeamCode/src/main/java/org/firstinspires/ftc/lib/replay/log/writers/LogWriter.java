package org.firstinspires.ftc.lib.replay.log.writers;

public abstract class LogWriter {
    private String[] args;

    public LogWriter(String ...args) {
        this.args = args;
    };

    public abstract void initialize();

    public abstract void saveLine(String line);

    public abstract void saveInfo(String encodedInfo);

    public abstract void close();

    public String[] getArgs() {
        return args;
    }
}

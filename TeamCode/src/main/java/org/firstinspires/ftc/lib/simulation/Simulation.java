package org.firstinspires.ftc.lib.simulation;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.lib.server.Server;

public class Simulation {
    public enum GameMode {
        DISABLED,
        AUTONOMOUS,
        TELEOP;
    }

    private static Simulation instance;

    public static boolean inSimulation() {
        return instance != null;
    }

    public static Simulation getInstance() {
        if (instance == null) {
            throw new RuntimeException("Simulation wasn't started!");
        }

        return instance;
    }

    private static OpMode k_autonomous;
    private static OpMode k_teleop;

    public static void start(OpMode autonomous, OpMode teleop) {
        if (instance != null) {
            throw new RuntimeException("Simulation was already started!");
        }

        k_autonomous = autonomous;
        k_teleop = teleop;

        instance = new Simulation();
    }

    private GameMode m_gameMode;

    private Simulation() {
        m_gameMode = GameMode.DISABLED;

        new Server();

        Server.addRoute(new SimulationAPI());

        System.out.println("[Simulation] Started!");

        k_teleop.init();
        k_autonomous.init();

        Thread thread = new Thread(() -> {
            while (true) {
                if (m_gameMode == GameMode.AUTONOMOUS) {
                    k_autonomous.loop();
                } else if (m_gameMode == GameMode.TELEOP) {
                    k_teleop.loop();
                }
            }
        });

        thread.start();
    }

    public GameMode getGameMode() {
        return m_gameMode;
    }

    protected void gameModeChange(GameMode mode) {
        switch (mode) {
            case AUTONOMOUS:
                if (m_gameMode == GameMode.TELEOP) {
                    k_teleop.stop();
                }
                k_autonomous.start();
                break;
            case TELEOP:
                if (m_gameMode == GameMode.AUTONOMOUS) {
                    k_autonomous.stop();
                }
                k_teleop.start();
                break;
            case DISABLED:
                if (m_gameMode == GameMode.AUTONOMOUS) {
                    k_autonomous.stop();
                } else if (m_gameMode == GameMode.TELEOP) {
                    k_teleop.stop();
                }
                break;
        }

        System.out.println("[Simulation] Game mode changed to " + mode.toString() + ".");

        m_gameMode = mode;
    }
}

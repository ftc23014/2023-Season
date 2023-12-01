package org.firstinspires.ftc.lib.systems.commands;

import org.firstinspires.ftc.lib.BooleanLambda;

public class IfOrSkipCommand extends Command {
    private Command m_command;

    private BooleanLambda m_condition;

    private boolean m_doSkip = false;

    public IfOrSkipCommand(BooleanLambda condition, Command command) {
        m_command = command;
        m_condition = condition;
    }

    @Override
    public void init() {
        m_doSkip = !m_condition.run();

        if (!m_doSkip) {
            m_command.init();
        }
    }

    @Override
    public void execute() {
        if (!m_doSkip) {
            m_command.execute();
        }
    }

    @Override
    public boolean hasFinished() {
        return m_doSkip || m_command.hasFinished();
    }
}

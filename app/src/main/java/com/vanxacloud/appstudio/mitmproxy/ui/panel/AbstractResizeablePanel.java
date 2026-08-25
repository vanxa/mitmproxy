package com.vanxacloud.appstudio.mitmproxy.ui.panel;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Ivan Konstantinov <idkonst@protonmail.com>
 */
public abstract class AbstractResizeablePanel extends Panel implements ResizeablePanel {

    private final Terminal terminal;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public AbstractResizeablePanel(Terminal terminal) {
        this.terminal = terminal;
    }

    public TerminalSize getTerminalSize() {
        try {
            return terminal.getTerminalSize();
        } catch (IOException e) {
            log.error("Could not get terminal size due to exception. Will return {0,0}");
            return new TerminalSize(0, 0);
        }
    }

    public Terminal getTerminal() {
        return terminal;
    }
}

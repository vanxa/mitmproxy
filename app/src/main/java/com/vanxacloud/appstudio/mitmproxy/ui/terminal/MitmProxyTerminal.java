package com.vanxacloud.appstudio.mitmproxy.ui.terminal;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.vanxacloud.appstudio.mitmproxy.ui.flow.FlowHandler;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.ResizeablePanel;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.flow.FlowPanel;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.footer.ActionPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class MitmProxyTerminal implements FlowHandler {
    public static final Logger log = LoggerFactory.getLogger("console");

    private final Terminal terminal;
    private final TerminalScreen screen;
    private final Panel mainPanel;
    private final FlowPanel flowPanel;
    private final ActionPanel commandPanel;

    public MitmProxyTerminal() throws IOException {
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        this.mainPanel = new Panel();
        this.flowPanel = new FlowPanel();
        this.commandPanel = new ActionPanel(flowPanel);


    }
    private void startScreen() throws IOException {
        log.info("Starting console");
        screen.startScreen();
        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());

        mainPanel.setFillColorOverride(null);
        TerminalSize terminalSize = terminal.getTerminalSize();


        mainPanel.addComponent(flowPanel);


        mainPanel.addComponent(commandPanel);

        terminal.addResizeListener((terminal1, newSize) -> {
            // Update size variables or trigger redraw
            resizePanels(newSize);
        });


        resizePanels(terminalSize);

        window.setComponent(mainPanel);


        gui.addWindowAndWait(window);
    }

    private void refreshScreen() {
        try {
            screen.refresh();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void resizePanels(TerminalSize terminalSize) {
        log.trace("Terminal size changed to {}, resizing panels", terminalSize);
        mainPanel.setPreferredSize(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - 3));
        mainPanel.getChildren().forEach(child -> {
            if (child instanceof ResizeablePanel resizeablePanel) {
                resizeablePanel.resize(terminalSize);
            }
        });

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Setup terminal and screen layers
        new MitmProxyTerminal().startScreen();
    }

    @Override
    public void addFlow(Object flow) {
        flowPanel.addFlow(flow);
        refreshScreen();
    }
}


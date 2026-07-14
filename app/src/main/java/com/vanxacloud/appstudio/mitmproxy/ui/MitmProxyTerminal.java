package com.vanxacloud.appstudio.mitmproxy.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class MitmProxyTerminal {
    public static final Logger log = LoggerFactory.getLogger("console");

    private final Terminal terminal;
    private final TerminalScreen screen;
    private final Panel mainPanel;
    private final Panel commandPanel;
    private final Panel actionPanel;
    private final ActionListBox textBox;
    private final Label flowPanelTextBox;
    private final Label actionPanelTextBox;

    public MitmProxyTerminal() throws IOException {
        this.terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        this.mainPanel = new Panel();
        this.actionPanel = new Panel();
        this.commandPanel = new Panel();

        this.flowPanelTextBox = new Label("Flows");
        flowPanelTextBox.setBackgroundColor(new TextColor.RGB(171, 148, 148));

        this.actionPanelTextBox = new Label("[0/0]");
        actionPanelTextBox.setBackgroundColor(new TextColor.RGB(171, 148, 148));

        this.textBox = new ActionListBox();
    }

    private void startScreen() throws IOException {
        log.info("Starting console");
        screen.startScreen();
        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());

        Panel flowLabelPanel = new Panel();
        flowLabelPanel.setFillColorOverride(new TextColor.RGB(171, 148, 148));
        flowLabelPanel.addComponent(flowPanelTextBox);


        mainPanel.setFillColorOverride(null);
        mainPanel.addComponent(flowLabelPanel);
        TerminalSize terminalSize = terminal.getTerminalSize();

        // Add items vertically
        textBox.addItem("Item 1", () -> log.info("Selected Item 1"));
        textBox.addItem("Item 2", () -> log.info("Selected Item 2"));
        textBox.addItem("Item 3", () -> log.info("Selected Item 3"));
        actionPanel.addComponent(textBox);

        mainPanel.addComponent(actionPanel);

        terminal.addResizeListener((terminal1, newSize) -> {
            // Update size variables or trigger redraw
            resizePanels(newSize);
        });


        resizePanels(terminalSize);


        Panel actionLabelPanel = new Panel();
        actionLabelPanel.setFillColorOverride(new TextColor.RGB(171, 148, 148));
        actionLabelPanel.addComponent(actionPanelTextBox);

        mainPanel.addComponent(actionLabelPanel);

        mainPanel.addComponent(commandPanel);

        window.setComponent(mainPanel);


        gui.addWindowAndWait(window);
    }

    private void resizePanels(TerminalSize terminalSize) {
        log.trace("Terminal size changed to {}, resizing panels", terminalSize);
        mainPanel.setPreferredSize(new TerminalSize(terminalSize.getColumns(), terminalSize.getRows() - 2));
        textBox.setPreferredSize(mainPanel.getPreferredSize());
        flowPanelTextBox.setPreferredSize(new TerminalSize(terminalSize.getColumns(), 1));
        actionPanelTextBox.setPreferredSize(new TerminalSize(terminalSize.getColumns(), 1));
        commandPanel.setPreferredSize(new TerminalSize(terminalSize.getColumns(), 2));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Setup terminal and screen layers
        new MitmProxyTerminal().startScreen();

    }
}


package com.vanxacloud.appstudio.mitmproxy.ui.panel.footer;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.terminal.Terminal;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.AbstractResizeablePanel;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.MITMPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Creates and manages the Footer panel where actions, both contextual and non-contextual are displayed and handled
 * The panel consists of three elements - the header, the context menu and the proxy menu
 * <p>
 * The header is a simple label that shows the selected interception flow, out of the total number of flows, in the format "[s/t]"
 * <p>
 * The context menu will show actions that can be performed on the selected flow. If no items are loaded (or have been deleted),
 * different actions based on the current context will be shown.
 * <p>
 * The proxy menu contains the following fixed action items:
 * <ul>
 * <li>Show the usage menu (help)</li>
 * <li>Quit the mitmproxy app</li>
 * <li>Show the events window</li>
 * <li>Show and configure options</li>
 * <li>Show, edit and delete interception rules and filters</li>
 * <li>Filter the currently displayed items</li>
 * <li>Save all currently displayed flows</li>
 * <li>Clear the list of items</li>
 * <li>Switch layout views</li>
 * <li>and more...</li>
 * </ul>
 * <p>
 *
 * @author Ivan Konstantinov <idkonst@protonmail.com>
 */
public class ActionPanel extends AbstractResizeablePanel {

    public static final Logger log = LoggerFactory.getLogger("action-panel");
    private final Label label;
    private final MITMPanel mainPanel;
    private final Panel actionLabelPanel;
    private final ProxyCommandRow commandRow;


    public ActionPanel(Terminal terminal, MITMPanel mainPanel) {
        super(terminal);
        this.mainPanel = mainPanel;
        this.actionLabelPanel = new Panel();
        this.label = new Label(String.format("[%d/%d]", this.mainPanel.getSelectedFlowIndex(), this.mainPanel.getNumberOfFlows()));
        this.label.setBackgroundColor(new TextColor.RGB(171, 148, 148));


        actionLabelPanel.setFillColorOverride(new TextColor.RGB(171, 148, 148));
        actionLabelPanel.addComponent(label);
        addComponent(actionLabelPanel);


        commandRow = new ProxyCommandRow(terminal);
        addComponent(commandRow);
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        log.trace("Terminal size changed to {}, resizing panel", terminalSize);
        setPreferredSize(new TerminalSize(terminalSize.getColumns(), 3));
        this.label.setPreferredSize(new TerminalSize(terminalSize.getColumns(), 1));
    }
}

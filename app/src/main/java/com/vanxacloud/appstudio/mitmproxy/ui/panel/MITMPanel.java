package com.vanxacloud.appstudio.mitmproxy.ui.panel;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Container;
import com.googlecode.lanterna.terminal.Terminal;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.flow.FlowPanel;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.footer.ActionPanel;

import java.io.IOException;

/**
 * @author Ivan Konstantinov <idkonst@protonmail.com>
 */
public class MITMPanel extends AbstractResizeablePanel {


    private FlowPanel flowPanel;
    private ActionPanel actionPanel;

    public MITMPanel(Terminal terminal) throws IOException {
        super(terminal);
        setFillColorOverride(null);
        initPanels();
    }

    public void initPanels() {
        this.flowPanel = new FlowPanel(getTerminal());
        this.actionPanel = new ActionPanel(getTerminal(), this);

        addComponent(flowPanel);
        addComponent(actionPanel);
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int rows = Math.max(terminalSize.getRows(), 3);
        log.debug("Setting panel size to ({},{})", terminalSize.getColumns(), rows - 3);
        setPreferredSize(new TerminalSize(terminalSize.getColumns(), rows - 3));
        log.debug("Resizing this panel's children");
        resizeChildren(this, terminalSize);
        log.debug("Resizing complete");
    }

    private void resizeChildren(Component panel, TerminalSize terminalSize) {
        if (panel instanceof Container containerChild && containerChild.getChildCount() > 0) {
            containerChild.getChildren().forEach(child -> {
                if (child instanceof ResizeablePanel resizeablePanel) {
                    log.debug("Resizing {}", resizeablePanel.getClass().getSimpleName());
                    resizeablePanel.resize(terminalSize);
                }
                resizeChildren(child, terminalSize);
            });
        }
    }

    public void addFlow(Object flow) {
        log.debug("Adding flow {}", flow);
        flowPanel.addFlow(flow);
    }

    public int getNumberOfFlows() {
        return flowPanel.getNumberOfItems();
    }

    public int getSelectedFlowIndex() {
        return flowPanel.getSelectedItemIndex();
    }
}

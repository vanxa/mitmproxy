package com.vanxacloud.appstudio.mitmproxy.ui.panel.flow;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.ResizeablePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowPanel extends Panel implements ResizeablePanel {

    private static final Logger log = LoggerFactory.getLogger(FlowPanel.class);

    private final Label label;
    private final ActionListBox flows;
    private final Panel labelPanel;

    public FlowPanel() {
        super();
        this.label = new Label("Flows");
        this.label.setBackgroundColor(new TextColor.RGB(171, 148, 148));
        this.flows = new ActionListBox();

        this.labelPanel = new Panel();
        labelPanel.setFillColorOverride(new TextColor.RGB(171, 148, 148));
        labelPanel.addComponent(label);
        addComponent(labelPanel);

        // Add items vertically
        addFlow("Item 1");
        addFlow("Item 2");

//        flows.addItem("Item 1", () -> log.info("Selected Item 1"));
//        flows.addItem("Item 2", () -> log.info("Selected Item 2"));
//        flows.addItem("Item 3", () -> flows.addItem("IT", () -> log.info("Selected Item 3")));
        addComponent(flows);

    }

    @Override
    public void resize(TerminalSize terminalSize) {
        setPreferredSize(getParent().getPreferredSize());
        flows.setPreferredSize(getPreferredSize());
        label.setPreferredSize(new TerminalSize(terminalSize.getColumns(), 1));
    }

    public int getSelectedItemIndex() {
        return flows.getSelectedIndex();
    }

    public int getNumberOfItems() {
        return flows.getItemCount();
    }

    public void addFlow(Object flow) {
        flows.addItem(flow.toString(), () -> log.info("Selectd flow {}", flow));
    }
}

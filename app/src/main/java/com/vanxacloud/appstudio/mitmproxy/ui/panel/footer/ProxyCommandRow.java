package com.vanxacloud.appstudio.mitmproxy.ui.panel.footer;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbsoluteLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LayoutManager;
import com.googlecode.lanterna.terminal.Terminal;
import com.vanxacloud.appstudio.mitmproxy.ui.panel.AbstractResizeablePanel;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Creates and manages the Proxy command panel row in the footer section.
 * The Proxy command panel row is responsible for handling the following actions:
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
 * This class also manages the changes to state of the menu items based on the current context.
 * For example, selecting the Options menu item will change the Quit menu item to Back (without changing the shortcut)
 *
 * @author Ivan Konstantinov <idkonst@protonmail.com>
 */
public class ProxyCommandRow extends AbstractResizeablePanel {

    private static final List<Label> COMMAND_ROW_ITEMS = Arrays.asList(
            new Label("Proxy:"),
            new Label("? Help"),
            new Label("q Quit"),
            new Label("E Events"),
            new Label("O Options"),
            new Label("i Intercept"),
            new Label("f Filter"),
            new Label("w Save flows"),
            new Label("z Clear list"),
            new Label("- Layout"),
            new Label("ctrl -> Switch"),
            new Label("F Follow new"));
    private final LayoutManager layout;

    public ProxyCommandRow(Terminal terminal) {
        super(terminal);
        TerminalSize terminalSize = getTerminalSize();
        this.layout = new AbsoluteLayout();
        setLayoutManager(layout);
        int currentColumn = 0;
        for (Label label : COMMAND_ROW_ITEMS) {
            label.setPosition(new TerminalPosition(currentColumn, 0));
            label.setSize(new TerminalSize(label.getText().length() + 1, 1));
            currentColumn += label.getText().length() + 1;
            addComponent(label);
        }

    }


    @Override
    public void resize(TerminalSize terminalSize) {
        final int width = terminalSize.getColumns();
        log.debug("Resizing command row panel to ({},1)", width);
        setPreferredSize(new TerminalSize(width, 1));
        COMMAND_ROW_ITEMS.forEach(label -> {
            label.setVisible(width >= label.getPosition().getColumn() + label.getText().length());
            if (log.isDebugEnabled()) {
                String action = label.isVisible() ? "Showing" : "Hiding";
                log.debug("{} label [{}] at {}", action, label.getText(), label.getPosition());
            }
        });
    }
}

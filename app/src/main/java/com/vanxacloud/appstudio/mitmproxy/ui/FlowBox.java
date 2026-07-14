package com.vanxacloud.appstudio.mitmproxy.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.TextBox;

public class FlowBox extends TextBox {

    public FlowBox(String initialContent) {
        super(initialContent);
    }

    public FlowBox(String initialContent, Style style) {
        super(initialContent, style);
    }

    public FlowBox(TerminalSize preferredSize) {
        super(preferredSize);
    }

    public FlowBox(TerminalSize preferredSize, Style style) {
        super(preferredSize, style);
    }

    public FlowBox(TerminalSize preferredSize, String initialContent) {
        super(preferredSize, initialContent);
    }

    public FlowBox(TerminalSize preferredSize, String initialContent, Style style) {
        super(preferredSize, initialContent, style);
    }
}

package com.vanxacloud.appstudio.mitmproxy.ui.panel.flow;

import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;

/**
 * @author Ivan Konstantinov (idkonst@protonmail.com)
 */
public class FlowListBox extends ActionListBox {

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        if (isKeyboardActivationStroke(keyStroke)) {
            runSelectedItem();
            return Result.HANDLED;
        } else if (keyStroke.getKeyType() == KeyType.MouseEvent) {
            MouseAction mouseAction = (MouseAction) keyStroke;
            MouseActionType actionType = mouseAction.getActionType();

            if (isMouseMove(keyStroke)
                    || actionType == MouseActionType.CLICK_RELEASE
                    || actionType == MouseActionType.SCROLL_UP
                    || actionType == MouseActionType.SCROLL_DOWN) {
                return super.handleKeyStroke(keyStroke);
            }

            // includes mouse drag
            int existingIndex = getSelectedIndex();
            int newIndex = getIndexByMouseAction(mouseAction);
            if (existingIndex != newIndex || !isFocused() || actionType == MouseActionType.CLICK_DOWN) {
                // the index has changed, or the focus needs to be obtained, or the user is clicking on the current selection to perform the action again
                Result result = super.handleKeyStroke(keyStroke);
                runSelectedItem();
                return result;
            }
            return Result.HANDLED;
        } else {
            Result result = super.handleKeyStroke(keyStroke);
            //runSelectedItem();
            return result;
        }
    }
}

package com.vanxacloud.appstudio.proximity.fx.validation.decoration;

import javafx.scene.Node;
import javafx.scene.control.Label;
import net.synedra.validatorfx.Decoration;
import net.synedra.validatorfx.ValidationMessage;

public class LabelDecorator implements Decoration {
    private final ValidationMessage message;


    public LabelDecorator(ValidationMessage message) {
        this.message = message;
    }

    @Override
    public void remove(Node target) {
        ((Label) target).setText("");
    }

    @Override
    public void add(Node target) {
        ((Label) target).setText(message.getText());
    }

}

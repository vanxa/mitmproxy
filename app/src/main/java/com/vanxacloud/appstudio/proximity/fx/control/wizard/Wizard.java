package com.vanxacloud.appstudio.proximity.fx.control.wizard;

import com.vanxacloud.appstudio.proximity.fx.control.wizard.page.ProjectPage;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.page.SettingsPage;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.pane.StartupWizardPane;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import org.controlsfx.dialog.WizardPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class Wizard {

    private static final Logger log = LoggerFactory.getLogger(Wizard.class);
    private final ApplicationContext ac;
    private org.controlsfx.dialog.Wizard wizard;

    Wizard(ApplicationContext ac) {
        this.ac = ac;
    }

    public Settings show() {
        this.wizard = initializeWizard();
        Optional<ButtonType> buttonTypeOptional = wizard.showAndWait();
        if (buttonTypeOptional.isEmpty()) {
            log.warn("Wizard not completed");
            return null;
        }
        ButtonType buttonType = buttonTypeOptional.get();
        if (buttonType.equals(ButtonType.CANCEL)) {
            log.info("Wizard cancelled");
        } else if (buttonType.equals(ButtonType.OK)) {
            log.info("Wizard completed");
        } else if (buttonType.equals(ButtonType.FINISH)) {
            return new Settings(wizard.getSettings());
        }
        return null;
    }


    org.controlsfx.dialog.Wizard initializeWizard() {
        try {
            org.controlsfx.dialog.Wizard wizard = new org.controlsfx.dialog.Wizard();

            List<WizardPane> pages = new ArrayList<>();
            wizard.getDialog().setHeight(1000);
            wizard.getDialog().setWidth(1000);
            wizard.getDialog().setResizable(true);
            pages.add(new StartupWizardPane("pages/wizard/projects.fxml", clazz -> ac.getBean(ProjectPage.class)));
            pages.add(new StartupWizardPane("pages/wizard/settings.fxml", clazz -> ac.getBean(SettingsPage.class)));
            wizard.setTitle("Wizard");
            wizard.setFlow(new org.controlsfx.dialog.Wizard.LinearFlow(pages));
            return wizard;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public org.controlsfx.dialog.Wizard getWizard() {
        return wizard;
    }

    public record Settings(ObservableMap<String, Object> settings) {
    }
}

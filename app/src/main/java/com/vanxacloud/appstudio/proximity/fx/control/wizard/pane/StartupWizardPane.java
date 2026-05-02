package com.vanxacloud.appstudio.proximity.fx.control.wizard.pane;

import com.vanxacloud.appstudio.proximity.GeneratedSkipCoverage;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.page.WizardDialogPage;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;

@GeneratedSkipCoverage
public class StartupWizardPane extends WizardPane {

    private final FXMLLoader loader;

    public StartupWizardPane(String resourcePath, Callback<Class<?>, Object> controllerCallback) {
        getStylesheets().add("/style.css");
        getStyleClass().remove("wizard-pane");
        try {
            URL res = ResourceUtils.getURL(String.format("classpath:%s", resourcePath));
            this.loader = new FXMLLoader(res);
            loader.setControllerFactory(controllerCallback);
            setContent(loader.load());
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create pane ", e);
        }
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        WizardDialogPage controller = loader.getController();
        wizard.invalidProperty().bind(controller.isValid().not());
        controller.changeState(wizard.getSettings());
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        wizard.invalidProperty().unbind();
        WizardDialogPage controller = loader.getController();
        controller.changeState(wizard.getSettings());
    }
}

package com.vanxacloud.appstudio.proximity.fx.control.wizard.page;

import com.vanxacloud.appstudio.proximity.fx.validation.decoration.LabelDecorator;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class SettingsPage extends WizardDialogPage {

    @FXML
    private RadioButton defaultSettingsRadioButton;
    @FXML
    private RadioButton projectSettingsRadioButton;

    /*
           ------------------------------------------------------------------------
           ------------------Load from configuration file ------------------------------------------
           ------------------File: --------------------------------------------
           ------------------Error label ------------------------------------------
            */
    @FXML
    private RadioButton existingSettingsFilePathRadioButton;

    @FXML
    private Button openProjectButton;

    @FXML
    private TextField existingSettingsFilePath;
    @FXML
    private Label existingSettingsFileErrorLabel;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            getValidator().createCheck()
                    .dependsOn("existingProject", existingSettingsFilePathRadioButton.selectedProperty())
                    .dependsOn("existingProjectFilePath", existingSettingsFilePath.textProperty())
                    .withMethod(context -> {
                        boolean isValid = true;
                        if (existingSettingsFilePathRadioButton.isSelected()) {
                            if (existingSettingsFilePath.getText().isEmpty()) {
                                isValid = false;
                            } else {
                                isValid = Paths.get(existingSettingsFilePath.getText()).toFile().exists();
                            }
                        }
                        if (!isValid) {
                            context.error("Choose a valid file");
                        }
                        setValid(isValid);
                    })
                    .decoratingWith(LabelDecorator::new)
                    .decorates(existingSettingsFileErrorLabel)
                    .immediate();
        });
    }

    @FXML
    public void handleOpenSettingsFile(ActionEvent event) {
        String title = "Load from configuration file";
        existingSettingsFilePath.setText(chooseFile((Button) event.getSource(), title));
    }


    @Override
    void stateChanged(MapChangeListener.Change<? extends String, ?> change) {
        Object existingProjectRadioButton = change.getMap().get("existingProjectRadioButton");
        if (existingProjectRadioButton instanceof Boolean b) {
            this.projectSettingsRadioButton.setDisable(!b);
        }
    }

    public RadioButton getExistingSettingsFilePathRadioButton() {
        return existingSettingsFilePathRadioButton;
    }

    public TextField getExistingSettingsFilePath() {
        return existingSettingsFilePath;
    }

    public Label getExistingSettingsFileErrorLabel() {
        return existingSettingsFileErrorLabel;
    }

    public RadioButton getDefaultSettingsRadioButton() {
        return defaultSettingsRadioButton;
    }

    public RadioButton getProjectSettingsRadioButton() {
        return projectSettingsRadioButton;
    }

    public Button getOpenProjectButton() {
        return openProjectButton;
    }
}

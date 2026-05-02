package com.vanxacloud.appstudio.proximity.fx.control.wizard.page;

import com.vanxacloud.appstudio.proximity.config.Constants;
import com.vanxacloud.appstudio.proximity.fx.validation.decoration.LabelDecorator;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ProjectPage extends WizardDialogPage {


    BooleanProperty initializedProjectName = new SimpleBooleanProperty(false);

    @FXML
    private RadioButton temporaryProjectRadioButton;


    /*
        ------------------------------------------------------------------------
        ------------------New Project ------------------------------------------
        ------------------Project Name: --------------------------------------------
        ------------------Project File: --------------------------------------------
        ------------------Error label ------------------------------------------
         */

    @FXML
    private RadioButton newProjectRadioButton;


    @FXML
    private TextField newProjectName;


    @FXML
    private TextField newProjectFilePath;

    @FXML
    private Label newProjectErrorLabel;


    /*
    ------------------------------------------------------------------------
    ------------------Open Project ------------------------------------------
    ------------------Filename: --------------------------------------------
    ------------------Error label ------------------------------------------
     */

    @FXML
    private RadioButton existingProjectRadioButton;

    @FXML
    private TextField existingProjectFilePath;

    @FXML
    private Label existingProjectErrorLabel;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            getValidator().createCheck()
                    .dependsOn("newProject", newProjectRadioButton.selectedProperty())
                    .dependsOn("initializedNewProjectName", initializedProjectName)
                    .dependsOn("newProjectText", newProjectName.textProperty())
                    .dependsOn("newProjectFilePath", newProjectFilePath.textProperty())
                    .withMethod(c -> {
                        if (newProjectRadioButton.isSelected()) {
                            if (newProjectFilePath.getText().isEmpty() || newProjectName.getText().isEmpty()) {
                                c.error("Invalid project name");
                                setValid(false);
                            } else {
                                setValid(true);
                            }
                        } else {
                            setValid(true);
                        }
                    })
                    .decoratingWith(LabelDecorator::new)
                    .decorates(newProjectErrorLabel)
                    .immediate();

            getValidator().createCheck()
                    .dependsOn("existingProject", existingProjectRadioButton.selectedProperty())
                    .dependsOn("existingProjectFilePath", existingProjectFilePath.textProperty())
                    .withMethod(context -> {
                        boolean isValid = true;
                        if (existingProjectRadioButton.isSelected()) {
                            if (existingProjectFilePath.getText().isEmpty()) {
                                isValid = false;
                            } else {
                                isValid = Paths.get(existingProjectFilePath.getText()).toFile().exists();
                            }
                        }
                        if (!isValid) {
                            context.error("Choose a valid file");
                        }
                        setValid(isValid);
                    })
                    .decoratingWith(LabelDecorator::new)
                    .decorates(existingProjectErrorLabel)
                    .immediate();
        });

    }

    @FXML
    public void handleOpenFile(ActionEvent event) {
        EventTarget target = event.getTarget();
        if (target instanceof Button button) {
            TextField targetField;
            String title;
            if ("newProjectButton".equals(button.getId())) {
                title = "New Project File";
                targetField = newProjectFilePath;
            } else if ("openProjectButton".equals(button.getId())) {
                title = "Open Project File";
                targetField = existingProjectFilePath;
            } else {
                throw new IllegalArgumentException(String.format("Invalid event for unknown button %s", button.getId()));
            }
            targetField.setText(chooseFile((Button) event.getSource(), title));
        }

    }


    public void setDefaultNewProject(MouseEvent mouseEvent) {
        if (newProjectFilePath.getText().isEmpty()) {
            String date = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.getDefault()).format(LocalDateTime.now());
            newProjectFilePath.setText(String.format("%s.%s", date, Constants.PROJECT_FILE_EXTENSION));
            newProjectName.setText(date);
            initializedProjectName.set(true);
        }

    }


    public RadioButton getTemporaryProjectRadioButton() {
        return temporaryProjectRadioButton;
    }

    public RadioButton getNewProjectRadioButton() {
        return newProjectRadioButton;
    }

    public TextField getNewProjectName() {
        return newProjectName;
    }

    public TextField getNewProjectFilePath() {
        return newProjectFilePath;
    }

    public Label getNewProjectErrorLabel() {
        return newProjectErrorLabel;
    }

    public RadioButton getExistingProjectRadioButton() {
        return existingProjectRadioButton;
    }

    public TextField getExistingProjectFilePath() {
        return existingProjectFilePath;
    }

    public Label getExistingProjectErrorLabel() {
        return existingProjectErrorLabel;
    }
}

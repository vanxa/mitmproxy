package com.vanxacloud.appstudio.proximity.fx.control.wizard.page;

import com.sun.javafx.binding.MapExpressionHelper;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.pane.StartupWizardPane;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import net.synedra.validatorfx.Severity;
import net.synedra.validatorfx.ValidationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith({ApplicationExtension.class, SpringExtension.class})
@SpringBootTest
class SettingsPageTest {


    @MockitoSpyBean
    @Autowired
    private SettingsPage settingsPage;


    @Start
    private void start(Stage stage) {
        StartupWizardPane pane = new StartupWizardPane("pages/wizard/settings.fxml", clazz -> settingsPage);
        Scene scene = new Scene(pane, 320, 240);
        stage.setTitle("settingsPage");
        stage.setScene(scene);
        scene.getStylesheets().add("/style.css");
        stage.show();

    }

    @Test
    void test_existingProjectRadioButtonSelected_settingsWithProjectEnabled(FxRobot robot) {
        MapExpressionHelper.SimpleChange<String, Boolean> change = new MapExpressionHelper.SimpleChange<>(FXCollections.observableMap(Collections.singletonMap("existingProjectRadioButton", false)));
        settingsPage.stateChanged(change);
        verifyThat(settingsPage.getExistingSettingsFilePathRadioButton(), radioButton -> !radioButton.isDisabled());
    }

    @Test
    void test_newProjectRadioButton_settingsWithProjectEnabled(FxRobot robot) {
        MapExpressionHelper.SimpleChange<String, Boolean> change = new MapExpressionHelper.SimpleChange<>(FXCollections.observableMap(Collections.singletonMap("newProjectRadioButton", false)));
        settingsPage.stateChanged(change);
        verifyThat(settingsPage.getExistingSettingsFilePathRadioButton(), radioButton -> !radioButton.isDisabled());
    }

    @Test
    void test_temporaryProjectRadioButtonSelected_settingsWithProjectEnabled(FxRobot robot) {
        MapExpressionHelper.SimpleChange<String, Boolean> change = new MapExpressionHelper.SimpleChange<>(FXCollections.observableMap(Collections.singletonMap("temporaryProjectRadioButton", false)));
        settingsPage.stateChanged(change);
        verifyThat(settingsPage.getExistingSettingsFilePathRadioButton(), radioButton -> !radioButton.isDisabled());
    }

    @Test
    void test_defaultSettings_selected(FxRobot robot) {
        robot.clickOn("#defaultSettingsRadioButton");
        verifyThat(settingsPage.getDefaultSettingsRadioButton(), ToggleButton::isSelected);
    }

    @Test
    void test_loadFromConfigurationFile_noFileSelected_invalid(FxRobot robot) {
        robot.clickOn("#existingSettingsFilePathRadioButton");
        verifyThat(settingsPage.getExistingSettingsFilePathRadioButton(), ToggleButton::isSelected);
        robot.interact(() -> {
            assertThat(settingsPage.getValidator().validate()).isFalse();
            assertThat(settingsPage.getValidator().containsErrors()).isTrue();
            assertThat(settingsPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });

        robot.clickOn("#existingSettingsFilePath").write("a.txt");
        robot.interact(() -> {
            assertThat(settingsPage.getValidator().validate()).isFalse();
            assertThat(settingsPage.getValidator().containsErrors()).isTrue();
            assertThat(settingsPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });

    }

    @Test
    void test_loadFromConfigurationFile_selectValidFile_valid(FxRobot robot, @TempDir Path tempDir) throws IOException {
        robot.clickOn("#existingSettingsFilePathRadioButton");
        verifyThat(settingsPage.getExistingSettingsFilePathRadioButton(), ToggleButton::isSelected);
        robot.interact(() -> {
            assertThat(settingsPage.getValidator().validate()).isFalse();
            assertThat(settingsPage.getValidator().containsErrors()).isTrue();
            assertThat(settingsPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });


        Path path = tempDir.resolve(Paths.get("a.txt"));
        Files.writeString(path, "This is a test file", Charset.defaultCharset());

        robot.clickOn("#existingSettingsFilePath").write(path.toFile().getAbsolutePath());
        robot.interact(() -> {
            assertThat(settingsPage.getValidator().validate()).isTrue();
            assertThat(settingsPage.getValidator().containsErrors()).isFalse();
            assertThat(settingsPage.getValidator().getValidationResult().getMessages()).isEmpty();
        });
    }


    @Test
    void test_openExistingProjectFile(FxRobot robot) {
        doReturn("a.txt").when(settingsPage).chooseFile(any(), any());
        robot.clickOn("#openProjectButton");
        verifyThat(settingsPage.getExistingSettingsFilePath(), hasText("a.txt"));
    }
}
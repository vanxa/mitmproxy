package com.vanxacloud.appstudio.proximity.fx.control.wizard.page;

import com.vanxacloud.appstudio.proximity.config.Constants;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.pane.StartupWizardPane;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

@ExtendWith({ApplicationExtension.class, SpringExtension.class})
@SpringBootTest
class ProjectPageTest {


    @MockitoSpyBean
    @Autowired
    private ProjectPage projectPage;


    @Start
    private void start(Stage stage) {
        StartupWizardPane pane = new StartupWizardPane("pages/wizard/projects.fxml", clazz -> projectPage);
        Scene scene = new Scene(pane, 320, 240);
        stage.setTitle("ProjectPage");
        stage.setScene(scene);
        scene.getStylesheets().add("/style.css");
        stage.show();

    }

    @Test
    void test_temporaryRadioButton_selected(FxRobot robot) {
        robot.clickOn("#temporaryProjectRadioButton");
        verifyThat(projectPage.getTemporaryProjectRadioButton(), ToggleButton::isSelected);
    }

    @Test
    void test_newProjectRadioButton_newProjectNameFilled(FxRobot robot) {
        robot.clickOn("#newProjectRadioButton");
        String date = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.getDefault()).format(LocalDateTime.now());
        verifyThat(projectPage.getNewProjectRadioButton(), ToggleButton::isSelected);
        verifyThat(projectPage.getNewProjectName(), hasText(date));
        verifyThat(projectPage.getNewProjectFilePath(), hasText(String.format("%s.%s", date, Constants.PROJECT_FILE_EXTENSION)));
    }

    @Test
    void test_existingProjectRadioButton_noFileSelected_invalid(FxRobot robot) {
        robot.clickOn("#existingProjectRadioButton");
        verifyThat(projectPage.getExistingProjectRadioButton(), ToggleButton::isSelected);
        robot.interact(() -> {
            assertThat(projectPage.getValidator().validate()).isFalse();
            assertThat(projectPage.getValidator().containsErrors()).isTrue();
            assertThat(projectPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });

    }

    @Test
    void test_existingProjectRadioButton_selectInvalidFile_invalid(FxRobot robot) {
        robot.clickOn("#existingProjectRadioButton");
        verifyThat(projectPage.getExistingProjectRadioButton(), ToggleButton::isSelected);
        robot.interact(() -> {
            assertThat(projectPage.getValidator().validate()).isFalse();
            assertThat(projectPage.getValidator().containsErrors()).isTrue();
            assertThat(projectPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });

        robot.clickOn("#existingProjectFilePath").write("a.txt");
        robot.interact(() -> {
            assertThat(projectPage.getValidator().validate()).isFalse();
            assertThat(projectPage.getValidator().containsErrors()).isTrue();
            assertThat(projectPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });

    }

    @Test
    void test_existingProjectRadioButton_selectValidFile_valid(FxRobot robot, @TempDir Path tempDir) throws IOException {
        robot.clickOn("#existingProjectRadioButton");
        verifyThat(projectPage.getExistingProjectRadioButton(), ToggleButton::isSelected);
        robot.interact(() -> {
            assertThat(projectPage.getValidator().validate()).isFalse();
            assertThat(projectPage.getValidator().containsErrors()).isTrue();
            assertThat(projectPage.getValidator().getValidationResult().getMessages()).contains(new ValidationMessage(Severity.ERROR, "Choose a valid file"));
        });


        Path path = tempDir.resolve(Paths.get("a.txt"));
        Files.writeString(path, "This is a test file", Charset.defaultCharset());

        robot.clickOn("#existingProjectFilePath").write(path.toFile().getAbsolutePath());
        robot.interact(() -> {
            assertThat(projectPage.getValidator().validate()).isTrue();
            assertThat(projectPage.getValidator().containsErrors()).isFalse();
            assertThat(projectPage.getValidator().getValidationResult().getMessages()).isEmpty();
        });
    }

    @Test
    void test_newProjectRadioButton_chooseFile(FxRobot robot) throws IOException {
        doReturn("a.txt").when(projectPage).chooseFile(any(), any());
        robot.clickOn("#newProjectRadioButton");
        robot.clickOn("#newProjectButton");
        verifyThat(projectPage.getNewProjectFilePath(), hasText("a.txt"));
        assertThat(projectPage.getValidator().validate()).isTrue();
    }

    @Test
    void test_existingProjectRadioButton_chooseFile(FxRobot robot, @TempDir Path tempDir) throws IOException {
        Path path = tempDir.resolve(Paths.get("a.txt"));
        Files.writeString(path, "This is a test file", Charset.defaultCharset());

        doReturn(path.toFile().getAbsolutePath()).when(projectPage).chooseFile(any(), any());
        robot.clickOn("#existingProjectRadioButton");
        robot.clickOn("#openProjectButton");
        verifyThat(projectPage.getExistingProjectFilePath(), hasText(path.toFile().getAbsolutePath()));
        assertThat(projectPage.getValidator().validate()).isTrue();
    }

    @Test
    void test_openNewProjectFile(FxRobot robot) {
        doReturn("a.txt").when(projectPage).chooseFile(any(), any());
        robot.clickOn("#newProjectButton");
        verifyThat(projectPage.getNewProjectFilePath(), hasText("a.txt"));

    }

    @Test
    void test_openExistingProjectFile(FxRobot robot) {
        doReturn("a.txt").when(projectPage).chooseFile(any(), any());
        robot.clickOn("#openProjectButton");
        verifyThat(projectPage.getExistingProjectFilePath(), hasText("a.txt"));
    }

}
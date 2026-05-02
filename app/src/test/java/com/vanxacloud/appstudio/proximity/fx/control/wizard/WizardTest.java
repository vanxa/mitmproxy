package com.vanxacloud.appstudio.proximity.fx.control.wizard;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith({ApplicationExtension.class, SpringExtension.class})
@SpringBootTest
class WizardTest {

    @Autowired
    @MockitoSpyBean
    private Wizard wizard;

    @Test
    void test_showWizard_cancel(FxRobot robot) {
        final Wizard.Settings[] settings = new Wizard.Settings[1];
        Platform.runLater(() -> {
            settings[0] = wizard.show();
        });
        WaitForAsyncUtils.waitForFxEvents();
        robot.clickOn("Cancel");
        assertThat(settings[0]).isNull();

    }

    @Test
    void test_showWizard_emtySettings_returnNull(FxRobot robot) {
        org.controlsfx.dialog.Wizard wiz = mock(org.controlsfx.dialog.Wizard.class);
        doReturn(wiz).when(wizard).initializeWizard();
        doReturn(Optional.empty()).when(wiz).showAndWait();

        robot.interact(() -> {
            Wizard.Settings settings = wizard.show();
            assertThat(settings).isNull();
        });
    }

    @Test
    void test_showWizard_cancel_returnNull(FxRobot robot) {
        org.controlsfx.dialog.Wizard wiz = mock(org.controlsfx.dialog.Wizard.class);
        doReturn(wiz).when(wizard).initializeWizard();
        doReturn(Optional.of(ButtonType.CANCEL)).when(wiz).showAndWait();

        robot.interact(() -> {
            Wizard.Settings settings = wizard.show();
            assertThat(settings).isNull();
        });
    }

    @Test
    void test_showWizard_finish_returnWizardSettings(FxRobot robot) {
        org.controlsfx.dialog.Wizard wiz = mock(org.controlsfx.dialog.Wizard.class);
        doReturn(wiz).when(wizard).initializeWizard();
        ObservableMap<String, Object> settings = FXCollections.observableMap(Collections.singletonMap("Key", "Value"));
        doReturn(settings).when(wiz).getSettings();
        doReturn(Optional.of(ButtonType.FINISH)).when(wiz).showAndWait();

        robot.interact(() -> {
            Wizard.Settings set = wizard.show();
            assertThat(set).isNotNull();
            assertThat(set.settings().get("Key")).isEqualTo("Value");
        });
    }
}
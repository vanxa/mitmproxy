package com.vanxacloud.appstudio.proximity.app.event;

import com.vanxacloud.appstudio.proximity.GeneratedSkipCoverage;
import com.vanxacloud.appstudio.proximity.app.core.state.ApplicationState;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.Wizard;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

@GeneratedSkipCoverage
public interface Events {

    abstract class ApplicationStateEvent extends ApplicationEvent {

        private final ApplicationState applicationState;

        public ApplicationStateEvent(Stage source, ApplicationState applicationState) {
            super(source);
            this.applicationState = applicationState;
        }

        public Stage getStage() {
            return (Stage) getSource();
        }

        public ApplicationState getApplicationState() {
            return applicationState;
        }
    }

    class ReadyForWizardEvent extends ApplicationStateEvent {

        public ReadyForWizardEvent(Stage source) {
            super(source, ApplicationState.WIZARD);
        }
    }


    class ApplicationStartingEvent extends ApplicationStateEvent {

        private final Wizard.Settings wizardSettings;

        public ApplicationStartingEvent(Stage source, Wizard.Settings wizardSettings) {
            super(source, ApplicationState.STARTING);
            this.wizardSettings = wizardSettings;
        }

        public Wizard.Settings getWizardSettings() {
            return wizardSettings;
        }
    }

    class ApplicationStoppingEvent extends ApplicationStateEvent {

        public ApplicationStoppingEvent(Stage source) {
            super(source, ApplicationState.STOPPING);
        }
    }

    class ApplicationRunningEvent extends ApplicationStateEvent {

        public ApplicationRunningEvent(Stage stage) {
            super(stage, ApplicationState.RUNNING);
        }
    }

    class ApplicationStoppedEvent extends ApplicationStateEvent {

        public ApplicationStoppedEvent(Stage stage) {
            super(stage, ApplicationState.STOPPED);
        }
    }

}

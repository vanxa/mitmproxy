package com.vanxacloud.appstudio.proximity;

import com.vanxacloud.appstudio.proximity.app.event.Events;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.Wizard;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@GeneratedSkipCoverage
public class JavaFxApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(JavaFxApplication.class);
    private ConfigurableApplicationContext context;
    private Stage mainStage;


    @Override
    public void init() throws Exception {
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                ac -> {
                    ac.registerBean(Application.class, () -> JavaFxApplication.this);
                    ac.registerBean(Parameters.class, this::getParameters);
                    ac.registerBean(HostServices.class, this::getHostServices);
                };
        this.context = new SpringApplicationBuilder()
                .sources(ProximityApplication.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) throws IOException {
        CSSFX.start();
        this.mainStage = stage;
        RunContext context = new RunContext(getParameters());
        if (context.useDefaults) {
            this.context.publishEvent(new Events.ApplicationStartingEvent(stage, new Wizard.Settings(FXCollections.observableMap(Collections.emptyMap()))));
        } else {
            this.context.publishEvent(new Events.ReadyForWizardEvent(stage));
        }
    }

    @Override
    public void stop() throws Exception {
        this.context.publishEvent(new Events.ApplicationStoppingEvent(this.mainStage));
        this.context.close();
        Platform.exit();
    }

    private static class RunContext {

        private final boolean useDefaults;

        public RunContext(Parameters params) {
            List<String> raw = params.getRaw();
            useDefaults = raw.contains("--use-defaults");
        }
    }
}



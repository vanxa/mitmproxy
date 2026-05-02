package com.vanxacloud.appstudio.proximity;

import com.vanxacloud.appstudio.proximity.config.ConfigurationManager;
import com.vanxacloud.appstudio.proximity.config.PropertiesConfiguration;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfiguration.class)
@GeneratedSkipCoverage
public class ProximityApplication {
    public static void main(String[] args) {
        ConfigurationManager.getInstance().initialize();
        Application.launch(JavaFxApplication.class, args);
    }
}

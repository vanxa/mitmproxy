package com.vanxacloud.appstudio.proximity.config;

import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ConfigurationManager {

    private Logger log;
    private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    private Path basePath;
    private boolean initialized;

    private ConfigurationManager() {

    }

    public synchronized void initialize() {
        if (!initialized) {
            this.basePath = initDataDir();
            this.log = initializeLogging();
            this.initialized = true;
        }
        this.log.debug("Running on {} Version {} Arch {}", SystemProperties.getOsName(), SystemProperties.getOsVersion(), SystemProperties.getOsVersion());
    }

    private Logger initializeLogging() {
        if (basePath != null) {
            System.setProperty("dataDir", basePath.toString());
        }
        return LoggerFactory.getLogger(ConfigurationManager.class);
    }

    private Path initDataDir() {
        try {
            Path basePath = null;
            String configPath = SystemProperties.getProperty("configPath");
            if (configPath != null) {
                basePath = Paths.get(configPath);
            } else {
                if (SystemUtils.IS_OS_WINDOWS) {
                    basePath = Paths.get(SystemUtils.getEnvironmentVariable("APPDATA", "proximity"));
                } else if (SystemUtils.IS_OS_LINUX) {
                    basePath = Paths.get(SystemProperties.getProperty("user.home"), ".config", "proximity");
                }
                if (basePath == null) {
                    throw new RuntimeException(String.format("Unsupported operating system - %s", SystemUtils.OS_NAME));
                }

                if (Files.notExists(basePath)) {
                    Files.createDirectories(basePath);
                }
            }
            return basePath;
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize configuration due to exception", e);
        }
    }

    public static ConfigurationManager getInstance() {
        return INSTANCE;
    }

    Path getBasePath() {
        return basePath;
    }
}

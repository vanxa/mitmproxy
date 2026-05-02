package com.vanxacloud.appstudio.proximity.config;

import org.apache.commons.lang3.SystemProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class ConfigurationManagerTest {

    @AfterEach
    void cleanUp() {
        ConfigurationManager instance = ConfigurationManager.getInstance();
        ReflectionTestUtils.setField(instance, "initialized", false);
        System.setProperty("dataDir", "");
    }

    @Test
    void test_getConfigBasePath_fromSystemProperty() {
        ConfigurationManager instance = ConfigurationManager.getInstance();
        try (MockedStatic<SystemProperties> staticMock = mockStatic(SystemProperties.class)) {
            staticMock.when(() -> SystemProperties.getProperty(eq("configPath"))).thenReturn("/tmp");
            instance.initialize();
            assertThat(ConfigurationManager.getInstance().getBasePath()).isEqualTo(Paths.get("/tmp"));
        }


    }

}
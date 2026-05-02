package com.vanxacloud.appstudio.proximity.app;

import com.vanxacloud.appstudio.proximity.JavaFxApplication;
import com.vanxacloud.appstudio.proximity.app.proxy.Proxy;
import com.vanxacloud.appstudio.proximity.fx.control.wizard.Wizard;
import com.vanxacloud.appstudio.proximity.fx.splash.SplashLoader;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

/**
 * The main app class responsible for managing the main application window and its main subcomponents - see @{@link Proxy}
 */
@Component
public class MainApp {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);
    private final GenericApplicationContext ac;
    private final Proxy proxy;


    MainApp(GenericApplicationContext ac, Proxy proxy) {
        this.ac = ac;
        this.proxy = proxy;
    }


    /**
     * Starts the application and opens the main app window.
     *
     * @param stage    - the stage for the main app
     * @param settings - the wizard settings
     */
    public void start(Stage stage, Wizard.Settings settings) {
        SplashLoader splashLoader = new SplashLoader();
        final Task<Boolean> proxyStartupTask = new Task<>() {
            @Override
            protected Boolean call() throws InterruptedException {
                updateMessage("Starting proxy listener . . .");
                String id = proxy.startListener();
                Thread.sleep(1000);
                boolean ready = false;
                while (!ready) {
                    switch (proxy.getListenerState(id)) {
                        case INITIALIZING -> updateMessage("Initializing proxy listener");
                        case INITIALIZED -> {
                            updateMessage("Proxy listener initialized");
                            updateProgress(1, 4);
                        }
                        case CONFIGURING -> {
                            updateMessage("Configuring proxy listener");
                            updateProgress(2, 4);
                        }
                        case CONFIGURED -> {
                            updateMessage("Proxy listener configured");
                            updateProgress(3, 4);
                        }
                        case LISTENING -> {
                            updateMessage("Proxy listener running");
                            updateProgress(4, 4);
                            ready = true;
                        }
                    }
                    log.info("Sleeping");
                    Thread.sleep(1000);
                }
                log.info("Done");
                return true;
            }
        };

        splashLoader.showSplash(
                proxyStartupTask,
                () -> showMainStage(stage)
        );
        new Thread(proxyStartupTask).start();
    }

    private void showMainStage(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxApplication.class.getResource("/boot-view.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.initStyle(StageStyle.DECORATED);
            stage.setMaximized(true);
            scene.getStylesheets().add("/style.css");
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        log.info("Closing application");
        proxy.close();
    }


    public interface InitCompletionHandler {
        void complete();
    }
}

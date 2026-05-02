package com.vanxacloud.appstudio.proximity.fx.splash;

import com.vanxacloud.appstudio.proximity.GeneratedSkipCoverage;
import com.vanxacloud.appstudio.proximity.JavaFxApplication;
import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * This preload will display a splash image while starting the application. It will close just before the wizard is displayed
 */
@GeneratedSkipCoverage
public class SplashPreloader extends Preloader {
    private Stage preloaderStage;
    @FXML
    private ImageView logoImageView;


    // Called automatically after FXML is loaded
    @FXML
    private void initialize() {
        // Optional: Load image or set initial state
        Image logo = new Image(JavaFxApplication.class.getResourceAsStream("/images/logo.png"));
        logoImageView.setImage(logo);
    }

    @Override
    public void start(Stage stage) throws Exception {
        preloaderStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/splash/preloader.fxml"));
        loader.setController(this);
        AnchorPane root = loader.load();
        stage.setScene(new Scene(root, 900, 900));
        stage.setHeight(900);
        stage.setWidth(900);
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            preloaderStage.close();
        }
    }
}

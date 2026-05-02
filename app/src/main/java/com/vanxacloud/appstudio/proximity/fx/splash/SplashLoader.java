package com.vanxacloud.appstudio.proximity.fx.splash;

import com.vanxacloud.appstudio.proximity.GeneratedSkipCoverage;
import com.vanxacloud.appstudio.proximity.JavaFxApplication;
import com.vanxacloud.appstudio.proximity.app.MainApp;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@GeneratedSkipCoverage
public class SplashLoader {
    @FXML
    private ImageView logoImageView;

    @FXML
    private ProgressBar loadingProgress;

    @FXML
    private Label statusLabel;


    public void showSplash(Task<?> task,
                           MainApp.InitCompletionHandler initCompletionHandler) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/splash/splash.fxml"));
        try {
            loader.setController(this);
            AnchorPane splashLayout = loader.load();

            Image logo = new Image(JavaFxApplication.class.getResourceAsStream("/images/logo.png"));
            logoImageView.setImage(logo);
            Stage initStage = new Stage();
            initStage.setScene(new Scene(splashLayout, 900, 1000));
            initStage.setMaxWidth(1000);
            initStage.setMaxHeight(1000);
            initStage.initStyle(StageStyle.UNDECORATED);
            statusLabel.textProperty().bind(task.messageProperty());
            loadingProgress.progressProperty().bind(task.progressProperty());
            task.stateProperty().addListener((observableValue, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    loadingProgress.progressProperty().unbind();
                    loadingProgress.setProgress(1);
                    initStage.hide();
                    initCompletionHandler.complete();
                }
            });
            initStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

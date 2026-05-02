package com.vanxacloud.appstudio.proximity.fx.control.wizard.page;

import com.vanxacloud.appstudio.proximity.GeneratedSkipCoverage;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

public abstract class WizardDialogPage {

    private final SimpleBooleanProperty valid = new SimpleBooleanProperty(true);

    private final ObservableMap<String, Object> wizardState;

    private final MapChangeListener<String, Object> listener = this::stateChanged;

    private final Validator validator = new Validator();

    public WizardDialogPage() {
        this.wizardState = FXCollections.observableHashMap();
        wizardState.addListener(listener);

    }

    @GeneratedSkipCoverage(comment = "Whole functionality relies on FileChooser, which is not easily tested")
    protected String chooseFile(Button source, String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        Stage stage = (Stage) (source).getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }


    public BooleanExpression isValid() {
        return valid;
    }

    public void changeState(ObservableMap<String, Object> settings) {
        this.wizardState.putAll(settings);
    }


    void stateChanged(MapChangeListener.Change<? extends String, ?> change) {

    }

    public Validator getValidator() {
        return validator;
    }

    public SimpleBooleanProperty validProperty() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }
}

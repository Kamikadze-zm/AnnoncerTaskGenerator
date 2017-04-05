package ru.kamikadze_zm.announcertaskgenerator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AnnouncementController implements Initializable {
    
    private MainController parentController;

    @FXML
    private ComboBox usedPaths;

    @FXML
    private TextField moviePath;

    @FXML
    private TextField announcement;

    @FXML
    private Label symbolsCount;

    @FXML
    private CheckBox upperCase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usedPaths.setItems(FXCollections.observableArrayList(Main.paths));
        symbolsCount.textProperty().bind(announcement.lengthProperty().asString());
        upperCase.selectedProperty().set(true);
    }

    @FXML
    private void pastePath(ActionEvent event) {
        moviePath.setText(usedPaths.getSelectionModel().getSelectedItem().toString());
        moviePath.requestFocus();
        moviePath.end();
    }

    @FXML
    private void addAnnouncement(ActionEvent event) {
        if (moviePath.getText().isEmpty()) {
            Main.showMessage("Ошибка", "Не указан путь к файлу", Alert.AlertType.ERROR);
            return;
        }
        Announcement a = new Announcement(moviePath.getText(), announcement.getText(), upperCase.selectedProperty().get());
        if (a.getMovieName().isEmpty()) {
            Main.showMessage("Ошибка", "Не указано имя файла", Alert.AlertType.ERROR);
            return;
        }
        parentController.addAnnouncement(a);
        ((Stage) moviePath.getScene().getWindow()).close();
    }

    public void setParentControllerAndStage(MainController parentController) {
        this.parentController = parentController;
    }
}

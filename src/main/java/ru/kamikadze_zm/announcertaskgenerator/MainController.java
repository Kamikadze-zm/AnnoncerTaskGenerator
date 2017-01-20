package ru.kamikadze_zm.announcertaskgenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class MainController implements Initializable {

    private ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @FXML
    private ListView<Announcement> lvAnnouncements;
    @FXML
    private Button saveBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        announcements.addAll(Main.announcements);

        lvAnnouncements.setCellFactory(f -> new ListViewCell(announcements) {
            {
                prefWidthProperty().bind(lvAnnouncements.widthProperty().add(-20));
            }
        });
        lvAnnouncements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lvAnnouncements.setItems(announcements);
    }

    @FXML
    private void saveAndExit(ActionEvent event) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(Main.outFileName, false))) {
            for (Announcement a : announcements) {
                writer.write(a.toTaskString().trim() + System.lineSeparator());
                writer.flush();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Сохранено");
            alert.setHeaderText("Записано в файл: " + Main.outFileName);
            alert.showAndWait();
            Main.exit(0);
        } catch (IOException e) {
            Main.showError("Ошибка записи в файл: " + Main.outFileName + ". Попробуйте ещё раз");
        }
    }
}

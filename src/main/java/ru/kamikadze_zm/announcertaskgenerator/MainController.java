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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController implements Initializable {
    
    private static final Logger LOG = LogManager.getLogger(MainController.class);

    private Stage mainStage;

    private ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @FXML
    private ListView<Announcement> lvAnnouncements;

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
    private void newAnnouncement(ActionEvent event) {
        Stage announcementForm = new Stage();
        announcementForm.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Announcement.fxml"));
        try {
            Parent root = loader.load();
            AnnouncementController controller = loader.<AnnouncementController>getController();
            controller.setParentControllerAndStage(this);
            Scene scene = new Scene(root);
            announcementForm.setTitle("Добавление анонса");
            announcementForm.setScene(scene);
            announcementForm.initModality(Modality.WINDOW_MODAL);
            announcementForm.initOwner(mainStage);
            announcementForm.show();
        } catch (IOException e) {
            LOG.warn("Load Announcement.fxml exception: ", e);
            Main.showMessage("Ошибка", "Не удалось загрузить форму создания анонса", Alert.AlertType.ERROR);
        }
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
            LOG.warn("Write announcements to file - " + Main.outFileName + "exception: ", e);
            Main.showErrorAndExit("Ошибка записи в файл: " + Main.outFileName);
        }
    }
    
    public void addAnnouncement(Announcement a) {
        announcements.add(a);
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}

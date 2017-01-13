package ru.kamikadze_zm.announcertaskgenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Command.CommandKey;
import ru.kamikadze_zm.onair.command.Movie;

public class MainController implements Initializable {

    private ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @FXML
    private ListView<Announcement> lvAnnouncements;
    @FXML
    private Button saveBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String lastMovieFileName = "";
        SortedSet<Announcement> annSet = new TreeSet<>();
        for (Command c : Main.commands) {
            if (CommandKey.MOVIE == c.getCommandKey()) {
                Movie m = (Movie) c;
                if (!lastMovieFileName.equals(m.getFileName())) {
                    if (!m.getFileName().startsWith("D:\\")) {
                        lastMovieFileName = m.getFileName();
                        Announcement a = new Announcement(m.getFileName());
                        a.setAnnouncement(a.getMovieName()
                                .substring(0, a.getMovieName().lastIndexOf(".")));
                        annSet.add(a);
                    }
                }
            }
        }
        announcements.addAll(annSet);

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
                writer.write(a.toTaskString() + System.lineSeparator());
                writer.flush();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Сохранено");
            alert.setHeaderText("Записано в файл: " + Main.outFileName);
            alert.showAndWait();
            System.exit(0);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка записи в файл: " + Main.outFileName
            + ". Попробуйте ещё раз");
            alert.showAndWait();
        }
    }
}

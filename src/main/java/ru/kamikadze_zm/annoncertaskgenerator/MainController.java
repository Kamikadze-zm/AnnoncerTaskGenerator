package ru.kamikadze_zm.annoncertaskgenerator;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Command.CommandKey;
import ru.kamikadze_zm.onair.command.Movie;

public class MainController implements Initializable {

    private ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @FXML
    private ListView<Announcement> lvAnnouncements;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String lastMovieFileName = "";
        for (Command c : Main.commands) {
            if (CommandKey.MOVIE == c.getCommandKey()) {
                Movie m = (Movie) c;
                if (!lastMovieFileName.equals(m.getFileName())) {
                    if (!m.getFileName().startsWith("D:\\")) {
                        lastMovieFileName = m.getFileName();
                        Announcement a = new Announcement(m.getFileName());
                        a.setAnnouncement(a.getMovieName()
                                .substring(0, a.getMovieName().lastIndexOf(".")));
                        announcements.add(a);
                    }
                }
            }
        }

        lvAnnouncements.setCellFactory(f -> new ListViewCell((ActionEvent event) -> {
            remove();
        }));
        lvAnnouncements.setItems(announcements);
    }

    public void remove() {

    }
}

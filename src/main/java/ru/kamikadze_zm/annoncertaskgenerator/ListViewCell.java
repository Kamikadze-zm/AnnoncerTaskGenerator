package ru.kamikadze_zm.annoncertaskgenerator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ListViewCell extends ListCell<Announcement> {

    private final Label movieName;
    private final TextField announcement;
    private final Button actionBtn;
    private final HBox box;

    public ListViewCell(EventHandler<ActionEvent> event) {
        super();

        setOnMouseClicked((MouseEvent event1) -> {
        });

        actionBtn = new Button("Удалить");
        actionBtn.setOnAction(event);
        movieName = new Label();
        announcement = new TextField();
        box = new HBox(movieName, announcement, actionBtn);
    }

    @Override
    public void updateItem(Announcement item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            movieName.setText(item.getMovieName());
            announcement.setText(item.getAnnouncement());
            setGraphic(box);
        }
    }
}

package ru.kamikadze_zm.announcertaskgenerator;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class ListViewCell extends ListCell<Announcement> {

    private final Label movieName;
    private final TextField announcement;
    private final Label symbolCount;
    private final CheckBox upperCase;
    private final Button actionBtn;
    private final HBox box;
    private ObservableList<Announcement> announcements;

    public ListViewCell(ObservableList<Announcement> announcements) {
        super();

        this.announcements = announcements;
        setOnMouseClicked((MouseEvent event) -> {
        });

        actionBtn = new Button("Удалить");
        actionBtn.setOnAction(event -> {
            this.announcements.remove(this.getItem());
        });

        movieName = new Label();
        movieName.setId("movie-name");

        announcement = new TextField();
        announcement.textProperty().addListener((observable, oldValue, newValue) -> {
            this.getItem().setAnnouncement(newValue.trim());
        });

        symbolCount = new Label();
        symbolCount.textProperty().bind(announcement.lengthProperty().asString());

        upperCase = new CheckBox();
        upperCase.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (this.getItem() != null) {
                if (newValue) {
                    this.getItem().setUpperCase(true);
                } else {
                    this.getItem().setUpperCase(false);
                }
            }
        });

        box = new HBox(movieName, announcement, symbolCount, upperCase, actionBtn);
        box.setId("announcement-box");

        box.prefWidthProperty().bind(this.prefWidthProperty());
        movieName.prefWidthProperty().bind(box.prefWidthProperty().multiply(0.400));
        announcement.prefWidthProperty().bind(box.prefWidthProperty().multiply(0.425));
        symbolCount.prefWidthProperty().bind(box.prefWidthProperty().multiply(0.025));
        upperCase.prefWidthProperty().bind(box.prefWidthProperty().multiply(0.005));
        actionBtn.prefWidthProperty().bind(box.prefWidthProperty().multiply(0.10));
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
            upperCase.selectedProperty().set(true);
            setGraphic(box);
        }
    }
}

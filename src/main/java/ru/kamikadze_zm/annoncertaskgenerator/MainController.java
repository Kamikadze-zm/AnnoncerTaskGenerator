package ru.kamikadze_zm.annoncertaskgenerator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ru.kamikadze_zm.onair.command.Command;

public class MainController implements Initializable {
    
    private List<Command> commands;
    
    @FXML
    private ListView lvAnnouncements;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}

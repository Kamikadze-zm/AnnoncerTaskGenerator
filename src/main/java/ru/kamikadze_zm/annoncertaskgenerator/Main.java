package ru.kamikadze_zm.annoncertaskgenerator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.kamikadze_zm.onair.OnAirParserException;
import ru.kamikadze_zm.onair.Parser;
import ru.kamikadze_zm.onair.command.Command;

public class Main extends Application {

    public static List<Command> commands;
    public static String outFileName;

    @Override
    public void start(Stage stage) throws Exception {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("\\\\fs.settv.ru\\incoming"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            if (!file.getName().endsWith(".air")) {
                throw new RuntimeException("Неверное расширение файла. Требуется *.air");
            }

            try {
                commands = Parser.parse(file);
            } catch (OnAirParserException e) {
                ButtonType continueButton = new ButtonType("Продолжить", ButtonData.OK_DONE);
                ButtonType closeButton = new ButtonType("Выход", ButtonData.CANCEL_CLOSE);
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.getDialogPane().getButtonTypes().add(continueButton);
                dialog.getDialogPane().getButtonTypes().add(closeButton);
                dialog.setTitle("Ошибка");
                dialog.setHeaderText(e.getMessage());
                dialog.setContentText("Продолжить выполнение?");
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    System.exit(1);
                }
            }

            String fileName = file.getName();
            //yyyy_MM_dd
            Pattern pattern = Pattern.compile("\\d\\d\\d\\d_\\d\\d_\\d\\d");
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                String date = fileName.substring(matcher.start(), matcher.end());
                outFileName = "AnnouncerTask_" + date + "txt";
            }

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));

            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");

            stage.setTitle("Генератор файла задания для анонсера");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as
     * fallback in case the application can not be launched through deployment artifacts, e.g., in
     * IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

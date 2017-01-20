package ru.kamikadze_zm.announcertaskgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import ru.kamikadze_zm.onair.OnAirParserException;
import ru.kamikadze_zm.onair.Parser;
import ru.kamikadze_zm.onair.command.Command;
import ru.kamikadze_zm.onair.command.Movie;

public class Main extends Application {

    public static SortedSet<Announcement> announcements;
    public static String outFileName;

    private final static String SEPARATOR = "|";

    @Override
    public void start(Stage stage) throws Exception {

        final FileChooser fileChooser = new FileChooser();
        File initFolder = new File("\\\\fs.settv.ru\\incoming");
        if (initFolder.exists()) {
            fileChooser.setInitialDirectory(initFolder);
        } else {
            fileChooser.setInitialDirectory(new File("."));
        }
        fileChooser.setTitle("Выберите расписание (*.air)");
        ExtensionFilter extFilter = new ExtensionFilter(
                "Расписание (*.air) или файл задание (*.txt)", "*.air", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            showError("Файл не выбран");
            exit(0);
            return;
        }
        
        String fileName = file.getName();
        
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!"air".equalsIgnoreCase(ext) && !"txt".equalsIgnoreCase(ext)) {
            showError("Неверное расширение файла: " + ext);
            exit(1);
        }

        if (ext.equalsIgnoreCase("air")) {
            try {
                List<Command> commands = Parser.parse(file);
                announcements = getAnnouncements(commands);
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
                    exit(0);
                }
            }
        } else {
            announcements = getAnnouncements(file);
        }

        //yyyy_MM_dd
        Pattern pattern = Pattern.compile("\\d\\d\\d\\d_\\d\\d_\\d\\d");
        Matcher matcher = pattern.matcher(fileName);
        String date = "";

        if (matcher.find()) {
            date = fileName.substring(matcher.start(), matcher.end());
        }
        String fullPath = file.getAbsolutePath();
        outFileName = fullPath.substring(0, fullPath.lastIndexOf("\\") + 1)
                + "AnnouncerTask_" + date + ".txt";

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));

        Scene scene = new Scene(root);

        scene.getStylesheets()
                .add("/styles/Styles.css");

        stage.setTitle(
                "Генератор файла задания для анонсера");
        stage.setScene(scene);

        stage.show();

    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static void exit(int status) {
        System.exit(status);
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

    private static SortedSet getAnnouncements(List<Command> commands) {
        String lastMovieFileName = "";
        SortedSet<Announcement> annSet = new TreeSet<>();
        for (Command c : commands) {
            if (Command.CommandKey.MOVIE == c.getCommandKey()) {
                Movie m = (Movie) c;
                if (!lastMovieFileName.equals(m.getFileName())) {
                    if (!m.getFileName().startsWith("D:\\")) {
                        lastMovieFileName = m.getFileName();
                        Announcement a = new Announcement(m.getFileName(), true);
                        annSet.add(a);
                    }
                }
            }
        }
        return annSet;
    }

    private static SortedSet getAnnouncements(File announcerTaskFile) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(announcerTaskFile.getAbsolutePath()),
                    Charset.forName("cp1251"));
            SortedSet<Announcement> annSet = new TreeSet<>();
            lines.forEach((l) -> {
                annSet.add(parseAnnouncement(l));
            });
            return annSet;
        } catch (IOException e) {
            showError("Не удалось открыть файл: " + announcerTaskFile.getAbsolutePath());
            exit(1);
        }
        return null;
    }

    private static Announcement parseAnnouncement(String annString) {
        String movieName = annString.substring(0, annString.indexOf(SEPARATOR)).trim();
        String announcement = annString.substring(annString.indexOf(SEPARATOR) + 1).trim();
        String annUpper = announcement.toUpperCase();
        return new Announcement(movieName, announcement, annUpper.equals(announcement));
    }
}

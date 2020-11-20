package xyz.pary.announcertaskgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static xyz.pary.announcertaskgenerator.Main.HOME_DIRECTORY;
import static xyz.pary.announcertaskgenerator.Main.exit;
import xyz.pary.onair.OnAirParserException;
import xyz.pary.onair.Parser;
import xyz.pary.onair.command.Command;
import xyz.pary.onair.command.Movie;

public class App extends Application {

    private static Logger LOG;

    public static SortedSet<Announcement> announcements;
    public static String outFileName;

    public static String separator = "|";

    public static Set<String> paths = new HashSet<String>();

    @Override
    public void start(Stage stage) throws Exception {

        System.setProperty("homeDir", HOME_DIRECTORY);
        LOG = LogManager.getLogger(Main.class);

        //проверка наличия папки с настройками
        File homeDirectory = new File(HOME_DIRECTORY);
        if (!homeDirectory.exists()) {
            homeDirectory.mkdirs();
        }

        //загрузка настрроек
        try {
            File settingsFile = new File(Settings.SETTINGS_FILE);
            if (!settingsFile.exists()) {
                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Выберите файл с настройками (в кодировке cp1251) - будет скопирован в " + Settings.SETTINGS_FILE);
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    try {
                        Files.copy(file.toPath(), settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ce) {
                        LOG.error("Error of copying settings file: ", ce);
                        showErrorAndExit("Не удалось скопировать файл с настройками");
                    }
                }
            }
            Settings.loadSettings(settingsFile);
            separator = Settings.getParameter(Settings.SettingsKey.ANNOUNCER_NOW_SEPARATOR);
        } catch (SettingsException e) {
            showErrorAndExit(e.getMessage());
        } catch (FileNotFoundException e) {
            showErrorAndExit("Не найден файл с настроками: " + Settings.SETTINGS_FILE);
        } catch (IOException e) {
            showErrorAndExit("Не удалось загрузить файл с настройками");
        }

        //выбор файла
        final FileChooser fileChooser = new FileChooser();
        File initFolder = new File(Settings.getParameter(Settings.SettingsKey.SCHEDULE_PATH));
        if (initFolder.exists()) {
            fileChooser.setInitialDirectory(initFolder);
        } else {
            fileChooser.setInitialDirectory(new File("."));
        }
        fileChooser.setTitle("Выберите расписание (*.air) или файл задание (*.txt)");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Расписание (*.air) или файл задание (*.txt)", "*.air", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            showErrorAndExit("Файл не выбран");
            return;
        }

        String fileName = file.getName();

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!"air".equalsIgnoreCase(ext) && !"txt".equalsIgnoreCase(ext)) {
            showErrorAndExit("Неверное расширение файла: " + ext);
        }

        //парсинг анонсов для расписания
        if (ext.equalsIgnoreCase("air")) {
            try {
                List<Command> commands = Parser.parse(file);
                announcements = getAnnouncements(commands);
            } catch (OnAirParserException e) {
                ButtonType continueButton = new ButtonType("Продолжить", ButtonBar.ButtonData.OK_DONE);
                ButtonType closeButton = new ButtonType("Выход", ButtonBar.ButtonData.CANCEL_CLOSE);
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
        } else {//из файла задания
            announcements = getAnnouncements(file);
        }

        //дата расписания
        Pattern pattern = Pattern.compile(Settings.getParameter(Settings.SettingsKey.SCHEDULE_DATE_FORMAT).replaceAll("[dMy]", "\\\\d"));
        Matcher matcher = pattern.matcher(fileName);
        String date = "";

        if (matcher.find()) {
            date = fileName.substring(matcher.start(), matcher.end());
        }
        outFileName = Settings.getParameter(Settings.SettingsKey.SCHEDULE_PATH) + Settings.getParameter(Settings.SettingsKey.OUT_FILE_NAME).replace("<>", date);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        loader.<MainController>getController().setStage(stage);

        Scene scene = new Scene(root);

        scene.getStylesheets()
                .add("/styles/Styles.css");

        stage.setTitle(
                "Генератор файла задания для анонсера");
        stage.setScene(scene);

        stage.show();
    }

    public static void showErrorAndExit(String message) {
        showMessage("Ошибка", message, Alert.AlertType.ERROR);
        exit(1);
    }

    public static void showMessage(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    //получение анонсов из расписания
    private static SortedSet getAnnouncements(List<Command> commands) {
        List<String> announcerExclusions = new ArrayList<>();
        try {
            announcerExclusions.addAll(getLinesFromFile(HOME_DIRECTORY + Settings.getParameter(Settings.SettingsKey.ANNOUNCER_NOW_EXCLUSIONS)));
        } catch (IOException e) {
            LOG.warn("Cannot read exclusions file: ", e);
            showMessage("Предупреждение", "Не удалось прочитать файл исключений", Alert.AlertType.WARNING);
        }

        String lastMovieFileName = "";
        SortedSet<Announcement> annSet = new TreeSet<>();
        for (Command c : commands) {
            if (Command.CommandKey.MOVIE == c.getCommandKey()) {
                Movie m = (Movie) c;
                if (!lastMovieFileName.equals(m.getFileName())) {
                    if (!isExclusion(announcerExclusions, m.getFileName())) {
                        lastMovieFileName = m.getFileName();
                        Announcement a = new Announcement(m.getFileName(), true);
                        annSet.add(a);
                        paths.add(a.getMoviePath());
                    }
                }
            }
        }
        return annSet;
    }

    //получение анонсов из файла задания
    private static SortedSet getAnnouncements(File announcerTaskFile) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(announcerTaskFile.getAbsolutePath()),
                    Charset.forName("cp1251"));
            SortedSet<Announcement> annSet = new TreeSet<>();
            for (String l : lines) {
                Announcement a = parseAnnouncement(l);
                annSet.add(a);
                paths.add(a.getMoviePath());
            }
            return annSet;
        } catch (IOException e) {
            showErrorAndExit("Не удалось открыть файл: " + announcerTaskFile.getAbsolutePath());
        } catch (AnnouncerTaskGeneratorException e) {
            showErrorAndExit(e.getMessage());
        }
        return null;
    }

    //парсинг строки файла задания. до разделителя путь к файлу, после анонс
    private static Announcement parseAnnouncement(String annString) throws AnnouncerTaskGeneratorException {
        try {
            int separatorIndex = annString.indexOf(separator);
            String movieName = annString.substring(0, separatorIndex).trim();
            String announcement = annString.substring(separatorIndex + 1).trim();
            String annUpper = announcement.toUpperCase();
            boolean isUpper = annUpper.equals(announcement);
            if (isUpper) {
                announcement = announcement.toLowerCase();
            }
            return new Announcement(movieName, announcement, isUpper);
        } catch (Exception e) {
            LOG.error("Cannot parse announcement: ", e);
            throw new AnnouncerTaskGeneratorException("Не верный формат записи анонсов");
        }
    }

    /**
     * Возвращает строки из файла в кодировке cp1251, за исключением пустых строк и строк начинающихся с #
     *
     * @param path путь к файлу
     * @return список строк
     * @throws IOException в случае ошибок ввода/вывода
     */
    private static List<String> getLinesFromFile(String path) throws IOException {
        return Files.readAllLines(Paths.get(path), Charset.forName("cp1251")).stream()
                .filter(s -> !(s.startsWith("#") || s.isEmpty()))
                .collect(toList());
    }

    /**
     * Проверка является ли файл исключением. Между строками действует правило ИЛИ, между путями в строке после разделителей И НЕ. Разделитель - |
     *
     * @param exclusions список исключений
     * @param verifiablePath путь проверяемого файла
     * @return {@code true} если указанный путь является исключением
     */
    private static boolean isExclusion(List<String> exclusions, String verifiablePath) {
        if (!exclusions.isEmpty()) {
            for (String exc : exclusions) {
                String[] parts = exc.split("\\|");
                boolean isExclusion = verifiablePath.startsWith(parts[0].trim());
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        isExclusion = isExclusion && !verifiablePath.startsWith(parts[i].trim());
                    }
                }
                if (isExclusion) {
                    return true;
                }
            }
        }
        return false;
    }
}

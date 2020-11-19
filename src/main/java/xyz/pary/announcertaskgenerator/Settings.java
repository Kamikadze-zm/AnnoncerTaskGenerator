package xyz.pary.announcertaskgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static xyz.pary.announcertaskgenerator.Main.HOME_DIRECTORY;

public class Settings {

    private static final Logger LOG = LogManager.getLogger(Settings.class);

    private static final Map<SettingsKey, String> SETTINGS = new HashMap<>();

    public static final String SETTINGS_FILE = HOME_DIRECTORY + "settings.txt";

    private Settings() {
    }

    /**
     * Загружает настройки из файла settings.txt
     *
     * @throws IOException если не удалось прочитать файл
     * @throws SettingsException в случае отсутствующего параметра или пустого значения
     */
    public static void loadSettings(File file) throws IOException, SettingsException {
        try (InputStreamReader is = new InputStreamReader(new FileInputStream(file), Charset.forName("cp1251"))) {
            Properties p = new Properties();
            p.load(is);
            for (SettingsKey k : SettingsKey.values()) {
                String prop = p.getProperty(k.getKey());
                if (prop == null) {
                    LOG.warn("Not found settings parameter: " + k.getKey());
                    throw new SettingsException("В файле настроек отсутствует параметр: " + k.getKey());
                }
                if (prop.isEmpty()) {
                    LOG.warn("Not found value of settings parameter: " + k.getKey());
                    throw new SettingsException("В файле настроек отсутствует значение для параметра: " + k.getKey());
                }

                SETTINGS.put(k, prop);
            }
        } catch (IOException e) {
            LOG.warn("Load settings exception: ", e);
            throw e;
        }
    }

    /**
     * Получение значения соответствующего параметра настроек
     *
     * @param key - название параметра
     * @return значение параметра
     */
    public static String getParameter(SettingsKey key) {
        return SETTINGS.get(key);
    }

    /**
     * Параметры настроек
     */
    public static enum SettingsKey {

        /**
         * Путь к папке для выбора расписания
         */
        SCHEDULE_PATH("schedule-path"),
        /**
         * Формат даты в названии расписания (yyyy, MM, dd)
         */
        SCHEDULE_DATE_FORMAT("schedule-date-format"),
        /**
         * Название файла задания
         */
        OUT_FILE_NAME("out-file-name"),
        /**
         * Путь к файлу с исключениями для анонсера текущего фильма
         */
        ANNOUNCER_NOW_EXCLUSIONS("announcer-now-exclusions"),
        /**
         * Разделитель названия файла и анонса
         */
        ANNOUNCER_NOW_SEPARATOR("announcer-now-separator");

        private final String key;

        private SettingsKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}

package xyz.pary.announcertaskgenerator;

import java.io.File;
import static javafx.application.Application.launch;

public class Main {

    private static final String DEFAULT_APP_NAME = "AnnouncerTaskGenerator";

    public static final String HOME_DIRECTORY = System.getProperty("user.home") + File.separator
            + "Documents" + File.separator + getAppName() + File.separator;

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(App.class, args);
    }

    public static void exit(int status) {
        System.exit(status);
    }

    private static String getAppName() {
        String path = Main.class
                .getProtectionDomain().getCodeSource().getLocation().toString();
        int index = path.lastIndexOf("/");
        if (index != -1) {
            String name = path.substring(index + 1);
            if (name.endsWith(".jar")) {
                index = name.lastIndexOf(".");
                name = name.substring(0, index);
                return name.replaceAll("(-\\d\\.\\d)(\\.\\d)?", "");
            }
        }
        return DEFAULT_APP_NAME;
    }
}

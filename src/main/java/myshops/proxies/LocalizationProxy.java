package myshops.proxies;

import myshops.Config;
import myshops.utils.Constants;
import mytown.core.Localization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LocalizationProxy {
    private static Logger logger = LogManager.getLogger("MyShops.Localization");
    private static Localization localization;

    /**
     * Loads the {@link Localization}. First tries to load it from config/MyTown/Localization, then the classpath, then loads en_US in its place
     */
    public static void load() {
        try {
            InputStream is = null;

            File file = new File(Constants.CONFIG_FOLDER + "/localization/" + Config.localization + ".lang");
            if (file.exists()) {
                is = new FileInputStream(file);
            }
            if (is == null) {
                is = LocalizationProxy.class.getResourceAsStream("/localization/" + Config.localization + ".lang");
            }
            if (is == null) {
                is = LocalizationProxy.class.getResourceAsStream("/localization/en_US.lang");
                logger.warn("Reverting to en_US.lang because {} does not exist!", Config.localization + ".lang");
            }

            LocalizationProxy.localization = new Localization(new InputStreamReader(is));
            LocalizationProxy.localization.load();
        } catch (Exception ex) {
            logger.warn("Failed to load localization!", ex);
        }
    }

    public static Localization getLocalization() {
        if(localization == null)
            load();
        return LocalizationProxy.localization;
    }
}

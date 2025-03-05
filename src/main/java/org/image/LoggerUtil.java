package org.image;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtil.class.getName());

    /**
     * Logs the selected image's path.
     *
     * @param imagePath The path of the selected image.
     */
    public static void logSelectedImage(String imagePath) {
        LOGGER.log(Level.INFO, "Opened original file: {0}", imagePath);
    }

    /**
     * Logs the provided URL.
     *
     * @param url The URL to be logged.
     */
    public static void logURL(String url) {
        LOGGER.log(Level.INFO, "Parsed URL: {0}", url);
    }

}

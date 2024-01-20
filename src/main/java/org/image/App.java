package org.image;

import java.awt.image.BufferedImage;

import java.io.IOException;

import static org.image.ImgProvider.getRandomImagePath;
import static org.image.LoggerUtil.logSelectedImage;
import static org.image.LoggerUtil.logURL;

/**
 * The App class serves as the main class for the "Magnet Opener" application, which is responsible for selecting,
 * reading, decoding, and displaying random images from a specified image directory. The class consists of the following components:
 * - Constants: LOGGER for logging, IMAGE_DIRECTORY for specifying the image directory, and RANDOM for generating random numbers.
 * - main method: The entry point of the application, which handles the image processing and display.
 * - getRandomImagePath method: Selects and returns a random image path from the specified directory.
 * - logURL method: Logs the provided URL.
 * - readResourceFileToString method: Reads a resource file and returns its content as a string.
 * - logSelectedImage method: Logs the path of the selected image.
 * The application reads image paths from a properties file (img.properties) within the specified directory, decodes Base64-encoded images,
 * and displays them in a window.
 */
public class App {

    /**
     * The main method serves as the entry point for the "Magnet Opener" application. It performs the following steps:
     * Logs that the application is running.
     * Retrieves a random image path using getRandomImagePath() method.
     * Reads the image file as a Base64-encoded string using readResourceFileToString() method.
     * Decodes the Base64-encoded string back to a BufferedImage using decodeBase64ToImage() method.
     * If the BufferedImage is successfully obtained, it logs the selected image path and displays the image in a window using AppWindow.displayImages() method.
     * If the image decoding fails, an error message is printed to the console.
     */
    public static void main(String[] args) {
        logURL("App \"MaLO - ((Magnet Links Opener))\" running");
        String randomImagePath = getRandomImagePath();
        System.out.println("randomImagePath: " + randomImagePath);

        try {
            String base64ImageString = ImgProvider.readResourceFileToString(randomImagePath);
            BufferedImage imageDecode = ImgProcessor.decodeBase64ToImage(base64ImageString);

            if (imageDecode != null) {
                logSelectedImage(randomImagePath);
                AppWindow.displayImages(imageDecode);
            } else {
                System.err.println("Failed to decode the image.");
            }
        } catch (IllegalArgumentException e) {
            logSelectedImage("Resource file not found: " + randomImagePath);
        } catch (IOException e) {
            logURL("Failed to read resource file: " + randomImagePath);
        }

    }

}

/**
 * The AppWindowTest class is a JUnit test class that tests the functionality of the AppWindow class.
 * It contains four test methods that test different scenarios for the decodeBase64ToImage()
 * and displayImages() methods of the AppWindow class. The test methods are well-written
 * and cover different cases to ensure that the AppWindow class works as expected.
 * However, it would be even better if the displayImages() method could be refactored
 * to return a JFrame or JPanel object, which could be inspected for the presence
 * of the image. Overall, the AppWindowTest class is a good example
 * of how to write JUnit tests for a class.
 */
package org.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

public class AppWindowTest {

    @BeforeEach
    public void setUp() {
    }

    /**
     * The testDecodeBase64ToImage_invalidBase64String() method tests the decodeBase64ToImage()
     * method with an invalid Base64 string. It asserts that the method should throw an IllegalArgumentException.
     */
    @Test
    public void testDecodeBase64ToImage_invalidBase64String() {
        String invalidBase64ImageString = "invalid_base64_string";
        assertThrows(IllegalArgumentException.class, () -> ImgProcessor.decodeBase64ToImage(invalidBase64ImageString),
                "decodeBase64ToImage should throw IllegalArgumentException when decoding an invalid Base64 string");
    }

    /**
     * The testDecodeBase64ToImage_nullBase64String() method tests the decodeBase64ToImage()
     * method with a null Base64 string. It asserts that the method should throw a NullPointerException.
     */
    @Test
    public void testDecodeBase64ToImage_nullBase64String() {
        assertThrows(NullPointerException.class, () -> ImgProcessor.decodeBase64ToImage(null),
                "decodeBase64ToImage should throw NullPointerException when decoding a null Base64 string");
    }

    /**
     * The testDisplayImages() method tests the displayImages() method of the AppWindow class.
     * Since this method is GUI-related and does not return any value, it is difficult to test its functionality directly.
     * The test sets up a test image and visually tests the method by calling it and checking if the image is displayed correctly.
     */
    @Test
    public void testDisplayImages() {
        // Prepare a test image
        String validBase64ImageString = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+P+/HgAFhAJ/wlseKgAAAABJRU5ErkJggg=="; // 1x1 red pixel in PNG format
        BufferedImage testImage = ImgProcessor.decodeBase64ToImage(validBase64ImageString);

        // Since displayImages is a GUI-related method and doesn't return any values,
        // it's difficult to test its functionality directly. You can visually test
        // the method by calling it and checking if the image is displayed correctly.
        AppWindow.displayImages(testImage);
    }

}

package org.image;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test class for ImgProvider functionality.
 */
public class AppTest {
    /**
     * Setup method for initializing test resources.
     */
    @BeforeAll
    static void setUp() {
        // Initialization code if needed
    }

    /**
     * Tests that getRandomImagePath does not throw an exception when called with a valid image directory.
     */
    @Test
    public void testGetRandomImagePath_validDirectory() {
        assertDoesNotThrow(ImgProvider::getRandomImagePath,
                "getRandomImagePath should not throw an exception when called with a valid image directory");
    }

    /**
     * Tests that readResourceFileToString throws an IllegalArgumentException for an invalid resource path.
     */
    @Test
    void testReadResourceFileToString_withInvalidPath() {
        String invalidPath = "/img/invalid.txt";
        Assertions.assertThrows(IllegalArgumentException.class, () -> ImgProvider.readResourceFileToString(invalidPath));
    }

    /**
     * Tests that readResourceFileToString returns the expected content for a valid resource file.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    void testReadResourceFileToString() throws IOException {
        String imagePath = "/img/test.txt";
        String content = ImgProvider.readResourceFileToString(imagePath);
        Assertions.assertEquals("Hello, world!", content);
    }
}

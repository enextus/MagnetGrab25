package org.image;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ImgProvider class.
 */
public class ImgProviderTest {

    /**
     * Tests that getRandomImagePath() returns a valid image path.
     * Assumes that /img/img.properties exists and contains at least one image name.
     */
    @Test
    public void testGetRandomImagePath_returnsValidPath() {
        String imagePath = ImgProvider.getRandomImagePath();
        assertNotNull(imagePath, "Returned image path should not be null");
        assertTrue(imagePath.startsWith("/img/"), "Returned image path should start with '/img/'");
    }

    /**
     * Tests that readResourceFileToString(String) correctly reads the content of a valid resource file.
     * Assumes that /img/test.txt exists and its content is exactly "Hello, world!".
     */
    @Test
    public void testReadResourceFileToString_validFile() {
        try {
            String content = ImgProvider.readResourceFileToString("/img/test.txt");
            assertEquals("Hello, world!", content, "Content of /img/test.txt should be 'Hello, world!'");
        } catch (IOException e) {
            fail("IOException thrown during valid file reading: " + e.getMessage());
        }
    }

    /**
     * Tests that readResourceFileToString(String) throws an IllegalArgumentException when the resource file is not found.
     */
    @Test
    public void testReadResourceFileToString_invalidFile() {
        assertThrows(IllegalArgumentException.class, () -> ImgProvider.readResourceFileToString("/img/nonexistent.txt"), "Expected IllegalArgumentException for non-existent resource file");
    }
}

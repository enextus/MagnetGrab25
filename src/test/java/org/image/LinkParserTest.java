package org.image;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.awt.Desktop;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LinkParser class.
 */
public class LinkParserTest {

    /**
     * Resets the number of found magnet links before each test.
     */
    @BeforeEach
    public void setUp() {
        LinkParser.resetNumberOfFoundLinks();
    }

    /**
     * Tests that processMagnetLink increments the magnet link counter.
     */
    @Test
    public void testProcessMagnetLink_incrementsCounter() {
        // Create a dummy HTML containing a magnet link
        String html = "<html><body><a href=\"magnet:?xt=urn:btih:abcdef\">Magnet Link</a></body></html>";
        Document doc = Jsoup.parse(html);
        Element magnetLink = doc.select("a[href^=magnet]").first();
        assertNotNull(magnetLink, "Magnet link element should not be null");

        // Call processMagnetLink. This method also attempts to open the link,
        // which might not work in a test environment; ignore exceptions from Desktop actions.
        try {
            LinkParser.processMagnetLink(magnetLink);
        } catch (Exception e) {
            // Exceptions from Desktop actions are ignored
        }

        assertEquals(1, LinkParser.getNumberOfFoundLinks().intValue(),
                "Counter should be incremented to 1 after processing one magnet link");
    }

    /**
     * Tests that resetNumberOfFoundLinks resets the counter back to zero.
     */
    @Test
    public void testResetNumberOfFoundLinks() {
        // Process one magnet link to increase the counter.
        String html = "<html><body><a href=\"magnet:?xt=urn:btih:abcdef\">Magnet Link</a></body></html>";
        Document doc = Jsoup.parse(html);
        Element magnetLink = doc.select("a[href^=magnet]").first();
        try {
            assert magnetLink != null;
            LinkParser.processMagnetLink(magnetLink);
        } catch (Exception e) {
            // Ignore exceptions from Desktop actions
        }
        assertTrue(LinkParser.getNumberOfFoundLinks() > 0,
                "Counter should be greater than 0 before reset");

        // Reset counter and verify
        LinkParser.resetNumberOfFoundLinks();
        assertEquals(0, LinkParser.getNumberOfFoundLinks().intValue(),
                "Counter should be reset to 0");
    }

    /**
     * Tests that parseUrl processes magnet links from a local HTML file.
     * If the test resource is not found, the test is skipped.
     */
    @Test
    public void testParseUrl_processesMagnetLinks() {
        URL resourceUrlObj = getClass().getResource("/test_magnet.html");
        Assumptions.assumeTrue(resourceUrlObj != null, "Test resource /test_magnet.html not found, skipping test.");
        String resourceUrl = resourceUrlObj.toString();
        assertNotNull(resourceUrl, "Test resource /test_magnet.html must exist");

        // Call parseUrl to process magnet links asynchronously.
        LinkParser.parseUrl(resourceUrl);

        // Allow some time for asynchronous tasks to complete.
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            // Ignore interruption in tests.
        }

        // Verify that at least one magnet link was processed.
        assertTrue(LinkParser.getNumberOfFoundLinks() > 0,
                "At least one magnet link should be processed");
    }

    /**
     * Tests that openMagnetLinkInTorrentClient does not throw an exception for an invalid link.
     * The method should catch and log internal exceptions rather than throwing them.
     */
    @Test
    public void testOpenMagnetLinkInTorrentClient_invalidLink() {
        String invalidLink = "invalid_magnet_link";
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            assertDoesNotThrow(() -> LinkParser.openMagnetLinkInTorrentClient(invalidLink, desktop), "openMagnetLinkInTorrentClient should not throw even for an invalid link");
        } else {
            System.out.println("Desktop is not supported; skipping testOpenMagnetLinkInTorrentClient_invalidLink.");
        }
    }

}

/**
 * The LinkParser class is responsible for parsing a given URL for magnet links and opening them in the default torrent client
 * installed on the user's system. The class connects to the specified URL and retrieves the page content using Jsoup, selects
 * all magnet links on the page using a CSS selector, and processes each found magnet link. For each magnet link, the class
 * extracts the "href" attribute and opens the link in the default torrent client using the openMagnetLinkInTorrentClient() method.
 * Note that using this program to download illegal content may violate the laws of your country. Ensure that you use this program
 * in compliance with the law.
 */
package org.image;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.Desktop;
import java.io.InputStream;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LinkParser {
    private static final int NUMBER_OF_THREADS = 8;
    private static Integer numberOfFoundLinks = 0;
    private static final Logger logger = Logger.getLogger(LinkParser.class.getName());
    private static final String CSS_SELECTOR_MAGNET = "a[href^=magnet]";

    /*
      Initializes the logger configuration for the program by reading the "logging.properties" file
      located in the root of the classpath.
     */
    static {
        try {
            InputStream configFile = LinkParser.class.getResourceAsStream("/logging.properties");
            if (configFile == null) {
                throw new FileNotFoundException("File \"logging.properties\" not found!");
            }
            LogManager.getLogManager().readConfiguration(configFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load the logger configuration file", e);
        }
    }

    /**
     * Increments the counter for the number of found magnet links.
     */
    synchronized static void incrementNumberOfFoundLinks() {
        numberOfFoundLinks++;
    }

    /**
     * Returns the number of magnet links that have been found so far.
     *
     * @return the number of found magnet links
     */
    public static Integer getNumberOfFoundLinks() {
        return numberOfFoundLinks;
    }

    private static volatile boolean isSearching = true;

    /**
     * Processes a magnet link by incrementing the counter for the number of found magnet links,
     * extracting the "href" attribute from the link element, printing the link and number of found
     * links to the console, logging the link, adding the magnet link to a text area, and opening
     * the magnet link in the default torrent client.
     *
     * @param magnetLink the element representing the magnet link to be processed
     */
    static void processMagnetLink(Element magnetLink) {
        // Increment the counter for the number of found magnet links
        incrementNumberOfFoundLinks();

        // Extracting the "href" attribute from the link element
        String link = magnetLink.attr("href");
        System.out.println(getNumberOfFoundLinks() + ": " + "Link found: " + link);

        // For the logfile
        logger.log(Level.INFO, getNumberOfFoundLinks() + ": " + "Link found: " + link);

        // Add magnet link to text area
        AppWindow.addMagnetLinkToTextArea(getNumberOfFoundLinks() + ": " + link);

        // Opening the magnet link in the default torrent client
        openMagnetLinkInTorrentClient(link, Desktop.getDesktop());
    }

    public static void resetNumberOfFoundLinks() {
        numberOfFoundLinks = 0;
    }

    /**
     * Parses the given URL for magnet links and opens them in the default torrent client installed on the user's system.
     * This method connects to the specified URL and retrieves the page content using Jsoup. It then selects all magnet
     * links on the page using a CSS selector. For each found magnet link, the method extracts the "href" attribute
     * and opens the link in the default torrent client using the openMagnetLinkInTorrentClient() method.
     * It is important to note that using this program to download illegal content may violate the laws
     * of your country. Ensure that you use this program in compliance with the law.
     *
     * @param url The URL to be parsed for magnet links
     */
    public static void parseUrl(String url) {
        isSearching = true;
        try {
            // Настройка SSL для обхода проверки сертификатов
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Настройка HostnameVerifier для принятия всех имен хостов
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Устанавливаем соединение с помощью Jsoup
            Document doc = Jsoup.connect(url).get();
            Elements magnetLinks = doc.select(CSS_SELECTOR_MAGNET);

            ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

            for (Element magnetLink : magnetLinks) {
                if (!isSearching) {
                    executorService.shutdownNow();
                    break;
                }
                executorService.submit(() -> processMagnetLink(magnetLink));
            }

            if (isSearching) {
                executorService.shutdown();
            }

/*        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while connecting to the URL", e);
            e.printStackTrace();*/
        } catch (Exception e) {
        logger.log(Level.SEVERE, "An error occurred while connecting to the URL", e);
        // Removed printStackTrace(); relying on logger instead.
    }

}

    /**
     * Opens the given magnet link in the default torrent client installed on the user's system.
     * This method uses the java.awt.Desktop class to open the magnet link in the default torrent client.
     * It first checks if the Desktop class is supported on the current platform, and then checks if the
     * BROWSE action is supported. If both conditions are met, it proceeds to open the magnet link as a URI
     * using the browse() method. In most cases, this will trigger the default torrent client to open and
     * start downloading the linked content.
     * It is important to note that using this program to download illegal content may violate the laws
     * of your country. Ensure that you use this program in compliance with the law.
     *
     * @param magnetLink The magnet link to be opened in the default torrent client
     */
    static void openMagnetLinkInTorrentClient(String magnetLink, Desktop desktop) {
        try {
            // Checking if the BROWSE action is supported by the Desktop class on the current platform
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                // Creating a URI from the magnet link
                URI magnetURI = new URI(magnetLink);
                // Opening the magnet link in the default torrent client
                desktop.browse(magnetURI);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to open the magnet link. Check if a default application is set to handle magnet links.", e);
        }
    }

}

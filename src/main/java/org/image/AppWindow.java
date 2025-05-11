package org.image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.*;

/**
 * Main application window class for the 25H1MagnetGrabber application.
 * <p>
 * Responsible for initializing and displaying the GUI, including image previews,
 * URL input, magnet link listing, and an optional log viewer.
 * <p>
 * This class provides methods to parse URLs, display images, and center the window
 * on the monitor where the application was launched based on the current mouse position.
 */
public class AppWindow {

    // -----------------------------------------------------------------------------------------------------------------
    // Constants for UI configuration
    // -----------------------------------------------------------------------------------------------------------------

    /** Window title including app name, version, and copyright. */
    private static final String FRAME =
            "25H1MagnetGrabber 2025 * MaLO © Magnet Links Opener 2023, 25H1MagnetGrabber 2025";

    /** Background color for image preview panel (black). */
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    /** Text color for link listings and counters (gold). */
    private static final Color TEXT_COLOR = new Color(255, 215, 0);

    /** Layout spacings and dimensions. */
    private static final int NULLIS = 0;
    private static final int HORIZONTAL_SPACING = 2;
    private static final int VERTICAL_SPACING = 10;
    private static final Dimension RIGID_AREA_DIMENSION_HORIZONTAL =
            new Dimension(HORIZONTAL_SPACING, NULLIS);
    private static final Dimension RIGID_AREA_DIMENSION_VERTICAL =
            new Dimension(NULLIS, VERTICAL_SPACING);

    /** URL input field configuration. */
    private static final int URL_FIELD_COLUMNS = 5;
    private static final int URL_PANEL_WIDTH = 300;

    /** Font settings for labels. */
    private static final String FONT_NAME = "Arial";
    private static final int FONT_SIZE = 53;

    /** Text area rows/columns for magnet link list. */
    private static final int TEXT_AREA_ROWS = 10;
    private static final int TEXT_AREA_COLUMNS = 33;

    /** Timer delay in milliseconds for refreshing link count. */
    private static final int TIMER_DELAY = 150;

    /** Default URLs for quick actions. */
    private static final String DEFAULT_URL = "https://xxxtor.com/";
    private static final String TOP_URL     = "https://xxxtor.com/top/";

    /** Button labels and messages. */
    private static final String NEW_BUTTON_LABEL      = "New";
    private static final String TOP_BUTTON_LABEL      = "TOP";
    private static final String OK_BUTTON_TEXT        = "OK";
    private static final String CLEAR_BUTTON_TEXT     = "Clear";
    private static final String ERROR_DIALOG_TITLE    = "Issue!";
    private static final String INVALID_URL_MESSAGE   = "Invalid URL. Please enter a valid URL.";
    private static final String ERROR_MESSAGE_URL_SYNTAX    = "URL could not be parsed. Please check the URL.";
    private static final String ERROR_MESSAGE_URL_MALFORMED = "URL is malformed. Please check the URL.";
    private static final String CONTEXT_MENU_PASTE    = "Paste";

    // -----------------------------------------------------------------------------------------------------------------
    // UI components and state
    // -----------------------------------------------------------------------------------------------------------------

    /** TextField for entering URLs. */
    private static final JTextField urlField = new JTextField();

    /** TextArea for listing found magnet links. */
    private static JTextArea magnetLinksTextArea;

    /** Label showing the running count of found links. */
    private static JLabel numberLabel =
            new JLabel(Integer.toString(LinkParser.getNumberOfFoundLinks()));

    /** Separate window for displaying logs. */
    private static LogWindow logWindow;

    /** Logger for this class. */
    private static final Logger logger = Logger.getLogger(AppWindow.class.getName());

    // -----------------------------------------------------------------------------------------------------------------
    // Static initializer: right-click context menu on urlField
    // -----------------------------------------------------------------------------------------------------------------

    static {
        urlField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }
            /** Show a "Paste" popup menu on right-click. */
            private void showContextMenu(MouseEvent e) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem paste = new JMenuItem(CONTEXT_MENU_PASTE);
                paste.addActionListener(ev -> urlField.paste());
                menu.add(paste);
                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Displays the main window, initializes L&F, builds UI, centers it on the
     * monitor containing the mouse pointer, and attaches logging.
     *
     * @param colorImage BufferedImage to preview.
     */
    public static void displayImages(BufferedImage colorImage) {
        SwingUtilities.invokeLater(() -> {
            configureLookAndFeel();

            JFrame frame = createMainFrame();
            JPanel centerPanel = createCenterPanel(colorImage);
            JPanel textAreaPanel = createTextAreaPanel();

            // Add "Show Log" button
            JButton showLog = new JButton("Show Log");
            showLog.addActionListener(e -> logWindow.setVisible(true));
            frame.add(showLog, BorderLayout.NORTH);

            frame.add(centerPanel, BorderLayout.CENTER);
            frame.add(textAreaPanel, BorderLayout.SOUTH);

            frame.pack();
            centerOnMouseScreen(frame);
            frame.setVisible(true);

            startTimer();
            urlField.requestFocusInWindow();

            // Prepare log window
            logWindow = new LogWindow();
            logWindow.setVisible(false);

            // Redirect java.util.logging to logWindow
            Logger root = Logger.getLogger("");
            TextAreaHandler tah = new TextAreaHandler(logWindow);
            tah.setFormatter(new SimpleFormatter());
            root.addHandler(tah);
            root.setLevel(Level.INFO);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private helper methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates the main application frame with title, close operation, layout,
     * and non-resizable setting.
     */
    private static JFrame createMainFrame() {
        JFrame frame = new JFrame(FRAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        return frame;
    }

    /**
     * Creates the panel combining the counter and input button panel.
     */
    private static JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createNumberPanel());
        panel.add(Box.createRigidArea(RIGID_AREA_DIMENSION_VERTICAL));
        panel.add(createInputPanel());
        return panel;
    }

    /**
     * Creates the input panel with URL field and action buttons (OK, New, TOP, Clear).
     */
    private static JPanel createInputPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // Top row: URL + OK
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        top.add(createUrlPanel());
        top.add(Box.createRigidArea(RIGID_AREA_DIMENSION_HORIZONTAL));
        top.add(createOkButton());

        // Bottom row: New, TOP, Clear
        JPanel bot = new JPanel();
        bot.setOpaque(false);
        bot.setLayout(new BoxLayout(bot, BoxLayout.X_AXIS));
        bot.add(createSearchNewButton());
        bot.add(Box.createRigidArea(RIGID_AREA_DIMENSION_HORIZONTAL));
        bot.add(createSearchTopButton());
        bot.add(Box.createRigidArea(RIGID_AREA_DIMENSION_HORIZONTAL));
        bot.add(createClearButton());

        p.add(top);
        p.add(Box.createRigidArea(RIGID_AREA_DIMENSION_VERTICAL));
        p.add(bot);

        return p;
    }

    /** Builds the central image preview panel and overlays the button panel. */
    private static JPanel createCenterPanel(BufferedImage img) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        JLabel imgLabel = new JLabel(new ImageIcon(ImgProcessor.scaleImageForPreview(img)));
        panel.add(imgLabel, BorderLayout.CENTER);
        imgLabel.setLayout(new BorderLayout());
        imgLabel.add(createButtonPanel(), BorderLayout.CENTER);
        return panel;
    }

    /** Builds the magnet link counter panel. */
    private static JPanel createNumberPanel() {
        numberLabel = new JLabel(Integer.toString(LinkParser.getNumberOfFoundLinks()));
        numberLabel.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        numberLabel.setForeground(TEXT_COLOR);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(""));
        panel.add(numberLabel);
        return panel;
    }

    /** Builds the scrollable text area panel for magnet link list. */
    private static JPanel createTextAreaPanel() {
        magnetLinksTextArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);
        magnetLinksTextArea.setEditable(false);
        magnetLinksTextArea.setForeground(TEXT_COLOR);
        magnetLinksTextArea.setBackground(BACKGROUND_COLOR);
        JScrollPane scroll = new JScrollPane(magnetLinksTextArea);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.NORTH);
        return panel;
    }

    /** Builds the URL entry panel with fixed width. */
    private static JPanel createUrlPanel() {
        urlField.setColumns(URL_FIELD_COLUMNS);
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel(""));
        panel.add(urlField);
        Dimension dim = new Dimension(URL_PANEL_WIDTH, urlField.getPreferredSize().height);
        panel.setMaximumSize(dim);
        panel.setPreferredSize(dim);
        return panel;
    }

    /** Creates the OK button which triggers URL parsing. */
    private static JButton createOkButton() {
        JButton b = new JButton(OK_BUTTON_TEXT);
        b.addActionListener(e -> enterUrl());
        return b;
    }

    /** Creates the 'New' button to load default URL then parse. */
    private static JButton createSearchNewButton() {
        JButton b = new JButton(NEW_BUTTON_LABEL);
        b.addActionListener(e -> {
            urlField.setText(DEFAULT_URL);
            enterUrl();
        });
        return b;
    }

    /** Creates the 'TOP' button to load top URL then parse. */
    private static JButton createSearchTopButton() {
        JButton b = new JButton(TOP_BUTTON_LABEL);
        b.addActionListener(e -> {
            urlField.setText(TOP_URL);
            enterUrl();
        });
        return b;
    }

    /** Creates the 'Clear' button to reset links and clear fields. */
    private static JButton createClearButton() {
        JButton b = new JButton(CLEAR_BUTTON_TEXT);
        b.addActionListener(e -> {
            LinkParser.resetNumberOfFoundLinks();
            urlField.setText("");
            magnetLinksTextArea.setText("");
            urlField.requestFocusInWindow();
        });
        return b;
    }

    /**
     * Reads URL from input, logs it, validates syntax, and invokes parsing.
     * Shows error dialogs on invalid or malformed URLs.
     */
    private static void enterUrl() {
        String urlString = urlField.getText();
        LoggerUtil.logURL(urlString);
        try {
            URI uri = new URI(urlString);
            if (uri.isAbsolute() && uri.getScheme() != null) {
                URL url = uri.toURL();
                LinkParser.parseUrl(url.toString());
            } else {
                JOptionPane.showMessageDialog(null, INVALID_URL_MESSAGE,
                        ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(null, ERROR_MESSAGE_URL_SYNTAX,
                    ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(null, ERROR_MESSAGE_URL_MALFORMED,
                    ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Appends a found magnet link to the text area, if non-empty. */
    public static void addMagnetLinkToTextArea(String link) {
        if (link != null && !link.isEmpty()) {
            magnetLinksTextArea.append(link + "\n");
        }
    }

    /** Starts a Swing Timer to update the link counter label periodically. */
    private static void startTimer() {
        new Timer(TIMER_DELAY, e ->
                numberLabel.setText(Integer.toString(LinkParser.getNumberOfFoundLinks()))
        ).start();
    }

    /**
     * Centers the window on the screen containing the current mouse pointer.
     * Falls back to centering on the primary screen if pointer info is unavailable.
     */
    private static void centerOnMouseScreen(Window w) {
        PointerInfo pi = MouseInfo.getPointerInfo();
        if (pi == null) {
            w.setLocationRelativeTo(null);
            return;
        }
        Point mouse = pi.getLocation();
        GraphicsDevice[] devices =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (GraphicsDevice gd : devices) {
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.contains(mouse)) {
                int x = bounds.x + (bounds.width - w.getWidth()) / 2;
                int y = bounds.y + (bounds.height - w.getHeight()) / 2;
                w.setLocation(x, y);
                return;
            }
        }
        // Fallback if pointer outside all screens
        w.setLocationRelativeTo(null);
    }

    /** Applies cross-platform (Metal) look and feel; logs a warning on failure. */
    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to set the look and feel.", e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Inner classes for logging
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Secondary window to display application logs in real time.
     */
    public static class LogWindow extends JFrame {
        private final JTextArea logTextArea;

        public LogWindow() {
            super("Log Window");
            setSize(800, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            logTextArea = new JTextArea();
            logTextArea.setEditable(false);
            logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(logTextArea);
            add(scrollPane, BorderLayout.CENTER);
        }

        /** Thread-safe append of a formatted log message. */
        public void appendLog(String message) {
            SwingUtilities.invokeLater(() -> {
                logTextArea.append(message + "\n");
                logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
            });
        }
    }

    /**
     * Custom java.util.logging.Handler that pipes formatted log records into a LogWindow.
     */
    public static class TextAreaHandler extends Handler {
        private final LogWindow logWindow;

        public TextAreaHandler(LogWindow logWindow) {
            this.logWindow = logWindow;
        }

        @Override
        public void publish(LogRecord record) {
            if (isLoggable(record)) {
                String msg = getFormatter().format(record);
                logWindow.appendLog(msg);
            }
        }

        @Override public void flush() { /* no-op */ }
        @Override public void close() throws SecurityException { /* no-op */ }
    }
}

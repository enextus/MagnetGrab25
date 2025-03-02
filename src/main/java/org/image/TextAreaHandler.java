package org.image;

import java.util.logging.*;

public class TextAreaHandler extends Handler {
    private final LogWindow logWindow;

    public TextAreaHandler(LogWindow logWindow) {
        this.logWindow = logWindow;
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            // Filter out logs from java.awt packages
            String loggerName = record.getLoggerName();
            if (loggerName != null && loggerName.startsWith("java.awt")) {
                return; // Skip these logs
            }
            String message = getFormatter().format(record);
            logWindow.appendLog(message);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}

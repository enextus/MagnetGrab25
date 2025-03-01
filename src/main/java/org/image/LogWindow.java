package org.image;

import javax.swing.*;
import java.awt.*;

public class LogWindow extends JFrame {
    private final JTextArea logTextArea;

    public LogWindow() {
        super("Окно лога"); // Заголовок окна
        setSize(800, 600); // Размер окна
        setLocationRelativeTo(null); // Центрирование окна
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Скрытие окна при закрытии

        // Создание текстового поля для лога
        logTextArea = new JTextArea();
        logTextArea.setEditable(false); // Запрет редактирования текста
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Моноширинный шрифт для читаемости

        // Добавление прокрутки
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Метод для добавления сообщений в лог
    public void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message + "\n"); // Добавление сообщения с новой строкой
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength()); // Автопрокрутка вниз
        });
    }
}

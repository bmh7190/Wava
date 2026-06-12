package wava.view;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import wava.model.JitEvent;

public class LogPanel extends JPanel {
    private final JTextArea logArea;

    public LogPanel() {
        super(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setRows(8);

        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(event -> clear());

        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(clearButton, BorderLayout.SOUTH);
    }

    public void appendInfo(String message) {
        appendLine("[INFO] " + message);
    }

    public void appendJitEvent(JitEvent event) {
        appendLine("[JIT] " + event.formatLogMessage());
    }

    private void appendLine(String message) {
        logArea.append(message + System.lineSeparator());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clear() {
        logArea.setText("");
    }
}

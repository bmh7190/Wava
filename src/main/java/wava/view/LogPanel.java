package wava.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import wava.model.JitEvent;

public class LogPanel extends JPanel {
    private final JTextArea logArea;
    private final JTextField logPathField;
    private final JTextField filterField;
    private final JButton applyButton;
    private final JButton browseButton;
    private final JPanel settingsPanel;

    public LogPanel() {
        super(new BorderLayout(0, 6));
        UiStyle.applyPanelStyle(this, "JIT Log");

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setRows(8);
        logArea.setLineWrap(false);

        logPathField = new JTextField("logs/jit.log");
        filterField = new JTextField();
        applyButton = new JButton("Apply JIT Settings");
        browseButton = new JButton("Browse");
        browseButton.addActionListener(event -> chooseLogFile());

        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(event -> clear());

        settingsPanel = createSettingsPanel();
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

    public void setApplySettingsAction(ActionListener listener) {
        applyButton.addActionListener(listener);
    }

    public Path getLogPath() {
        return Path.of(logPathField.getText().trim());
    }

    public String getFilterText() {
        return filterField.getText();
    }

    public void setLogPath(Path path) {
        logPathField.setText(path.toString());
    }

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UiStyle.applyPanelStyle(panel, "JIT Settings");

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        formPanel.setOpaque(false);
        formPanel.add(createPathPanel());
        formPanel.add(createFilterPanel());
        formPanel.add(applyButton);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createPathPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("JIT Log"), BorderLayout.WEST);
        panel.add(logPathField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("Filter"), BorderLayout.WEST);
        panel.add(filterField, BorderLayout.CENTER);
        return panel;
    }

    private void chooseLogFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(getLogPath().toFile());
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            setLogPath(chooser.getSelectedFile().toPath());
        }
    }
}

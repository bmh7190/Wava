package wava.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import wava.model.jit.JitEvent;
import wava.model.jit.JitFilterPreset;

public class LogPanel extends JPanel {
    private final JTextArea logArea;
    private final JTextField logPathField;
    private final JComboBox<JitFilterPreset> filterPresetComboBox;
    private final JTextField filterField;
    private final JButton applyButton;
    private final JButton browseButton;
    private final JPanel settingsPanel;
    private String currentTargetFilterText;

    public LogPanel() {
        super(new BorderLayout(0, 6));
        UiStyle.applyPanelStyle(this, "JIT Log");

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setRows(8);
        logArea.setLineWrap(false);
        UiStyle.applyTextAreaStyle(logArea);

        logPathField = new JTextField("logs/jit.log");
        filterPresetComboBox = new JComboBox<>(JitFilterPreset.values());
        filterField = new JTextField();
        UiStyle.applyTextFieldStyle(logPathField);
        UiStyle.applyComboBoxStyle(filterPresetComboBox);
        UiStyle.applyTextFieldStyle(filterField);
        applyButton = new JButton("Apply JIT Settings");
        browseButton = new JButton("Browse");
        currentTargetFilterText = "";
        UiStyle.applyPrimaryButtonStyle(applyButton);
        UiStyle.applyButtonStyle(browseButton);
        browseButton.addActionListener(event -> chooseLogFile());
        filterPresetComboBox.addActionListener(event -> applyFilterPreset());
        filterField.getDocument().addDocumentListener(new FilterTextListener());

        JButton clearButton = new JButton("Clear Log");
        UiStyle.applyButtonStyle(clearButton);
        clearButton.addActionListener(event -> clear());

        settingsPanel = createSettingsPanel();
        JScrollPane scrollPane = new JScrollPane(logArea);
        UiStyle.applyScrollPaneStyle(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
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

    public void setCurrentTargetFilter(String filterText) {
        currentTargetFilterText = filterText;
        if (getFilterPreset() == JitFilterPreset.CURRENT_TARGET) {
            setFilterText(filterText);
        }
    }

    public void setFilterPreset(JitFilterPreset preset) {
        filterPresetComboBox.setSelectedItem(preset);
    }

    public JitFilterPreset getFilterPreset() {
        JitFilterPreset preset = (JitFilterPreset) filterPresetComboBox.getSelectedItem();
        return preset == null ? JitFilterPreset.CUSTOM : preset;
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

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        formPanel.setOpaque(false);
        formPanel.add(createPathPanel());
        formPanel.add(createPresetPanel());
        formPanel.add(createFilterPanel());
        formPanel.add(applyButton);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createPresetPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setOpaque(false);
        panel.add(new JLabel("Preset"), BorderLayout.WEST);
        panel.add(filterPresetComboBox, BorderLayout.CENTER);
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

    private void applyFilterPreset() {
        JitFilterPreset preset = getFilterPreset();
        if (preset == JitFilterPreset.CUSTOM) {
            return;
        }
        if (preset.usesCurrentTarget()) {
            setFilterText(currentTargetFilterText);
            return;
        }
        setFilterText(preset.getFilterText());
    }

    private void setFilterText(String filterText) {
        filterField.putClientProperty("updatingFilterPreset", Boolean.TRUE);
        filterField.setText(filterText);
        filterField.putClientProperty("updatingFilterPreset", Boolean.FALSE);
    }

    private class FilterTextListener implements DocumentListener {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent event) {
            markCustomFilter();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent event) {
            markCustomFilter();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent event) {
            markCustomFilter();
        }

        private void markCustomFilter() {
            if (Boolean.TRUE.equals(filterField.getClientProperty("updatingFilterPreset"))) {
                return;
            }
            filterPresetComboBox.setSelectedItem(JitFilterPreset.CUSTOM);
        }
    }
}

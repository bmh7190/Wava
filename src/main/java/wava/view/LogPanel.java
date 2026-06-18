package wava.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import wava.model.jit.JitEvent;
import wava.model.jit.JitFilterSuggestion;
import wava.model.jit.JitFilterPreset;

public class LogPanel extends JPanel {
    private final JTextArea logArea;
    private final JTextField logPathField;
    private final JComboBox<JitFilterPreset> filterPresetComboBox;
    private final JTextField filterField;
    private final DefaultListModel<JitFilterSuggestion> suggestionListModel;
    private final JList<JitFilterSuggestion> suggestionList;
    private final JButton applyButton;
    private final JButton browseButton;
    private final JButton useSuggestionButton;
    private final JPanel logPathPanel;
    private final JPanel filterPanel;
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
        suggestionListModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionListModel);
        UiStyle.applyTextFieldStyle(logPathField);
        UiStyle.applyComboBoxStyle(filterPresetComboBox);
        UiStyle.applyTextFieldStyle(filterField);
        applyButton = new JButton("Apply Filter");
        browseButton = new JButton("Browse");
        useSuggestionButton = new JButton("Use Suggestion");
        currentTargetFilterText = "";
        UiStyle.applyPrimaryButtonStyle(applyButton);
        UiStyle.applyButtonStyle(browseButton);
        UiStyle.applyButtonStyle(useSuggestionButton);
        UiStyle.applyListStyle(suggestionList);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        browseButton.addActionListener(event -> chooseLogFile());
        filterPresetComboBox.addActionListener(event -> applyFilterPreset());
        filterField.getDocument().addDocumentListener(new FilterTextListener());
        useSuggestionButton.addActionListener(event -> useSelectedSuggestion());
        suggestionList.addMouseListener(new SuggestionMouseListener());

        JButton clearButton = new JButton("Clear Log");
        UiStyle.applyButtonStyle(clearButton);
        clearButton.addActionListener(event -> clear());

        logPathPanel = createLogPathPanel();
        filterPanel = createFilterSettingsPanel();
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

    public void setFilterSuggestions(List<JitFilterSuggestion> suggestions) {
        suggestionListModel.clear();
        for (JitFilterSuggestion suggestion : suggestions) {
            suggestionListModel.addElement(suggestion);
        }
    }

    public int getFilterSuggestionCount() {
        return suggestionListModel.size();
    }

    public void selectFilterSuggestion(int index) {
        suggestionList.setSelectedIndex(index);
    }

    public void useSelectedFilterSuggestion() {
        useSelectedSuggestion();
    }

    public JitFilterPreset getFilterPreset() {
        JitFilterPreset preset = (JitFilterPreset) filterPresetComboBox.getSelectedItem();
        return preset == null ? JitFilterPreset.CUSTOM : preset;
    }

    public void setLogPath(Path path) {
        logPathField.setText(path.toString());
    }

    public JPanel getLogPathPanel() {
        return logPathPanel;
    }

    public JPanel getFilterPanel() {
        return filterPanel;
    }

    private JPanel createLogPathPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UiStyle.applyPanelStyle(panel, "JIT Log Path");

        JPanel formPanel = new JPanel(new GridLayout(1, 1, 0, 4));
        formPanel.setOpaque(false);
        formPanel.add(createPathPanel());

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createFilterSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UiStyle.applyPanelStyle(panel, "JIT Filter");

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        formPanel.setOpaque(false);
        formPanel.add(createPresetPanel());
        formPanel.add(createFilterPanel());
        formPanel.add(applyButton);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(createSuggestionPanel(), BorderLayout.CENTER);
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

    private JPanel createSuggestionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(new JLabel("Suggestions"), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(suggestionList);
        UiStyle.applyScrollPaneStyle(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(useSuggestionButton, BorderLayout.SOUTH);
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

    private void useSelectedSuggestion() {
        JitFilterSuggestion suggestion = suggestionList.getSelectedValue();
        if (suggestion == null) {
            return;
        }
        filterPresetComboBox.setSelectedItem(JitFilterPreset.CUSTOM);
        setFilterText(suggestion.getFilterText());
    }

    private class SuggestionMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getClickCount() >= 2) {
                useSelectedSuggestion();
            }
        }
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

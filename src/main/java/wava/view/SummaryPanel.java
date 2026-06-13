package wava.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import wava.model.jfr.JfrAvailabilityStatus;
import wava.model.jfr.JfrEventSummary;
import wava.model.jfr.JfrRecordingStatus;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitLogStatus;
import wava.model.jit.JitMethodCount;
import wava.model.monitor.MonitorState;
import wava.model.process.JavaProcessInfo;

public class SummaryPanel extends JPanel {
    private static final int SECTION_GAP = 8;
    private static final int MAX_TOP_METHODS = 3;

    private final JLabel stateLabel;
    private final JLabel noticeLabel;
    private final JLabel jitLogLabel;
    private final JLabel jitEventsLabel;
    private final JLabel jitTopMethodsLabel;
    private final JLabel jfrAvailabilityLabel;
    private final JLabel jfrRecordingLabel;
    private final JLabel jfrEventsLabel;
    private final JLabel jfrFileLabel;

    public SummaryPanel() {
        super(new BorderLayout(0, 6));
        UiStyle.applyPanelStyle(this, "Summary");

        stateLabel = createValueLabel();
        noticeLabel = createNoticeLabel();
        jitLogLabel = createValueLabel();
        jitEventsLabel = createValueLabel();
        jitTopMethodsLabel = createValueLabel();
        jfrAvailabilityLabel = createValueLabel();
        jfrRecordingLabel = createValueLabel();
        jfrEventsLabel = createValueLabel();
        jfrFileLabel = createValueLabel();

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
        resetSummaryValues();
        showMessage("No monitoring data.");
    }

    public void showState(MonitorState state) {
        stateLabel.setText("Monitor State: " + state.getLabel());
    }

    public void showMessage(String message) {
        noticeLabel.setText(message);
    }

    public void showSelectedProcess(JavaProcessInfo process) {
        if (process == null) {
            resetSummaryValues();
            showMessage("No process selected.");
            return;
        }
        showMessage("Selected: " + process.getDisplayName());
    }

    public void showEventSummary(
            JavaProcessInfo process,
            JitLogStatus jitLogStatus,
            String jitFilterText,
            JfrAvailabilityStatus jfrStatus,
            JfrRecordingStatus jfrRecordingStatus,
            JfrEventSummary jfrEventSummary,
            JitEventSummary jitSummary) {
        if (process == null) {
            showSelectedProcess(process);
            return;
        }

        updateHeader(process);
        updateJit(jitLogStatus, jitFilterText, jitSummary);
        updateJfr(jfrStatus, jfrRecordingStatus, jfrEventSummary);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 0));
        panel.setOpaque(false);
        stateLabel.setForeground(UiStyle.MUTED_TEXT);
        noticeLabel.setForeground(UiStyle.MUTED_TEXT);
        panel.add(stateLabel, BorderLayout.WEST);
        panel.add(noticeLabel, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane createScrollPane() {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(createSectionPanel("JIT",
                new Field("Log", jitLogLabel),
                new Field("Events", jitEventsLabel),
                new Field("Top", jitTopMethodsLabel)));
        contentPanel.add(Box.createVerticalStrut(SECTION_GAP));
        contentPanel.add(createSectionPanel("JFR",
                new Field("Runtime", jfrAvailabilityLabel),
                new Field("Recording", jfrRecordingLabel),
                new Field("Events", jfrEventsLabel),
                new Field("File", jfrFileLabel)));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createSectionPanel(String title, Field... fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UiStyle.PANEL_BACKGROUND);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(UiStyle.BORDER),
                new EmptyBorder(6, 8, 6, 8)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 5, 0);

        JLabel titleLabel = createSectionTitleLabel(title);
        panel.add(titleLabel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(1, 0, 1, 8);
        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            constraints.gridy = index + 1;
            constraints.gridx = 0;
            constraints.weightx = 0.0;
            constraints.fill = GridBagConstraints.NONE;
            JLabel nameLabel = createNameLabel(field.name);
            panel.add(nameLabel, constraints);

            constraints.gridx = 1;
            constraints.weightx = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            panel.add(field.valueLabel, constraints);
        }
        return panel;
    }

    private JLabel createSectionTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UiStyle.TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    private JLabel createNameLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UiStyle.MUTED_TEXT);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setForeground(UiStyle.TEXT);
        return label;
    }

    private JLabel createNoticeLabel() {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.RIGHT);
        return label;
    }

    private void resetSummaryValues() {
        jitLogLabel.setText("-");
        jitLogLabel.setToolTipText(null);
        jitEventsLabel.setText("-");
        jitTopMethodsLabel.setText("-");
        jitTopMethodsLabel.setToolTipText(null);
        jfrAvailabilityLabel.setText("-");
        jfrRecordingLabel.setText("-");
        jfrEventsLabel.setText("-");
        jfrFileLabel.setText("-");
        jfrFileLabel.setToolTipText(null);
    }

    private void updateHeader(JavaProcessInfo process) {
        setCompactText(noticeLabel, process.getDisplayName(), 80);
    }

    private void updateJit(JitLogStatus status, String filterText, JitEventSummary summary) {
        String logText = status.getStateLabel() + " - " + compactPath(status.getLogPath());
        jitLogLabel.setText(logText);
        jitLogLabel.setToolTipText("Path: " + status.getLogPath()
                + " / Filter: " + formatFilterText(filterText)
                + " / Detail: " + status.getDetail());
        jitEventsLabel.setText(summary.getTotalEventCount()
                + " total / " + summary.getMatchedEventCount() + " shown");
        setCompactText(jitTopMethodsLabel, formatTopMethods(summary), 72);
    }

    private void updateJfr(
            JfrAvailabilityStatus availabilityStatus,
            JfrRecordingStatus recordingStatus,
            JfrEventSummary eventSummary) {
        jfrAvailabilityLabel.setText(availabilityStatus.getStateLabel());
        jfrAvailabilityLabel.setToolTipText(availabilityStatus.getDetail());
        jfrRecordingLabel.setText(recordingStatus.getStateLabel());
        jfrRecordingLabel.setToolTipText(recordingStatus.getDetail());

        if (!eventSummary.isAvailable()) {
            jfrEventsLabel.setText("Unavailable");
            jfrEventsLabel.setToolTipText(eventSummary.getDetail());
            jfrFileLabel.setText("-");
            jfrFileLabel.setToolTipText(null);
            return;
        }
        jfrEventsLabel.setText(eventSummary.getTotalEventCount()
                + " total, " + eventSummary.getCompilationEventCount()
                + " compilation, " + eventSummary.getGcEventCount() + " GC");
        setCompactText(jfrFileLabel, compactPath(eventSummary.getSourcePath()), 72);
    }

    private String formatTopMethods(JitEventSummary summary) {
        if (!summary.hasMatchedEvents()) {
            return "No matching events";
        }

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (JitMethodCount method : summary.getTopMethods()) {
            if (count >= MAX_TOP_METHODS) {
                break;
            }
            if (count > 0) {
                builder.append(", ");
            }
            builder.append(method.getMethodName()).append(" (").append(method.getCount()).append(")");
            count++;
        }
        return builder.toString();
    }

    private String compactPath(Path path) {
        if (path == null) {
            return "-";
        }
        Path fileName = path.getFileName();
        return fileName == null ? path.toString() : fileName.toString();
    }

    private void setCompactText(JLabel label, String value, int maxLength) {
        if (value == null || value.isBlank()) {
            label.setText("-");
            label.setToolTipText(null);
            return;
        }
        if (value.length() <= maxLength) {
            label.setText(value);
            label.setToolTipText(null);
            return;
        }
        label.setText(value.substring(0, maxLength - 3) + "...");
        label.setToolTipText(value);
    }

    private String formatFilterText(String filterText) {
        if (filterText == null || filterText.isBlank()) {
            return "<none>";
        }
        return filterText;
    }

    private static class Field {
        private final String name;
        private final JLabel valueLabel;

        private Field(String name, JLabel valueLabel) {
            this.name = name;
            this.valueLabel = valueLabel;
        }
    }
}

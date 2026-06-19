package wava.view;

import java.awt.BorderLayout;
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
import wava.model.jit.JitMethodSummary;
import wava.model.monitor.MonitorState;
import wava.model.process.JavaProcessInfo;

public class SummaryPanel extends JPanel {
    private static final int MAX_TOP_METHODS = 3;

    private final JLabel jitStatusLabel;
    private final JLabel jfrStatusLabel;
    private final JLabel eventCountsLabel;

    public SummaryPanel() {
        super(new BorderLayout(0, 6));
        UiStyle.applyPanelStyle(this, "Summary");

        jitStatusLabel = createValueLabel();
        jfrStatusLabel = createValueLabel();
        eventCountsLabel = createValueLabel();

        add(createScrollPane(), BorderLayout.CENTER);
        resetSummaryValues();
        showMessage("No monitoring data.");
    }

    public void showState(MonitorState state) {
    }

    public void showMessage(String message) {
    }

    public void showSelectedProcess(JavaProcessInfo process) {
        if (process == null) {
            resetSummaryValues();
            showMessage("No process selected.");
            return;
        }
        showMessage("");
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

        updateJit(jitLogStatus, jitFilterText, jitSummary);
        updateJfr(jfrStatus, jfrRecordingStatus, jfrEventSummary);
        updateEventCounts(jitSummary, jfrEventSummary);
    }

    private JScrollPane createScrollPane() {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(createSectionPanel("Event Status",
                new Field("JIT", jitStatusLabel),
                new Field("JFR", jfrStatusLabel),
                new Field("Counts", eventCountsLabel)));
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        UiStyle.applyScrollPaneStyle(scrollPane);
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
        label.setFont(UiStyle.APP_FONT_BOLD);
        return label;
    }

    private JLabel createNameLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UiStyle.MUTED_TEXT);
        label.setFont(UiStyle.APP_FONT);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setForeground(UiStyle.TEXT);
        label.setFont(UiStyle.APP_FONT);
        return label;
    }

    private void resetSummaryValues() {
        jitStatusLabel.setText("-");
        jitStatusLabel.setToolTipText(null);
        jfrStatusLabel.setText("-");
        jfrStatusLabel.setToolTipText(null);
        eventCountsLabel.setText("-");
        eventCountsLabel.setToolTipText(null);
    }

    private void updateJit(JitLogStatus status, String filterText, JitEventSummary summary) {
        setCompactText(jitStatusLabel,
                status.getStateLabel() + " / " + summary.getMatchedEventCount() + " shown",
                34);
        jitStatusLabel.setToolTipText(formatJitTooltip(status, filterText, summary));
    }

    private void updateJfr(
            JfrAvailabilityStatus availabilityStatus,
            JfrRecordingStatus recordingStatus,
            JfrEventSummary eventSummary) {
        String recordingText = eventSummary.isAvailable() ? "Saved" : recordingStatus.getStateLabel();
        setCompactText(jfrStatusLabel,
                availabilityStatus.getStateLabel() + " / " + recordingText,
                34);

        String sourceText = eventSummary.isAvailable()
                ? " / Source: " + compactPath(eventSummary.getSourcePath())
                : "";
        jfrStatusLabel.setToolTipText("Availability: " + availabilityStatus.getDetail()
                + " / Recording: " + recordingStatus.getDetail()
                + sourceText
                + " / Summary: " + eventSummary.getDetail());
    }

    private void updateEventCounts(JitEventSummary jitSummary, JfrEventSummary jfrSummary) {
        if (!jfrSummary.isAvailable()) {
            setCompactText(eventCountsLabel,
                    "JIT " + jitSummary.getMatchedEventCount() + ", JFR -",
                    36);
            eventCountsLabel.setToolTipText("JIT total: " + jitSummary.getTotalEventCount()
                    + " / JIT shown: " + jitSummary.getMatchedEventCount()
                    + " / JFR: " + jfrSummary.getDetail());
            return;
        }
        setCompactText(eventCountsLabel,
                "JIT " + jitSummary.getMatchedEventCount()
                        + ", JFR " + jfrSummary.getTotalEventCount()
                        + ", GC " + jfrSummary.getGcEventCount(),
                36);
        eventCountsLabel.setToolTipText("JIT total: " + jitSummary.getTotalEventCount()
                + " / JIT shown: " + jitSummary.getMatchedEventCount()
                + " / JFR total: " + jfrSummary.getTotalEventCount()
                + " / JFR compilation: " + jfrSummary.getCompilationEventCount()
                + " / JFR GC: " + jfrSummary.getGcEventCount());
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

    private String formatJitTooltip(
            JitLogStatus status,
            String filterText,
            JitEventSummary summary) {
        StringBuilder builder = new StringBuilder("<html>");
        appendTooltipLine(builder, "Path", status.getLogPath().toString());
        appendTooltipLine(builder, "Filter", formatFilterText(filterText));
        appendTooltipLine(builder, "Total", String.valueOf(summary.getTotalEventCount()));
        appendTooltipLine(builder, "Shown", String.valueOf(summary.getMatchedEventCount()));
        appendTooltipLine(builder, "Top", formatTopMethods(summary));
        appendTooltipLine(builder, "Detail", status.getDetail());
        if (!summary.getTopMethodSummaries().isEmpty()) {
            builder.append("<br><b>Method summary</b>");
            for (JitMethodSummary methodSummary : summary.getTopMethodSummaries()) {
                builder.append("<br>")
                        .append(escapeHtml(methodSummary.getMethodName()))
                        .append(" - events ")
                        .append(methodSummary.getEventCount())
                        .append(", latest ")
                        .append(escapeHtml(methodSummary.getLatestLevelLabel()))
                        .append(", time ")
                        .append(escapeHtml(methodSummary.formatElapsedRange()));
                if (methodSummary.isMadeNotEntrantObserved()) {
                    builder.append(", made not entrant");
                }
            }
        }
        builder.append("</html>");
        return builder.toString();
    }

    private void appendTooltipLine(StringBuilder builder, String label, String value) {
        builder.append("<b>")
                .append(escapeHtml(label))
                .append(":</b> ")
                .append(escapeHtml(value))
                .append("<br>");
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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

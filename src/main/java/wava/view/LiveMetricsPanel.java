package wava.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import wava.model.metric.MetricSample;
import wava.model.process.JavaProcessInfo;
import wava.model.warmup.WarmupStabilityPoint;
import wava.model.warmup.WarmupSummary;

public class LiveMetricsPanel extends JPanel {
    private static final int SECTION_GAP = 8;

    private final JLabel cpuLabel;
    private final JLabel heapLabel;
    private final JLabel gcLabel;
    private final JLabel warmupCpuLabel;
    private final JLabel warmupHeapLabel;
    private final JLabel stabilityLabel;

    public LiveMetricsPanel() {
        super(new BorderLayout(0, 6));
        UiStyle.applyPanelStyle(this, "Live Metrics");

        cpuLabel = createValueLabel();
        heapLabel = createValueLabel();
        gcLabel = createValueLabel();
        warmupCpuLabel = createValueLabel();
        warmupHeapLabel = createValueLabel();
        stabilityLabel = createValueLabel();

        add(createContentPanel(), BorderLayout.CENTER);
        clear();
    }

    public void showSelectedProcess(JavaProcessInfo process) {
        if (process == null) {
            clearValues();
        }
    }

    public void showMonitoringData(
            JavaProcessInfo process,
            MetricSample latestSample,
            WarmupSummary summary,
            WarmupStabilityPoint stabilityPoint) {
        showSelectedProcess(process);
        cpuLabel.setText(formatValue(latestSample.getCpuUsagePercent()) + " %");
        heapLabel.setText(formatHeapValue(latestSample));
        gcLabel.setText(formatGcValue(latestSample));
        updateWarmup(summary, stabilityPoint);
    }

    public void clear() {
        clearValues();
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createSectionPanel("Latest",
                new Field("CPU", cpuLabel),
                new Field("Heap", heapLabel),
                new Field("GC", gcLabel)));
        panel.add(Box.createVerticalStrut(SECTION_GAP));
        panel.add(createSectionPanel("Warm-up",
                new Field("CPU", warmupCpuLabel),
                new Field("Heap", warmupHeapLabel),
                new Field("Stable", stabilityLabel)));
        return panel;
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
            panel.add(createNameLabel(field.name), constraints);

            constraints.gridx = 1;
            constraints.weightx = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            panel.add(field.valueLabel, constraints);
        }
        return panel;
    }

    private void updateWarmup(WarmupSummary summary, WarmupStabilityPoint stabilityPoint) {
        if (!summary.isAvailable()) {
            warmupCpuLabel.setText("Collecting (" + summary.getSampleCount() + ")");
            warmupCpuLabel.setToolTipText(null);
            warmupHeapLabel.setText("Collecting");
            warmupHeapLabel.setToolTipText(null);
        } else {
            String cpuText = compactChangeText(
                    summary.getEarlyAverageCpu(),
                    summary.getLateAverageCpu(),
                    summary.getCpuChange(),
                    "%");
            String heapText = compactChangeText(
                    summary.getEarlyAverageHeap(),
                    summary.getLateAverageHeap(),
                    summary.getHeapChange(),
                    "MB");
            warmupCpuLabel.setText(cpuText);
            warmupCpuLabel.setToolTipText("CPU " + expandedChangeText(
                    summary.getEarlyAverageCpu(),
                    summary.getLateAverageCpu(),
                    summary.getCpuChange(),
                    "%"));
            warmupHeapLabel.setText(heapText);
            warmupHeapLabel.setToolTipText("Heap " + expandedChangeText(
                    summary.getEarlyAverageHeap(),
                    summary.getLateAverageHeap(),
                    summary.getHeapChange(),
                    "MB"));
        }

        if (!stabilityPoint.isAvailable()) {
            stabilityLabel.setText("Not available");
            stabilityLabel.setToolTipText(null);
            return;
        }
        stabilityLabel.setText("S" + stabilityPoint.getSampleIndex()
                + ", range " + formatValue(stabilityPoint.getCpuRangePercent()) + "%");
        stabilityLabel.setToolTipText("Stable point sample " + stabilityPoint.getSampleIndex()
                + ", CPU range " + formatValue(stabilityPoint.getCpuRangePercent())
                + "%, JIT events " + stabilityPoint.getJitEventCount());
    }

    private void clearValues() {
        cpuLabel.setText("-");
        heapLabel.setText("-");
        gcLabel.setText("-");
        warmupCpuLabel.setText("-");
        warmupHeapLabel.setText("-");
        stabilityLabel.setText("-");
        stabilityLabel.setToolTipText(null);
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

    private String formatValue(double value) {
        return String.format("%.2f", value);
    }

    private String formatHeapValue(MetricSample sample) {
        if (!sample.isHeapAvailable()) {
            return "Unavailable";
        }
        return formatValue(sample.getHeapUsedMb()) + " MB";
    }

    private String formatGcValue(MetricSample sample) {
        if (!sample.isGcAvailable()) {
            return "Unavailable";
        }
        return "+" + sample.getGcCountDelta()
                + " collections, +" + sample.getGcTimeDeltaMillis() + " ms";
    }

    private String formatSignedValue(double value) {
        return String.format("%+.2f", value);
    }

    private String compactChangeText(double earlyValue, double lateValue, double changeValue, String unit) {
        return formatValue(earlyValue) + ">" + formatValue(lateValue)
                + " (" + formatSignedValue(changeValue) + unit + ")";
    }

    private String expandedChangeText(double earlyValue, double lateValue, double changeValue, String unit) {
        return "early " + formatValue(earlyValue)
                + unit + ", late " + formatValue(lateValue)
                + unit + ", change " + formatSignedValue(changeValue) + unit;
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

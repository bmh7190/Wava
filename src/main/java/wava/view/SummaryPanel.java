package wava.view;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import wava.model.jfr.JfrAvailabilityStatus;
import wava.model.jfr.JfrRecordingStatus;
import wava.model.process.JavaProcessInfo;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitLogStatus;
import wava.model.jit.JitMethodCount;
import wava.model.metric.MetricSample;
import wava.model.monitor.MonitorState;
import wava.model.process.TargetProcessStatus;
import wava.model.warmup.WarmupStabilityPoint;
import wava.model.warmup.WarmupSummary;

public class SummaryPanel extends JPanel {
    private final JLabel stateLabel;
    private final JTextArea summaryArea;

    public SummaryPanel() {
        super(new BorderLayout(0, 6));
        UiStyle.applyPanelStyle(this, "Summary");

        stateLabel = new JLabel();
        stateLabel.setForeground(UiStyle.MUTED_TEXT);
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setRows(8);

        add(stateLabel, BorderLayout.NORTH);
        add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        showMessage("No monitoring data.");
    }

    public void showState(MonitorState state) {
        stateLabel.setText("Monitor State: " + state.getLabel());
    }

    public void showMessage(String message) {
        summaryArea.setText(message);
    }

    public void showSelectedProcess(JavaProcessInfo process) {
        if (process == null) {
            summaryArea.setText("No process selected.");
            return;
        }
        summaryArea.setText("Selected Process" + System.lineSeparator()
                + "PID: " + process.getPid() + System.lineSeparator()
                + "Name: " + process.getDisplayName());
    }

    public void showMonitoringSummary(
            JavaProcessInfo process,
            TargetProcessStatus targetProcessStatus,
            List<MetricSample> samples,
            WarmupSummary summary,
            WarmupStabilityPoint stabilityPoint,
            JitLogStatus jitLogStatus,
            String jitFilterText,
            JfrAvailabilityStatus jfrStatus,
            JfrRecordingStatus jfrRecordingStatus,
            JitEventSummary jitSummary) {
        if (process == null || samples.isEmpty()) {
            showSelectedProcess(process);
            return;
        }
        MetricSample latestSample = samples.get(samples.size() - 1);
        summaryArea.setText("Monitoring Target" + System.lineSeparator()
                + "PID: " + process.getPid() + System.lineSeparator()
                + "Name: " + process.getDisplayName() + System.lineSeparator()
                + "Target status: " + targetProcessStatus.getLabel() + System.lineSeparator()
                + "Samples: " + samples.size() + System.lineSeparator()
                + "CPU: " + formatValue(latestSample.getCpuUsagePercent()) + " %" + System.lineSeparator()
                + "Heap: " + formatHeapValue(latestSample) + System.lineSeparator()
                + "GC: " + formatGcValue(latestSample) + System.lineSeparator()
                + System.lineSeparator()
                + formatWarmupSummary(summary) + System.lineSeparator()
                + System.lineSeparator()
                + formatStabilityPoint(stabilityPoint) + System.lineSeparator()
                + System.lineSeparator()
                + formatJitLogStatus(jitLogStatus, jitFilterText) + System.lineSeparator()
                + System.lineSeparator()
                + formatJfrStatus(jfrStatus, jfrRecordingStatus) + System.lineSeparator()
                + System.lineSeparator()
                + formatJitSummary(jitSummary));
    }

    private String formatWarmupSummary(WarmupSummary summary) {
        if (!summary.isAvailable()) {
            return "Warm-up Summary" + System.lineSeparator()
                    + "Collect more samples. Current: " + summary.getSampleCount();
        }

        return "Warm-up Summary" + System.lineSeparator()
                + "CPU early avg: " + formatValue(summary.getEarlyAverageCpu()) + " %" + System.lineSeparator()
                + "CPU late avg: " + formatValue(summary.getLateAverageCpu()) + " %" + System.lineSeparator()
                + "CPU change: " + formatSignedValue(summary.getCpuChange()) + " %" + System.lineSeparator()
                + "Heap early avg: " + formatValue(summary.getEarlyAverageHeap()) + " MB" + System.lineSeparator()
                + "Heap late avg: " + formatValue(summary.getLateAverageHeap()) + " MB" + System.lineSeparator()
                + "Heap change: " + formatSignedValue(summary.getHeapChange()) + " MB";
    }

    private String formatStabilityPoint(WarmupStabilityPoint point) {
        if (!point.isAvailable()) {
            return "Warm-up Stability" + System.lineSeparator()
                    + "Estimated stable point: Not available" + System.lineSeparator()
                    + "Samples checked: " + point.getSampleCount();
        }

        return "Warm-up Stability" + System.lineSeparator()
                + "Estimated stable point: sample " + point.getSampleIndex() + System.lineSeparator()
                + "CPU range: " + formatValue(point.getCpuRangePercent()) + " %" + System.lineSeparator()
                + "JIT events in window: " + point.getJitEventCount();
    }

    private String formatJitLogStatus(JitLogStatus status, String filterText) {
        return "JIT Log Status" + System.lineSeparator()
                + "State: " + status.getStateLabel() + System.lineSeparator()
                + "Path: " + status.getLogPath() + System.lineSeparator()
                + "Filter: " + formatFilterText(filterText) + System.lineSeparator()
                + "Detail: " + status.getDetail();
    }

    private String formatJitSummary(JitEventSummary summary) {
        StringBuilder builder = new StringBuilder();
        builder.append("JIT Summary").append(System.lineSeparator())
                .append("Events: ")
                .append(summary.getTotalEventCount())
                .append(" total / ")
                .append(summary.getMatchedEventCount())
                .append(" shown");

        if (!summary.hasMatchedEvents()) {
            return builder.append(System.lineSeparator())
                    .append("No matching JIT events.")
                    .toString();
        }

        builder.append(System.lineSeparator())
                .append("Latest: ")
                .append(summary.getLatestMethodName())
                .append(System.lineSeparator())
                .append("Top methods:");

        int rank = 1;
        for (JitMethodCount method : summary.getTopMethods()) {
            builder.append(System.lineSeparator())
                    .append(rank)
                    .append(". ")
                    .append(method.getMethodName())
                    .append(" (")
                    .append(method.getCount())
                    .append(")");
            rank++;
        }
        return builder.toString();
    }

    private String formatJfrStatus(JfrAvailabilityStatus status, JfrRecordingStatus recordingStatus) {
        return "JFR Status" + System.lineSeparator()
                + "Availability: " + status.getStateLabel() + System.lineSeparator()
                + "Recording: " + recordingStatus.getStateLabel() + System.lineSeparator()
                + "Runtime: " + status.getDetail() + System.lineSeparator()
                + "Recording detail: " + recordingStatus.getDetail();
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
        return "count +" + sample.getGcCountDelta()
                + ", time +" + sample.getGcTimeDeltaMillis() + " ms";
    }

    private String formatSignedValue(double value) {
        return String.format("%+.2f", value);
    }

    private String formatFilterText(String filterText) {
        if (filterText == null || filterText.isBlank()) {
            return "<none>";
        }
        return filterText;
    }
}

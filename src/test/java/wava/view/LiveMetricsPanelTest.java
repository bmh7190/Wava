package wava.view;

import wava.model.metric.MetricSample;
import wava.model.process.JavaProcessInfo;
import wava.model.warmup.WarmupStabilityPoint;
import wava.model.warmup.WarmupSummary;

public class LiveMetricsPanelTest {
    public static void main(String[] args) {
        createLiveMetricsPanel();
        updateLiveMetricsStates();
    }

    private static void createLiveMetricsPanel() {
        LiveMetricsPanel panel = new LiveMetricsPanel();

        assertTrue(panel.getComponentCount() == 2, "component count");
    }

    private static void updateLiveMetricsStates() {
        LiveMetricsPanel panel = new LiveMetricsPanel();

        panel.showSelectedProcess(null);
        panel.showSelectedProcess(new JavaProcessInfo(1234L, "sample.WarmupTarget"));
        panel.showMonitoringData(
                new JavaProcessInfo(1234L, "sample.WarmupTarget"),
                new MetricSample(1000L, 2.5, 64.0, true, true, 1L, 4L),
                WarmupSummary.available(10, 4.0, 2.0, 80.0, 72.0),
                WarmupStabilityPoint.available(10, 5, 5000L, 0.5, 3));
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }
}

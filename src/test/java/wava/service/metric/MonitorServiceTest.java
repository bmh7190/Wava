package wava.service.metric;

import java.util.List;
import wava.model.process.JavaProcessInfo;
import wava.model.metric.MetricSample;

public class MonitorServiceTest {
    public static void main(String[] args) {
        addSamplesInOrder();
        keepSlidingWindow();
        resetClearsSamples();
    }

    private static void addSamplesInOrder() {
        MonitorService service = new MonitorService(new CountingMetricCollector(), 5);
        JavaProcessInfo process = new JavaProcessInfo(10L, "sample.Main");

        service.addSample(process);
        service.addSample(process);

        List<MetricSample> samples = service.getSamples();
        assertEquals(2, samples.size(), "sample count");
        assertEquals(0.0, samples.get(0).getCpuUsagePercent(), "first cpu");
        assertEquals(1.0, samples.get(1).getCpuUsagePercent(), "second cpu");
    }

    private static void keepSlidingWindow() {
        MonitorService service = new MonitorService(new CountingMetricCollector(), 2);
        JavaProcessInfo process = new JavaProcessInfo(10L, "sample.Main");

        service.addSample(process);
        service.addSample(process);
        service.addSample(process);

        List<MetricSample> samples = service.getSamples();
        assertEquals(2, samples.size(), "window size");
        assertEquals(1.0, samples.get(0).getCpuUsagePercent(), "window first cpu");
        assertEquals(2.0, samples.get(1).getCpuUsagePercent(), "window second cpu");
    }

    private static void resetClearsSamples() {
        MonitorService service = new MonitorService(new CountingMetricCollector(), 5);
        JavaProcessInfo process = new JavaProcessInfo(10L, "sample.Main");

        service.addSample(process);
        service.reset();

        assertEquals(0, service.getSamples().size(), "reset size");
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static class CountingMetricCollector implements MetricCollector {
        @Override
        public MetricSample collect(JavaProcessInfo targetProcess, int sampleIndex) {
            return new MetricSample(sampleIndex, sampleIndex, sampleIndex + 10.0);
        }
    }
}

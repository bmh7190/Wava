package wava.service.warmup;

import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.model.warmup.WarmupStabilityPoint;
import wava.service.jit.JitEventFilter;

public class WarmupStabilityAnalyzer {
    private static final int WINDOW_SIZE = 5;
    private static final int MIN_SAMPLE_COUNT = WINDOW_SIZE * 2;
    private static final double CPU_RANGE_THRESHOLD_PERCENT = 2.0;

    public WarmupStabilityPoint analyze(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter filter) {
        if (samples.size() < MIN_SAMPLE_COUNT) {
            return WarmupStabilityPoint.unavailable(samples.size());
        }

        for (int startIndex = WINDOW_SIZE; startIndex <= samples.size() - WINDOW_SIZE; startIndex++) {
            int endIndex = startIndex + WINDOW_SIZE - 1;
            double cpuRange = calculateCpuRange(samples, startIndex, endIndex);
            int currentJitCount = countJitEvents(samples, jitEvents, filter, startIndex, endIndex);
            int previousJitCount = countJitEvents(samples, jitEvents, filter, startIndex - WINDOW_SIZE, startIndex - 1);

            if (cpuRange <= CPU_RANGE_THRESHOLD_PERCENT && currentJitCount <= previousJitCount) {
                MetricSample stableSample = samples.get(startIndex);
                return WarmupStabilityPoint.available(
                        samples.size(),
                        startIndex,
                        stableSample.getTimestampMillis(),
                        cpuRange,
                        currentJitCount);
            }
        }

        return WarmupStabilityPoint.unavailable(samples.size());
    }

    private double calculateCpuRange(List<MetricSample> samples, int startIndex, int endIndex) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int index = startIndex; index <= endIndex; index++) {
            double cpu = samples.get(index).getCpuUsagePercent();
            min = Math.min(min, cpu);
            max = Math.max(max, cpu);
        }
        return max - min;
    }

    private int countJitEvents(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter filter,
            int startIndex,
            int endIndex) {
        long startMillis = samples.get(startIndex).getTimestampMillis();
        long endMillis = samples.get(endIndex).getTimestampMillis();
        int count = 0;
        for (JitEvent event : jitEvents) {
            if (event.getTimestampMillis() >= startMillis
                    && event.getTimestampMillis() <= endMillis
                    && filter.matches(event)) {
                count++;
            }
        }
        return count;
    }
}

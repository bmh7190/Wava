package wava.service.warmup;

import java.util.List;
import wava.model.metric.MetricSample;
import wava.model.warmup.WarmupSummary;

public class WarmupAnalyzer {
    private static final int MIN_SAMPLE_COUNT = 6;
    private static final double SECTION_RATIO = 0.3;

    public WarmupSummary analyze(List<MetricSample> samples) {
        if (samples.size() < MIN_SAMPLE_COUNT) {
            return WarmupSummary.unavailable(samples.size());
        }

        int sectionSize = Math.max(1, (int) Math.floor(samples.size() * SECTION_RATIO));
        List<MetricSample> earlySamples = samples.subList(0, sectionSize);
        List<MetricSample> lateSamples = samples.subList(samples.size() - sectionSize, samples.size());

        return WarmupSummary.available(
                samples.size(),
                averageCpu(earlySamples),
                averageCpu(lateSamples),
                averageHeap(earlySamples),
                averageHeap(lateSamples));
    }

    private double averageCpu(List<MetricSample> samples) {
        return samples.stream()
                .mapToDouble(MetricSample::getCpuUsagePercent)
                .average()
                .orElse(0.0);
    }

    private double averageHeap(List<MetricSample> samples) {
        return samples.stream()
                .filter(MetricSample::isHeapAvailable)
                .mapToDouble(MetricSample::getHeapUsedMb)
                .average()
                .orElse(0.0);
    }
}
